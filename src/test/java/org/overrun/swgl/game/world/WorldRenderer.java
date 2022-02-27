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

import org.joml.Intersectionf;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.game.Frustum;
import org.overrun.swgl.game.HitResult;
import org.overrun.swgl.game.Tesselator;
import org.overrun.swgl.game.phys.AABB;
import org.overrun.swgl.game.world.block.Blocks;
import org.overrun.swgl.game.world.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class WorldRenderer implements IWorldListener, AutoCloseable {
    public static final int MAX_REBUILD_PER_FRAMES = 8;
    public final World world;
    private final Chunk[] chunks;
    public final int xChunks;
    public final int yChunks;
    public final int zChunks;

    public WorldRenderer(World world) {
        this.world = world;
        world.addListener(this);
        xChunks = world.width / Chunk.CHUNK_SIZE;
        yChunks = world.height / Chunk.CHUNK_SIZE;
        zChunks = world.depth / Chunk.CHUNK_SIZE;
        chunks = new Chunk[xChunks * yChunks * zChunks];
        for (int x = 0; x < xChunks; x++) {
            for (int y = 0; y < yChunks; y++) {
                for (int z = 0; z < zChunks; z++) {
                    int x0 = x * Chunk.CHUNK_SIZE;
                    int y0 = y * Chunk.CHUNK_SIZE;
                    int z0 = z * Chunk.CHUNK_SIZE;
                    int x1 = (x + 1) * Chunk.CHUNK_SIZE;
                    int y1 = (y + 1) * Chunk.CHUNK_SIZE;
                    int z1 = (z + 1) * Chunk.CHUNK_SIZE;

                    if (x1 > world.width) {
                        x1 = world.width;
                    }
                    if (y1 > world.height) {
                        y1 = world.height;
                    }
                    if (z1 > world.depth) {
                        z1 = world.depth;
                    }

                    chunks[(x + y * xChunks) * zChunks + z] = new Chunk(world, x0, y0, z0, x1, y1, z1);
                }
            }
        }
    }

    private List<Chunk> getDirtyChunks() {
        List<Chunk> list = null;
        for (var chunk : chunks) {
            if (chunk.isDirty()) {
                if (list == null)
                    list = new ArrayList<>();
                list.add(chunk);
            }
        }
        return list;
    }

    public void updateDirtyChunks(Player player) {
        var list = getDirtyChunks();
        if (list != null) {
            list.sort(new DirtyChunkSorter(player, Frustum.getInstance()));
            for (int i = 0; i < MAX_REBUILD_PER_FRAMES && i < list.size(); i++) {
                list.get(i).rebuild();
            }
        }
    }

    public void renderHit(HitResult hitResult) {
        glLineWidth(2.0f);
        enableBlend();
        blendFunc(GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA);
        var t = Tesselator.getInstance();
        t.enableColor();
        t.disableTexture();
        t.begin(GL_LINES);
        Blocks.STONE.renderOutline(t, hitResult.x(), hitResult.y(), hitResult.z());
        t.flush();
        t.disableColor();
        disableBlend();
        glLineWidth(1.0f);
    }

    public HitResult pick(Player player, Matrix4fc viewMatrix, FpsCamera camera) {
        float r = 4.0f;
        var box = player.aabb.grow(r, r, r);
        int x0 = (int) box.min.x;
        int x1 = (int) (box.max.x + 1.0f);
        int y0 = (int) box.min.y;
        int y1 = (int) (box.max.y + 1.0f);
        int z0 = (int) box.min.z;
        int z1 = (int) (box.max.z + 1.0f);
        float closestDistance = Float.POSITIVE_INFINITY;
        var min = new Vector3f();
        var max = new Vector3f();
        var nearFar = new Vector2f();
        HitResult hitResult = null;
        AABB rayCast;
        var dir = viewMatrix.positiveZ(new Vector3f()).negate();
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    var block = world.getBlock(x, y, z);
                    if (!block.isAir()) {
                        rayCast = block.getRayCast(x, y, z);
                        min.set(rayCast.min);
                        max.set(rayCast.max);
                        if (Intersectionf.intersectRayAab(camera.getPosition(),
                            dir,
                            min,
                            max,
                            nearFar)
                            && nearFar.x < closestDistance) {
                            closestDistance = nearFar.x;
                            hitResult = new HitResult(x, y, z, rayCast.rayCastFacing(camera.getPosition(), dir));
                        }
                    }
                }
            }
        }
        return hitResult;
    }

    public void render(Frustum frustum) {
        for (var chunk : chunks) {
            if (frustum.testAab(chunk.aabb)) {
                chunk.render();
            }
        }
    }

    public void markDirty(int x0, int y0, int z0,
                          int x1, int y1, int z1) {
        x0 /= Chunk.CHUNK_SIZE;
        y0 /= Chunk.CHUNK_SIZE;
        z0 /= Chunk.CHUNK_SIZE;
        x1 /= Chunk.CHUNK_SIZE;
        y1 /= Chunk.CHUNK_SIZE;
        z1 /= Chunk.CHUNK_SIZE;

        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x1 >= xChunks) {
            x1 = xChunks - 1;
        }
        if (y1 >= yChunks) {
            y1 = yChunks - 1;
        }
        if (z1 >= zChunks) {
            z1 = zChunks - 1;
        }

        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                for (int z = z0; z <= z1; z++) {
                    chunks[(x + y * xChunks) * zChunks + z].markDirty();
                }
            }
        }
    }

    @Override
    public void blockChanged(int x, int y, int z) {
        markDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void lightColumnChanged(int x, int z, int y0, int y1) {
        markDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }

    @Override
    public void allChanged() {
        markDirty(0, 0, 0, world.width, world.height, world.depth);
    }

    @Override
    public void close() {
        for (var chunk : chunks) {
            chunk.close();
        }
    }
}
