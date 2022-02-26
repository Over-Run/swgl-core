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

package org.overrun.swgl.theworld.world.block;

import org.overrun.swgl.core.util.math.Direction;
import org.overrun.swgl.theworld.Tesselator;
import org.overrun.swgl.theworld.phys.AABB;
import org.overrun.swgl.theworld.world.World;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Block {
    public final byte id;
    protected int texture;

    public Block(byte id, int texture) {
        this.id = id;
        this.texture = texture;
    }

    public int getTexture() {
        return texture;
    }

    public boolean isAir() {
        return false;
    }

    public AABB getCollision(int x, int y, int z) {
        var aabb = new AABB();
        aabb.min.set(x, y, z);
        aabb.max.set(x + 1, y + 1, z + 1);
        return aabb;
    }

    public boolean shouldRenderFace(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isAir();
    }

    public void renderFace(Tesselator t, Direction face, int x, int y, int z) {
        float x0 = (float) x;
        float y0 = (float) y;
        float z0 = (float) z;
        float x1 = x + 1.0f;
        float y1 = y + 1.0f;
        float z1 = z + 1.0f;
        int texture = getTexture();
        float u0 = (texture % 16) / 256.0f;
        float v0 = ((float) (texture / 16)) / 256.0f;
        float u1 = ((texture % 16) + 16.0f) / 256.0f;
        float v1 = ((float) (texture / 16) + 16.0f) / 256.0f;
        switch (face) {
            case WEST -> {
                // -x
                t.quadIndex();
                t.color(1, 1, 1, 1).tex(u0, v0).vertex(x0, y1, z0).emit();
                t.color(1, 1, 1, 1).tex(u0, v1).vertex(x0, y0, z0).emit();
                t.color(1, 1, 1, 1).tex(u1, v1).vertex(x0, y0, z1).emit();
                t.color(1, 1, 1, 1).tex(u1, v0).vertex(x0, y1, z1).emit();
            }
            case EAST -> {
                // +x
                t.quadIndex();
                t.color(1, 1, 1, 1).tex(u0, v0).vertex(x1, y1, z1).emit();
                t.color(1, 1, 1, 1).tex(u0, v1).vertex(x1, y0, z1).emit();
                t.color(1, 1, 1, 1).tex(u1, v1).vertex(x1, y0, z0).emit();
                t.color(1, 1, 1, 1).tex(u1, v0).vertex(x1, y1, z0).emit();
            }
            case DOWN -> {
                // -y
                t.quadIndex();
                t.color(1, 1, 1, 1).tex(u0, v0).vertex(x0, y0, z1).emit();
                t.color(1, 1, 1, 1).tex(u0, v1).vertex(x0, y0, z0).emit();
                t.color(1, 1, 1, 1).tex(u1, v1).vertex(x1, y0, z0).emit();
                t.color(1, 1, 1, 1).tex(u1, v0).vertex(x1, y0, z1).emit();
            }
            case UP -> {
                // +y
                t.quadIndex();
                t.color(1, 1, 1, 1).tex(u0, v0).vertex(x0, y1, z0).emit();
                t.color(1, 1, 1, 1).tex(u0, v1).vertex(x0, y1, z1).emit();
                t.color(1, 1, 1, 1).tex(u1, v1).vertex(x1, y1, z1).emit();
                t.color(1, 1, 1, 1).tex(u1, v0).vertex(x1, y1, z0).emit();
            }
            case NORTH -> {
                // -z
                t.quadIndex();
                t.color(1, 1, 1, 1).tex(u0, v0).vertex(x1, y1, z0).emit();
                t.color(1, 1, 1, 1).tex(u0, v1).vertex(x1, y0, z0).emit();
                t.color(1, 1, 1, 1).tex(u1, v1).vertex(x0, y0, z0).emit();
                t.color(1, 1, 1, 1).tex(u1, v0).vertex(x0, y1, z0).emit();
            }
            case SOUTH -> {
                // +z
                t.quadIndex();
                t.color(1, 1, 1, 1).tex(u0, v0).vertex(x0, y1, z1).emit();
                t.color(1, 1, 1, 1).tex(u0, v1).vertex(x0, y0, z1).emit();
                t.color(1, 1, 1, 1).tex(u1, v1).vertex(x1, y0, z1).emit();
                t.color(1, 1, 1, 1).tex(u1, v0).vertex(x1, y1, z1).emit();
            }
        }
    }

    public void render(Tesselator t, World world, int x, int y, int z) {
        if (shouldRenderFace(world, x - 1, y, z)) {
            renderFace(t, Direction.WEST, x, y, z);
        }
        if (shouldRenderFace(world, x + 1, y, z)) {
            renderFace(t, Direction.EAST, x, y, z);
        }
        if (shouldRenderFace(world, x, y - 1, z)) {
            renderFace(t, Direction.DOWN, x, y, z);
        }
        if (shouldRenderFace(world, x, y + 1, z)) {
            renderFace(t, Direction.UP, x, y, z);
        }
        if (shouldRenderFace(world, x, y, z - 1)) {
            renderFace(t, Direction.NORTH, x, y, z);
        }
        if (shouldRenderFace(world, x, y, z + 1)) {
            renderFace(t, Direction.SOUTH, x, y, z);
        }
    }
}
