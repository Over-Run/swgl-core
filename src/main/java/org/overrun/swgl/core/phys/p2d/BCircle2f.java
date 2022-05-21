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

package org.overrun.swgl.core.phys.p2d;

import org.joml.Intersectionf;
import org.joml.Vector2fc;

/**
 * The 2d bounding circle.
 *
 * @author squid233
 * @since 0.2.0
 */
public class BCircle2f {
    private float x, y, r;

    public BCircle2f() {
    }

    public BCircle2f(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public BCircle2f(Vector2fc pos, float r) {
        this(pos.x(), pos.y(), r);
    }

    /**
     * Copy the other circle.
     *
     * @param other the other circle
     */
    public BCircle2f(BCircle2f other) {
        x = other.x;
        y = other.y;
        r = other.r;
    }

    /**
     * Check if this is intersected with {@code b}.
     *
     * @param b the other circle
     * @return is intersected
     */
    public boolean test(BCircle2f b) {
        return Intersectionf.testCircleCircle(x, y, r * r,
            b.x, b.y, b.r * b.r);
    }

    /**
     * Check if this is intersected with {@code b}.
     *
     * @param b the other rect
     * @return is intersected
     */
    public boolean test(AABRect2f b) {
        return Intersectionf.testAarCircle(b.minX(), b.minY(),
            b.maxX(), b.maxY(),
            x, y, r * r);
    }

    public BCircle2f move(float x, float y, BCircle2f dst) {
        return dst.x(x() + x).y(y() + y);
    }

    public BCircle2f move(float x, float y) {
        return move(x, y, this);
    }

    public BCircle2f x(float x) {
        this.x = x;
        return this;
    }

    public BCircle2f y(float y) {
        this.y = y;
        return this;
    }

    public BCircle2f r(float r) {
        this.r = r;
        return this;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float r() {
        return r;
    }
}
