/*
 * MIT License
 *
 * Copyright (c) 2022 Overrun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.overrun.swgl.game.world;

import org.overrun.swgl.core.util.math.Direction;
import org.overrun.swgl.game.phys.AABB;
import org.overrun.swgl.game.world.block.Block;
import org.overrun.swgl.game.world.block.Blocks;
import org.overrun.swgl.game.world.block.IBlockAir;
import org.overrun.swgl.game.world.entity.Entity;
import org.overrun.swgl.game.world.entity.HumanEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author squid233
 * @since 0.1.0
 */
public class World {
    private static final int BLOCK_UPDATE_INTERVAL = 400;
    /**
     * The blocks.
     * <p>
     * Computation of index for {@code (x, y, z, w, d)}: {@code x+w(yd+z)}
     * </p>
     */
    private final byte[] blocks;
    private final int[] lightDepths;
    public final long seed;
    public final int width;
    public final int height;
    public final int depth;
    /**
     * The daytime of the world in ticks.
     * <table>
     * <tr><th>Tick</th><th>Realtime</th></tr>
     * <tr><td>0</td><td>6:00 Morning</td></tr>
     * <tr><td>2000</td><td>8:00 Day</td></tr>
     * <tr><td>6000</td><td>12:00 Noon</td></tr>
     * <tr><td>12000</td><td>18:00 Night</td></tr>
     * <tr><td>18000</td><td>0:00 Midnight</td></tr>
     * </table>
     */
    public int daytimeTick = 0;
    public final Map<UUID, Entity> entities = new LinkedHashMap<>();
    private final Random random;
    private final List<IWorldListener> listeners = new ArrayList<>();
    private int unprocessed = 0;

    public World(long seed, int width, int height, int depth) {
        blocks = new byte[width * height * depth];
        lightDepths = new int[width * depth];
        this.seed = seed;
        this.width = width;
        this.height = height;
        this.depth = depth;
        random = new Random(seed);
        if (!load()) {
            generateMap();
            calcLightDepths(0, 0, width, depth);
        }

        for (int i = 0; i < 100; i++) {
            var human = new HumanEntity(this, 128, 0, 128);
            human.resetPos();
            entities.put(human.uuid, human);
        }
    }

    public boolean load() {
        if (Files.notExists(Path.of("world.dat")))
            return false;
        try (var fis = new FileInputStream("world.dat");
             var gis = new GZIPInputStream(fis);
             var dis = new DataInputStream(gis)) {
            dis.readFully(blocks);
            calcLightDepths(0, 0, width, depth);
            for (var listener : listeners) {
                listener.allChanged();
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void save() {
        try (var fos = new FileOutputStream("world.dat");
             var gos = new GZIPOutputStream(fos);
             var dos = new DataOutputStream(gos)) {
            dos.write(blocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateMap() {
        int[] map0 = SimplexNoiseTerrain.generateTerrain(random.nextFloat(),
            width,
            height,
            depth,
            null);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                int y0 = map0[x + z * width];
                blocks[getBlockIndex(x, y0, z)] = Blocks.GRASS_BLOCK.id;
                for (int y = 0; y < y0; y++) {
                    int index = getBlockIndex(x, y, z);
                    if (y == 0) {
                        blocks[index] = Blocks.BEDROCK.id;
                        continue;
                    }
                    // 10 layers dirt
                    if (y < y0 - 10) {
                        blocks[index] = Blocks.STONE.id;
                    } else {
                        blocks[index] = Blocks.DIRT.id;
                    }
                }
            }
        }
    }

    public void calcLightDepths(int x0, int z0, int w, int d) {
        for (int x = x0; x < x0 + w; x++) {
            for (int z = z0; z < z0 + d; z++) {
                int oldDepth = lightDepths[x + z * width];

                int y = height - 1;
                while (y > 0 && !isLightBlocker(x, y, z)) {
                    --y;
                }

                lightDepths[x + z * width] = y;
                if (oldDepth != y) {
                    int yl0 = Math.min(oldDepth, y);
                    int yl1 = Math.max(oldDepth, y);

                    for (var listener : listeners) {
                        listener.lightColumnChanged(x, z, yl0, yl1);
                    }
                }
            }
        }
    }

    public void addListener(IWorldListener listener) {
        listeners.add(listener);
    }

    public boolean isLightBlocker(int x, int y, int z) {
        var block = getBlock(x, y, z);
        return !(block instanceof IBlockAir) && block.blocksLight();
    }

    public boolean isLit(int x, int y, int z) {
        return isOutsideWorld(x, y, z) || y >= lightDepths[x + z * width];
    }

    public int getBlockIndex(int x, int y, int z) {
        return x + (y * depth + z) * width;
    }

    public boolean isOutsideWorld(int x, int y, int z) {
        return !isInsideWorld(x, y, z);
    }

    public boolean isInsideWorld(int x, int y, int z) {
        return x >= 0 && x < width
               && y >= 0 && y < height
               && z >= 0 && z < depth;
    }

    public boolean setBlock(int x, int y, int z, Block block) {
        if (isOutsideWorld(x, y, z))
            return false;
        if (getBlock(x, y, z) == block)
            return false;
        blocks[getBlockIndex(x, y, z)] = block.id;
        calcLightDepths(x, z, 1, 1);
        for (var listener : listeners) {
            listener.blockChanged(x, y, z);
        }
        return true;
    }

    public Block getBlock(int x, int y, int z) {
        if (isInsideWorld(x, y, z))
            return Blocks.getBlock(blocks[getBlockIndex(x, y, z)]);
        return Blocks.AIR;
    }

    public void addEntity(UUID uuid, Entity entity) {
        entities.put(uuid, entity);
    }

    public void removeEntity(UUID uuid) {
        entities.remove(uuid);
    }

    public void tick() {
        ++daytimeTick;

        var it = entities.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            var entity = e.getValue();
            entity.tick();
            if (entity.removed) {
                it.remove();
            }
        }

        unprocessed += width * height * depth;
        int ticks = unprocessed / BLOCK_UPDATE_INTERVAL;
        unprocessed -= ticks * BLOCK_UPDATE_INTERVAL;

        for (int i = 0; i < ticks; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int z = random.nextInt(depth);
            var block = getBlock(x, y, z);
            if (!(block instanceof IBlockAir)) {
                block.tick(this, x, y, z, random);
            }
        }
    }

    public boolean canPlaceOn(int x, int y, int z, Block block, Direction face) {
        return block.canPlaceOn(getBlock(x, y, z), this, x, y, z, face);
    }

    public boolean isReplaceable(int x, int y, int z) {
        if (isInsideWorld(x, y, z))
            return getBlock(x, y, z).isReplaceable();
        return false;
    }

    public List<AABB> getCubes(AABB origin) {
        // Get the round 36 boxes
        var lst = new ArrayList<AABB>();
        int x0 = (int) origin.min.x;
        int y0 = (int) origin.min.y;
        int z0 = (int) origin.min.z;
        int x1 = (int) (origin.max.x + 1.0f);
        int y1 = (int) (origin.max.y + 1.0f);
        int z1 = (int) (origin.max.z + 1.0f);

        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }

        if (x1 > width) {
            x1 = width;
        }
        if (y1 > height) {
            y1 = height;
        }
        if (z1 > depth) {
            z1 = depth;
        }

        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    var block = getBlock(x, y, z);
                    if (!(block instanceof IBlockAir)) {
                        var aabb = block.getCollision(x, y, z);
                        if (aabb != null) {
                            lst.add(aabb);
                        }
                    }
                }
            }
        }
        return lst;
    }
}
