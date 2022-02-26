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

import org.overrun.swgl.theworld.Tesselator;
import org.overrun.swgl.theworld.phys.AABB;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Chunk implements AutoCloseable {
    public static final int CHUNK_SIZE = 16;
    public final World world;
    public final int x, y, z;
    public final int x0, y0, z0;
    public final int x1, y1, z1;
    public final AABB aabb;
    private boolean dirty = true;
    private final Tesselator tesselator = new Tesselator();

    public Chunk(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        x0 = x * CHUNK_SIZE;
        y0 = y * CHUNK_SIZE;
        z0 = z * CHUNK_SIZE;
        x1 = (x + 1) * CHUNK_SIZE;
        y1 = (y + 1) * CHUNK_SIZE;
        z1 = (z + 1) * CHUNK_SIZE;
        aabb = new AABB();
        aabb.min.set(x0, y0, z0);
        aabb.max.set(x1, y1, z1);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean rebuild() {
        dirty = false;
        var t = tesselator;
        t.begin();
        t.enableColor();
        t.enableTexture();
        int blocks = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    var block = world.getBlock(x, y, z);
                    if (!block.isAir()) {
                        block.render(t, world, x, y, z);
                        ++blocks;
                    }
                }
            }
        }
        return false;
    }

    public void render() {
        tesselator.flush();
    }

    @Override
    public void close() {
        tesselator.close();
    }
}
