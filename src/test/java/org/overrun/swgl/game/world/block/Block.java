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

package org.overrun.swgl.game.world.block;

import org.overrun.swgl.core.util.math.Direction;
import org.overrun.swgl.game.Tesselator;
import org.overrun.swgl.game.phys.AABB;
import org.overrun.swgl.game.world.World;

import java.util.Random;

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

    public int getTexture(Direction face) {
        return texture;
    }

    public boolean isAir() {
        return false;
    }

    public AABB getOutline(int x, int y, int z) {
        var aabb = new AABB();
        aabb.min.set(x, y, z);
        aabb.max.set(x + 1, y + 1, z + 1);
        return aabb;
    }

    public AABB getRayCast(int x, int y, int z) {
        var aabb = new AABB();
        aabb.min.set(x, y, z);
        aabb.max.set(x + 1, y + 1, z + 1);
        return aabb;
    }

    public AABB getCollision(int x, int y, int z) {
        var aabb = new AABB();
        aabb.min.set(x, y, z);
        aabb.max.set(x + 1, y + 1, z + 1);
        return aabb;
    }

    public void tick(World world, int x, int y, int z, Random random) {
    }

    public boolean shouldRenderFace(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isAir();
    }

    public void renderOutline(Tesselator t, int x, int y, int z) {
        float x0 = (float) x - 0.0001f;
        float y0 = (float) y - 0.0001f;
        float z0 = (float) z - 0.0001f;
        float x1 = x + 1.0001f;
        float y1 = y + 1.0001f;
        float z1 = z + 1.0001f;
        t.color(0, 0, 0, 0.5f);
        // -x
        t.vertex(x0, y1, z0).emit();
        t.vertex(x0, y0, z0).emit();
        t.vertex(x0, y0, z1).emit();
        t.vertex(x0, y1, z1).emit();
        // +x
        t.vertex(x1, y1, z1).emit();
        t.vertex(x1, y0, z1).emit();
        t.vertex(x1, y0, z0).emit();
        t.vertex(x1, y1, z0).emit();
        t.index(
            // -x
            0, 1, 1, 2, 2, 3, 3, 0,
            // +x
            4, 5, 5, 6, 6, 7, 7, 4,
            // -z
            7, 0, 6, 1,
            // +z
            3, 4, 2, 5
        );
    }

    public void renderFaceNoTex(Tesselator t, Direction face, int x, int y, int z) {
        float x0 = (float) x;
        float y0 = (float) y;
        float z0 = (float) z;
        float x1 = x + 1.0f;
        float y1 = y + 1.0f;
        float z1 = z + 1.0f;
        t.color(1, 1, 1, 1);
        switch (face) {
            case WEST -> {
                // -x
                t.quadIndex();
                t.normal(-1, 0, 0);
                t.vertex(x0, y1, z0).emit();
                t.vertex(x0, y0, z0).emit();
                t.vertex(x0, y0, z1).emit();
                t.vertex(x0, y1, z1).emit();
            }
            case EAST -> {
                // +x
                t.quadIndex();
                t.normal(1, 0, 0);
                t.vertex(x1, y1, z1).emit();
                t.vertex(x1, y0, z1).emit();
                t.vertex(x1, y0, z0).emit();
                t.vertex(x1, y1, z0).emit();
            }
            case DOWN -> {
                // -y
                t.quadIndex();
                t.normal(0, -1, 0);
                t.vertex(x0, y0, z1).emit();
                t.vertex(x0, y0, z0).emit();
                t.vertex(x1, y0, z0).emit();
                t.vertex(x1, y0, z1).emit();
            }
            case UP -> {
                // +y
                t.quadIndex();
                t.normal(0, 1, 0);
                t.vertex(x0, y1, z0).emit();
                t.vertex(x0, y1, z1).emit();
                t.vertex(x1, y1, z1).emit();
                t.vertex(x1, y1, z0).emit();
            }
            case NORTH -> {
                // -z
                t.quadIndex();
                t.normal(0, 0, -1);
                t.vertex(x1, y1, z0).emit();
                t.vertex(x1, y0, z0).emit();
                t.vertex(x0, y0, z0).emit();
                t.vertex(x0, y1, z0).emit();
            }
            case SOUTH -> {
                // +z
                t.quadIndex();
                t.normal(0, 0, 1);
                t.vertex(x0, y1, z1).emit();
                t.vertex(x0, y0, z1).emit();
                t.vertex(x1, y0, z1).emit();
                t.vertex(x1, y1, z1).emit();
            }
        }
    }

    public void renderFace(Tesselator t, Direction face, int x, int y, int z) {
        float x0 = (float) x;
        float y0 = (float) y;
        float z0 = (float) z;
        float x1 = x + 1.0f;
        float y1 = y + 1.0f;
        float z1 = z + 1.0f;
        t.color(1, 1, 1, 1);
        switch (face) {
            case WEST -> {
                int texture = getTexture(Direction.WEST);
                float u0 = (texture % 16) * 16.0f / 256.0f;
                float v0 = (float) (texture / 16) * 16.0f / 256.0f;
                float u1 = ((texture % 16) * 16.0f + 16.0f) / 256.0f;
                float v1 = ((float) (texture / 16) * 16.0f + 16.0f) / 256.0f;
                // -x
                t.quadIndex();
                t.normal(-1, 0, 0);
                t.tex(u0, v0).vertex(x0, y1, z0).emit();
                t.tex(u0, v1).vertex(x0, y0, z0).emit();
                t.tex(u1, v1).vertex(x0, y0, z1).emit();
                t.tex(u1, v0).vertex(x0, y1, z1).emit();
            }
            case EAST -> {
                int texture = getTexture(Direction.EAST);
                float u0 = (texture % 16) * 16.0f / 256.0f;
                float v0 = (float) (texture / 16) * 16.0f / 256.0f;
                float u1 = ((texture % 16) * 16.0f + 16.0f) / 256.0f;
                float v1 = ((float) (texture / 16) * 16.0f + 16.0f) / 256.0f;
                // +x
                t.quadIndex();
                t.normal(1, 0, 0);
                t.tex(u0, v0).vertex(x1, y1, z1).emit();
                t.tex(u0, v1).vertex(x1, y0, z1).emit();
                t.tex(u1, v1).vertex(x1, y0, z0).emit();
                t.tex(u1, v0).vertex(x1, y1, z0).emit();
            }
            case DOWN -> {
                int texture = getTexture(Direction.DOWN);
                float u0 = (texture % 16) * 16.0f / 256.0f;
                float v0 = (float) (texture / 16) * 16.0f / 256.0f;
                float u1 = ((texture % 16) * 16.0f + 16.0f) / 256.0f;
                float v1 = ((float) (texture / 16) * 16.0f + 16.0f) / 256.0f;
                // -y
                t.quadIndex();
                t.normal(0, -1, 0);
                t.tex(u0, v0).vertex(x0, y0, z1).emit();
                t.tex(u0, v1).vertex(x0, y0, z0).emit();
                t.tex(u1, v1).vertex(x1, y0, z0).emit();
                t.tex(u1, v0).vertex(x1, y0, z1).emit();
            }
            case UP -> {
                int texture = getTexture(Direction.UP);
                float u0 = (texture % 16) * 16.0f / 256.0f;
                float v0 = (float) (texture / 16) * 16.0f / 256.0f;
                float u1 = ((texture % 16) * 16.0f + 16.0f) / 256.0f;
                float v1 = ((float) (texture / 16) * 16.0f + 16.0f) / 256.0f;
                // +y
                t.quadIndex();
                t.normal(0, 1, 0);
                t.tex(u0, v0).vertex(x0, y1, z0).emit();
                t.tex(u0, v1).vertex(x0, y1, z1).emit();
                t.tex(u1, v1).vertex(x1, y1, z1).emit();
                t.tex(u1, v0).vertex(x1, y1, z0).emit();
            }
            case NORTH -> {
                int texture = getTexture(Direction.NORTH);
                float u0 = (texture % 16) * 16.0f / 256.0f;
                float v0 = (float) (texture / 16) * 16.0f / 256.0f;
                float u1 = ((texture % 16) * 16.0f + 16.0f) / 256.0f;
                float v1 = ((float) (texture / 16) * 16.0f + 16.0f) / 256.0f;
                // -z
                t.quadIndex();
                t.normal(0, 0, -1);
                t.tex(u0, v0).vertex(x1, y1, z0).emit();
                t.tex(u0, v1).vertex(x1, y0, z0).emit();
                t.tex(u1, v1).vertex(x0, y0, z0).emit();
                t.tex(u1, v0).vertex(x0, y1, z0).emit();
            }
            case SOUTH -> {
                int texture = getTexture(Direction.SOUTH);
                float u0 = (texture % 16) * 16.0f / 256.0f;
                float v0 = (float) (texture / 16) * 16.0f / 256.0f;
                float u1 = ((texture % 16) * 16.0f + 16.0f) / 256.0f;
                float v1 = ((float) (texture / 16) * 16.0f + 16.0f) / 256.0f;
                // +z
                t.quadIndex();
                t.normal(0, 0, 1);
                t.tex(u0, v0).vertex(x0, y1, z1).emit();
                t.tex(u0, v1).vertex(x0, y0, z1).emit();
                t.tex(u1, v1).vertex(x1, y0, z1).emit();
                t.tex(u1, v0).vertex(x1, y1, z1).emit();
            }
        }
    }

    public void renderAll(Tesselator t, int x, int y, int z) {
        for (var face : Direction.values()) {
            renderFace(t, face, x, y, z);
        }
    }

    public void render(Tesselator t, World world, int x, int y, int z) {
        for (var face : Direction.values()) {
            if (shouldRenderFace(world,
                x + face.getOffsetX(),
                y + face.getOffsetY(),
                z + face.getOffsetZ())) {
                renderFace(t, face, x, y, z);
            }
        }
    }
}
