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

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

/**
 * @author squid233
 * @since 0.2.0
 */
public class AtlasSpriteSlot implements Comparable<AtlasSpriteSlot> {
    public Node fit;
    public int w, h;

    /**
     * Construct with the params fit node and position
     *
     * @param fit Fit node
     * @param w   width
     * @param h   height
     */
    public AtlasSpriteSlot(Node fit,
                           int w,
                           int h) {
        this.fit = fit;
        this.w = w;
        this.h = h;
    }

    /**
     * Construct with the params position
     *
     * @param w width
     * @param h height
     */
    public AtlasSpriteSlot(int w,
                           int h) {
        this.w = w;
        this.h = h;
    }

    public static AtlasSpriteSlot of(int x,
                                     int y,
                                     int w,
                                     int h) {
        return new AtlasSpriteSlot(new Node(x, y, w, h), w, h);
    }

    /**
     * Construct without params
     */
    public AtlasSpriteSlot() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AtlasSpriteSlot.class.getSimpleName() + "[", "]")
            .add("fit=" + fit)
            .add("w=" + w)
            .add("h=" + h)
            .toString();
    }

    @Override
    public int compareTo(@NotNull AtlasSpriteSlot o) {
        return (h > o.h) ? -1 : ((h < o.h) ? 1 : Integer.compare(o.w, w));
    }
}
