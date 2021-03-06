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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Blocks {
    private static final Map<Byte, Block> BLOCK_MAP = new LinkedHashMap<>();
    public static final Block AIR = register(new AirBlock((byte) 0));
    public static final Block STONE = register(new Block((byte) 1, 0));
    public static final Block GRASS_BLOCK = register(new GrassBlock((byte) 2, 2));
    public static final Block DIRT = register(new Block((byte) 3, 3));
    public static final Block BEDROCK = register(new Block((byte) 4, 4));
    public static final Block COBBLESTONE = register(new Block((byte) 5, 5));
    public static final Block OAK_PLANKS = register(new Block((byte) 6, 6));
    public static final Block OAK_LOG = register(new LogBlock((byte) 7, 7, 8));
    public static final Block OAK_SAPLING = register(new SaplingBlock((byte) 8, 9));
    public static final Block OAK_LEAVES = register(new LeavesBlock((byte) 9, 10));

    public static Block getBlock(int id) {
        return BLOCK_MAP.get((byte) id);
    }

    private static Block register(Block block) {
        BLOCK_MAP.put(block.id, block);
        return block;
    }
}
