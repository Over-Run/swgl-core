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

package org.overrun.swgl.core.util.math;

/**
 * @author squid233
 * @since 0.1.0
 */
public class FloatPoint {
    public static final float EPSILON_SINGLE = 1.0E-06f;

    /**
     * Check if two single float point numbers are equal.
     * <p>
     * Checking method: {@code |a-b| &lt; 10<sup>-6</sup>}
     * </p>
     *
     * @param a The first number.
     * @param b The second number.
     * @return The result.
     */
    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) < EPSILON_SINGLE;
    }

    /**
     * Check if two single float point numbers are non-equal.
     * <p>
     * Checking method: {@code |a-b| &gt;= 10<sup>-6</sup>}
     * </p>
     *
     * @param a The first number.
     * @param b The second number.
     * @return The result.
     */
    public static boolean isNonEqual(float a, float b) {
        return Math.abs(a - b) >= EPSILON_SINGLE;
    }

    public static boolean isZero(float a) {
        return isEqual(a, 0.0f);
    }

    public static boolean isNonZero(float a) {
        return isNonEqual(a, 0.0f);
    }
}
