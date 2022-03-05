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
import org.overrun.swgl.game.world.World;

import java.util.Random;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GrassBlock extends Block {
    public GrassBlock(byte id, int texture) {
        super(id, texture);
    }

    @Override
    public int getTexture(Direction face) {
        if (face == Direction.UP)
            return 1;
        if (face == Direction.DOWN)
            return 3;
        return super.getTexture(face);
    }

    @Override
    public void tick(World world, int x, int y, int z, Random random) {
        if (!world.isLit(x, y, z)) {
            world.setBlock(x, y, z, Blocks.DIRT);
        } else {
            // repeat 4 times
            for (int i = 0; i < 4; i++) {
                int bx = x + random.nextInt(3) - 1;
                int by = y + random.nextInt(5) - 3;
                int bz = z + random.nextInt(3) - 1;
                if (world.getBlock(bx, by, bz) == Blocks.DIRT && world.isLit(bx, by, bz)) {
                    world.setBlock(bx, by, bz, Blocks.GRASS_BLOCK);
                }
            }
        }
    }
}
