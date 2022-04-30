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

package org.overrun.swgl.core.util;

/**
 * @param left  the left value
 * @param right the right value
 * @author squid233
 * @since 0.1.0
 */
public record FloatPair(float left, float right) {
    /**
     * Get the key value.
     *
     * @return the key value
     * @see #left()
     * @since 0.2.0
     */
    public float key() {
        return left();
    }

    /**
     * Get the value.
     *
     * @return the value
     * @see #right()
     * @since 0.2.0
     */
    public float value() {
        return right();
    }

    /**
     * Get the first value.
     *
     * @return the first value
     * @see #left()
     * @since 0.2.0
     */
    public float first() {
        return left();
    }

    /**
     * Get the second value.
     *
     * @return the second value
     * @see #right()
     * @since 0.2.0
     */
    public float second() {
        return right();
    }

    /**
     * Get the x value.
     *
     * @return the x value
     * @see #left()
     * @since 0.2.0
     */
    public float x() {
        return left();
    }

    /**
     * Get the y value.
     *
     * @return the y value
     * @see #right()
     * @since 0.2.0
     */
    public float y() {
        return right();
    }
}
