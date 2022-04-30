/*
 * MIT License
 *
 * Copyright (c) 2021-2022 Overrun Organization
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
 *
 */

package org.overrun.swgl.core.asset.tex;

import java.util.StringJoiner;

/**
 * @author squid233
 * @since 0.2.0
 */
public class Node {
    public boolean used;
    public int x;
    public int y;
    public int w;
    public int h;
    public Node down;
    public Node right;

    /**
     * Construct with the params using status, position, size and children
     *
     * @param used  used
     * @param x     x
     * @param y     y
     * @param w     width
     * @param h     height
     * @param down  down node
     * @param right right node
     */
    public Node(boolean used,
                int x,
                int y,
                int w,
                int h,
                Node down,
                Node right) {
        this.used = used;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.down = down;
        this.right = right;
    }

    /**
     * Construct with the params position and size
     *
     * @param x x
     * @param y y
     * @param w width
     * @param h height
     */
    public Node(int x,
                int y,
                int w,
                int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Construct without params
     */
    public Node() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")
            .add("used=" + used)
            .add("x=" + x)
            .add("y=" + y)
            .add("w=" + w)
            .add("h=" + h)
            .add("down=" + down)
            .add("right=" + right)
            .toString();
    }
}
