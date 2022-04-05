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
import org.overrun.swgl.game.atlas.BlockAtlas;
import org.overrun.swgl.game.phys.AABB;
import org.overrun.swgl.game.world.World;

import java.util.Random;

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;
import static org.overrun.swgl.core.util.math.Numbers.divSafeFast;
import static org.overrun.swgl.core.util.math.Numbers.remainder;

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

    public AABB getOutline(int x, int y, int z) {
        var aabb = new AABB();
        aabb.min.set(x, y, z);
        aabb.max.set(x + 1.0f, y + 1.0f, z + 1.0f);
        return aabb;
    }

    public AABB getRayCast(int x, int y, int z) {
        return getOutline(x, y, z);
    }

    public AABB getCollision(int x, int y, int z) {
        return getOutline(x, y, z);
    }

    public void tick(World world, int x, int y, int z, Random random) {
    }

    /**
     * Check if this block can place on the {@code target} block.
     *
     * @param target the target block
     * @param world  the target world
     * @param x      the target pos x
     * @param y      the target pos y
     * @param z      the target pos z
     * @param face   the target face
     * @return can place on
     */
    public boolean canPlaceOn(Block target, World world, int x, int y, int z, Direction face) {
        return true;
    }

    public boolean isReplaceable() {
        return false;
    }

    public boolean blocksLight() {
        return true;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean hasSideTransparency() {
        return false;
    }

    public boolean shouldRenderFace(World world, int x, int y, int z, int layer) {
        return world.getBlock(x, y, z).hasSideTransparency() && (world.isLit(x, y, z) ^ (layer == 1));
    }

    public void renderFaceNoTex(Direction face, int x, int y, int z) {
        float x0 = (float) x;
        float y0 = (float) y;
        float z0 = (float) z;
        float x1 = x + 1.0f;
        float y1 = y + 1.0f;
        float z1 = z + 1.0f;
        lglIndices(0, 1, 2, 2, 3, 0);
        lglNormal(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ());
        switch (face) {
            case WEST -> {
                // -x
                lglVertex(x0, y1, z0);
                lglEmit();
                lglVertex(x0, y0, z0);
                lglEmit();
                lglVertex(x0, y0, z1);
                lglEmit();
                lglVertex(x0, y1, z1);
                lglEmit();
            }
            case EAST -> {
                // +x
                lglVertex(x1, y1, z1);
                lglEmit();
                lglVertex(x1, y0, z1);
                lglEmit();
                lglVertex(x1, y0, z0);
                lglEmit();
                lglVertex(x1, y1, z0);
                lglEmit();
            }
            case DOWN -> {
                // -y
                lglVertex(x0, y0, z1);
                lglEmit();
                lglVertex(x0, y0, z0);
                lglEmit();
                lglVertex(x1, y0, z0);
                lglEmit();
                lglVertex(x1, y0, z1);
                lglEmit();
            }
            case UP -> {
                // +y
                lglVertex(x0, y1, z0);
                lglEmit();
                lglVertex(x0, y1, z1);
                lglEmit();
                lglVertex(x1, y1, z1);
                lglEmit();
                lglVertex(x1, y1, z0);
                lglEmit();
            }
            case NORTH -> {
                // -z
                lglVertex(x1, y1, z0);
                lglEmit();
                lglVertex(x1, y0, z0);
                lglEmit();
                lglVertex(x0, y0, z0);
                lglEmit();
                lglVertex(x0, y1, z0);
                lglEmit();
            }
            case SOUTH -> {
                // +z
                lglVertex(x0, y1, z1);
                lglEmit();
                lglVertex(x0, y0, z1);
                lglEmit();
                lglVertex(x1, y0, z1);
                lglEmit();
                lglVertex(x1, y1, z1);
                lglEmit();
            }
        }
    }

    public void renderFace(Direction face, int x, int y, int z) {
        float x0 = (float) x;
        float y0 = (float) y;
        float z0 = (float) z;
        float x1 = x + 1.0f;
        float y1 = y + 1.0f;
        float z1 = z + 1.0f;
        lglIndices(0, 1, 2, 2, 3, 0);
        lglNormal(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ());
        int texture = getTexture(face);
        float u0 = remainder(texture, 16) * 16.0f / BlockAtlas.TEXTURE_WIDTH;
        float v0 = (float) (divSafeFast(texture, 16)) * 16.0f / BlockAtlas.TEXTURE_HEIGHT;
        float u1 = (remainder(texture, 16) * 16.0f + 16.0f) / BlockAtlas.TEXTURE_WIDTH;
        float v1 = ((float) (divSafeFast(texture, 16)) * 16.0f + 16.0f) / BlockAtlas.TEXTURE_HEIGHT;
        switch (face) {
            case WEST -> {
                // -x
                lglTexCoord(u0, v0);
                lglVertex(x0, y1, z0);
                lglEmit();
                lglTexCoord(u0, v1);
                lglVertex(x0, y0, z0);
                lglEmit();
                lglTexCoord(u1, v1);
                lglVertex(x0, y0, z1);
                lglEmit();
                lglTexCoord(u1, v0);
                lglVertex(x0, y1, z1);
                lglEmit();
            }
            case EAST -> {
                // +x
                lglTexCoord(u0, v0);
                lglVertex(x1, y1, z1);
                lglEmit();
                lglTexCoord(u0, v1);
                lglVertex(x1, y0, z1);
                lglEmit();
                lglTexCoord(u1, v1);
                lglVertex(x1, y0, z0);
                lglEmit();
                lglTexCoord(u1, v0);
                lglVertex(x1, y1, z0);
                lglEmit();
            }
            case DOWN -> {
                // -y
                lglTexCoord(u0, v0);
                lglVertex(x0, y0, z1);
                lglEmit();
                lglTexCoord(u0, v1);
                lglVertex(x0, y0, z0);
                lglEmit();
                lglTexCoord(u1, v1);
                lglVertex(x1, y0, z0);
                lglEmit();
                lglTexCoord(u1, v0);
                lglVertex(x1, y0, z1);
                lglEmit();
            }
            case UP -> {
                // +y
                lglTexCoord(u0, v0);
                lglVertex(x0, y1, z0);
                lglEmit();
                lglTexCoord(u0, v1);
                lglVertex(x0, y1, z1);
                lglEmit();
                lglTexCoord(u1, v1);
                lglVertex(x1, y1, z1);
                lglEmit();
                lglTexCoord(u1, v0);
                lglVertex(x1, y1, z0);
                lglEmit();
            }
            case NORTH -> {
                // -z
                lglTexCoord(u0, v0);
                lglVertex(x1, y1, z0);
                lglEmit();
                lglTexCoord(u0, v1);
                lglVertex(x1, y0, z0);
                lglEmit();
                lglTexCoord(u1, v1);
                lglVertex(x0, y0, z0);
                lglEmit();
                lglTexCoord(u1, v0);
                lglVertex(x0, y1, z0);
                lglEmit();
            }
            case SOUTH -> {
                // +z
                lglTexCoord(u0, v0);
                lglVertex(x0, y1, z1);
                lglEmit();
                lglTexCoord(u0, v1);
                lglVertex(x0, y0, z1);
                lglEmit();
                lglTexCoord(u1, v1);
                lglVertex(x1, y0, z1);
                lglEmit();
                lglTexCoord(u1, v0);
                lglVertex(x1, y1, z1);
                lglEmit();
            }
        }
    }

    public void renderAllNoTex(int x, int y, int z) {
        for (var face : Direction.values()) {
            renderFaceNoTex(face, x, y, z);
        }
    }

    public void renderAll(int x, int y, int z) {
        for (var face : Direction.values()) {
            renderFace(face, x, y, z);
        }
    }

    public void render(World world, int layer, int x, int y, int z) {
        for (var face : Direction.values()) {
            if (shouldRenderFace(world,
                x + face.getOffsetX(),
                y + face.getOffsetY(),
                z + face.getOffsetZ(),
                layer)) {
                float c1 = 1.0f;
                float c2 = 0.8f;
                float c3 = 0.6f;
                if (face.isOnAxisX()) {
                    lglColor(c1, c1, c1);
                } else if (face.isOnAxisY()) {
                    lglColor(c2, c2, c2);
                } else if (face.isOnAxisZ()) {
                    lglColor(c3, c3, c3);
                }
                renderFace(face, x, y, z);
            }
        }
    }
}
