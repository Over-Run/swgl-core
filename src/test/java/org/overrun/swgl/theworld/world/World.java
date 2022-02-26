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

package org.overrun.swgl.theworld.world;

import org.overrun.swgl.theworld.phys.AABB;
import org.overrun.swgl.theworld.world.block.Block;
import org.overrun.swgl.theworld.world.block.Blocks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author squid233
 * @since 0.1.0
 */
public class World {
    /**
     * The blocks.
     * <p>
     * Computation of index for {@code (x, y, z, w, d)}: {@code x+w(yd+z)}
     * </p>
     */
    public final byte[] blocks;
    public final int width;
    public final int height;
    public final int depth;

    public World(int width, int height, int depth) {
        blocks = new byte[width * height * depth];
        this.width = width;
        this.height = height;
        this.depth = depth;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < depth; z++) {
                    blocks[getBlockIndex(x, y, z)] = Blocks.STONE.id;
                }
            }
        }
    }

    public int getBlockIndex(int x, int y, int z) {
        return x + (y * depth + z) * width;
    }

    public boolean isInsideWorld(int x, int y, int z) {
        return x >= 0 && x < width
            && y >= 0 && y < height
            && z >= 0 && z < depth;
    }

    public Block getBlock(int x, int y, int z) {
        if (isInsideWorld(x, y, z))
            return Blocks.getBlock(blocks[getBlockIndex(x, y, z)]);
        return Blocks.AIR;
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
                    if (!block.isAir()) {
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
