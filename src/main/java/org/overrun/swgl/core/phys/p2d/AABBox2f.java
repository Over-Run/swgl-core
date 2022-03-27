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

/**
 * The 2d axis-aligned bounding box.
 *
 * @author squid233
 * @since 0.2.0
 */
public class AABBox2f {
    private final Vector2f min = new Vector2f();
    private final Vector2f max = new Vector2f();

    public AABBox2f() {
    }

    public AABBox2f(Vector2fc min, Vector2fc max) {
        setMinMax(min, max);
    }

    public AABBox2f(float minX, float minY, float maxX, float maxY) {
        setMinMax(minX, minY, maxX, maxY);
    }

    /**
     * Check if this is intersected with {@code b}.
     *
     * @param b the other box
     * @return is intersected
     */
    public boolean test(AABBox2f b) {
        return max.x() >= b.min.x() && max.y() >= b.min.y() &&
            min.x() <= b.max.x() && min.y() <= b.max.y();
    }

    public float clipXCollide(float dx, AABBox2f b) {
        if (min.y() > b.max.y() || max.y() < b.min.y())
            return dx;
        return dx;//todo
    }

    public void setMin(Vector2fc min) {
        this.min.set(min);
    }

    public void setMin(float x, float y) {
        this.min.set(x, y);
    }

    public void setMax(Vector2fc max) {
        this.max.set(max);
    }

    public void setMax(float x, float y) {
        this.max.set(x, y);
    }

    public void setMinMax(Vector2fc min, Vector2fc max) {
        setMin(min);
        setMax(max);
    }

    public void setMinMax(float minX, float minY, float maxX, float maxY) {
        setMin(minX, minY);
        setMax(maxX, maxY);
    }

    public Vector2f getMin() {
        return min;
    }

    public Vector2f getMax() {
        return max;
    }
}
