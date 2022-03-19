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

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;
import static org.overrun.swgl.core.util.math.Numbers.divSafeFast;
import static org.overrun.swgl.core.util.math.Numbers.remainder;

/**
 * @author squid233
 * @since 0.1.0
 */
public class SaplingBlock extends Block {
    public SaplingBlock(byte id, int texture) {
        super(id, texture);
    }

    @Override
    public void render(World world, int layer, int x, int y, int z) {
        if (world.isLit(x, y, z) == (layer != 1)) {
            float x0 = (float) x;
            float y0 = (float) y;
            float z0 = (float) z;
            float x1 = x + 1.0f;
            float y1 = y + 1.0f;
            float z1 = z + 1.0f;
            int tex = getTexture(Direction.SOUTH);
            float u0 = remainder(tex, 16) * 16.0f / BlockAtlas.TEXTURE_WIDTH;
            float v0 = (float) (divSafeFast(tex, 16)) * 16.0f / BlockAtlas.TEXTURE_HEIGHT;
            float u1 = (remainder(tex, 16) * 16.0f + 16.0f) / BlockAtlas.TEXTURE_WIDTH;
            float v1 = ((float) (divSafeFast(tex, 16)) * 16.0f + 16.0f) / BlockAtlas.TEXTURE_HEIGHT;
            lglColor(1.0f, 1.0f, 1.0f);

            // 1(1)
            lglIndices(0, 1, 2, 2, 3, 0);
            lglTexCoord(u0, v0);
            lglVertex(x0, y1, z0);
            lglEmit();
            lglTexCoord(u0, v1);
            lglVertex(x0, y0, z0);
            lglEmit();
            lglTexCoord(u1, v1);
            lglVertex(x1, y0, z1);
            lglEmit();
            lglTexCoord(u1, v0);
            lglVertex(x1, y1, z1);
            lglEmit();
            // 1(2)
            lglIndices(0, 1, 2, 2, 3, 0);
            lglTexCoord(u0, v0);
            lglVertex(x1, y1, z1);
            lglEmit();
            lglTexCoord(u0, v1);
            lglVertex(x1, y0, z1);
            lglEmit();
            lglTexCoord(u1, v1);
            lglVertex(x0, y0, z0);
            lglEmit();
            lglTexCoord(u1, v0);
            lglVertex(x0, y1, z0);
            lglEmit();
            // 2(1)
            lglIndices(0, 1, 2, 2, 3, 0);
            lglTexCoord(u0, v0);
            lglVertex(x0, y1, z1);
            lglEmit();
            lglTexCoord(u0, v1);
            lglVertex(x0, y0, z1);
            lglEmit();
            lglTexCoord(u1, v1);
            lglVertex(x1, y0, z0);
            lglEmit();
            lglTexCoord(u1, v0);
            lglVertex(x1, y1, z0);
            lglEmit();
            // 2(2)
            lglIndices(0, 1, 2, 2, 3, 0);
            lglTexCoord(u0, v0);
            lglVertex(x1, y1, z0);
            lglEmit();
            lglTexCoord(u0, v1);
            lglVertex(x1, y0, z0);
            lglEmit();
            lglTexCoord(u1, v1);
            lglVertex(x0, y0, z1);
            lglEmit();
            lglTexCoord(u1, v0);
            lglVertex(x0, y1, z1);
            lglEmit();
        }
    }

    @Override
    public boolean canPlaceOn(Block target, World world, int x, int y, int z, Direction face) {
        int ox = x + face.getOffsetX();
        int oy = y + face.getOffsetY();
        int oz = z + face.getOffsetZ();
        if (world.isOutsideWorld(ox,
            oy,
            oz))
            return false;
        if (face == Direction.UP) {
            return target == Blocks.GRASS_BLOCK || target == Blocks.DIRT;
        }
        var bottom = world.getBlock(ox, oy - 1, oz);
        return bottom == Blocks.GRASS_BLOCK || bottom == Blocks.DIRT;
    }

    @Override
    public boolean hasSideTransparency() {
        return true;
    }

    @Override
    public boolean blocksLight() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AABB getCollision(int x, int y, int z) {
        return null;
    }
}
