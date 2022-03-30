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

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.overrun.swgl.core.util.math.Numbers;

/**
 * The 2d axis-aligned bounding box.
 *
 * @author squid233
 * @since 0.2.0
 */
public class AABBox2f {
    private final Vector2f min = new Vector2f();
    private final Vector2f max = new Vector2f();
    private boolean fixed = false;

    public AABBox2f() {
    }

    public AABBox2f(Vector2fc min, Vector2fc max) {
        setMinMax(min, max).tryFix();
    }

    public AABBox2f(float minX, float minY, float maxX, float maxY) {
        setMinMax(minX, minY, maxX, maxY).tryFix();
    }

    public AABBox2f tryFix() {
        if (fixed)
            return this;
        fixed = true;
        if (min.x > max.x) {
            float f = min.x;
            min.x = max.x;
            max.x = f;
        }
        if (min.y > max.y) {
            float f = min.y;
            min.y = max.y;
            max.y = f;
        }
        return this;
    }

    /**
     * Check if this is intersected with {@code b}.
     *
     * @param b the other box
     * @return is intersected
     */
    public boolean test(AABBox2f b) {
        tryFix();
        return max.x() >= b.min.x() && max.y() >= b.min.y() &&
               min.x() <= b.max.x() && min.y() <= b.max.y();
    }

    /**
     * Clip the movement x from this box.
     * This box is the mover and {@code b} is tester (static object).
     *
     * @param dx the movement
     * @param b  the other box
     * @return the clipped movement
     */
    public float clipXCollide(float dx, AABBox2f b) {
        tryFix();
        if (min.y() > b.max.y() || max.y() < b.min.y())
            return dx;
        if (Numbers.isZero(dx))
            return 0.0f;
        if (dx > 0.0f && max.x() < b.min.x())
            return b.min.x() - max.x();
        if (dx < 0.0f && min.x() > b.max.x())
            return b.max.x() - min.x();
        return 0.0f;
    }

    /**
     * Clip the movement y from this box.
     * This box is the mover and {@code b} is tester (static object).
     *
     * @param dy the movement
     * @param b  the other box
     * @return the clipped movement
     */
    public float clipYCollide(float dy, AABBox2f b) {
        tryFix();
        if (min.x() > b.max.x() || max.x() < b.min.x())
            return dy;
        if (Numbers.isZero(dy))
            return 0.0f;
        if (dy > 0.0f && max.y() < b.min.y())
            return b.min.y() - max.y();
        if (dy < 0.0f && min.y() > b.max.y())
            return b.max.y() - min.y();
        return 0.0f;
    }

    public AABBox2f move(float x, float y, AABBox2f dst) {
        return dst.setMinMax(min.x + x, min.y + y, max.x + x, max.y + y);
    }

    public AABBox2f move(float x, float y) {
        return move(x, y, this);
    }

    public AABBox2f setMin(Vector2fc min) {
        fixed = false;
        this.min.set(min);
        return tryFix();
    }

    public AABBox2f setMin(float x, float y) {
        fixed = false;
        this.min.set(x, y);
        return tryFix();
    }

    public AABBox2f setMax(Vector2fc max) {
        fixed = false;
        this.max.set(max);
        return tryFix();
    }

    public AABBox2f setMax(float x, float y) {
        fixed = false;
        this.max.set(x, y);
        return tryFix();
    }

    public AABBox2f setMinMax(Vector2fc min, Vector2fc max) {
        fixed = false;
        this.min.set(min);
        this.max.set(max);
        return tryFix();
    }

    public AABBox2f setMinMax(float minX, float minY, float maxX, float maxY) {
        fixed = false;
        this.min.set(minX, minY);
        this.max.set(maxX, maxY);
        return tryFix();
    }

    public Vector2fc getMin() {
        return min;
    }

    public Vector2fc getMax() {
        return max;
    }
}
