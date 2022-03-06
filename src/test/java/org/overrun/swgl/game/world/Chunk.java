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

import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.util.Timer;
import org.overrun.swgl.game.phys.AABB;
import org.overrun.swgl.game.world.entity.PlayerEntity;

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;
import static org.overrun.swgl.core.gl.ims.GLLists.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Chunk implements AutoCloseable {
    public static final int CHUNK_SIZE = 16;
    public final World world;
    public final float x, y, z;
    public final int x0, y0, z0;
    public final int x1, y1, z1;
    public final AABB aabb;
    private boolean dirty = true;
    public double dirtiedTime = 0.0;
    public static int updates = 0;
    private final int lists;
    private static long totalTime = 0L;
    private static int totalUpdates = 0;

    public Chunk(World world,
                 int x0, int y0, int z0,
                 int x1, int y1, int z1) {
        this.world = world;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        x = (x0 + x1) / 2.0f;
        y = (y0 + y1) / 2.0f;
        z = (z0 + z1) / 2.0f;
        aabb = new AABB();
        aabb.min.set(x0, y0, z0);
        aabb.max.set(x1, y1, z1);
        lists = lglGenLists(2);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        if (!dirty)
            dirtiedTime = Timer.getTime();
        dirty = true;
    }

    private void rebuild(int layer) {
        dirty = false;
        ++updates;
        long before = System.nanoTime();
        lglNewList(lists + layer);
        lglBegin(GLDrawMode.TRIANGLES);
        int blocks = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    var block = world.getBlock(x, y, z);
                    if (!block.isAir()) {
                        block.render(world, layer, x, y, z);
                        ++blocks;
                    }
                }
            }
        }
        lglEnd();
        lglEndList();
        long after = System.nanoTime();
        if (blocks > 0) {
            totalTime += after - before;
            ++totalUpdates;
        }
    }

    public void rebuild() {
        rebuild(0);
        rebuild(1);
    }

    public void render(int layer) {
        lglCallList(lists + layer);
    }

    public float distanceSqr(PlayerEntity player) {
        return player.position.distanceSquared(x, y, z);
    }

    @Override
    public void close() {
        lglDeleteLists(lists, 2);
    }
}
