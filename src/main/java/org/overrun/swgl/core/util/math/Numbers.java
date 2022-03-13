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

import static org.joml.Math.toRadians;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Numbers {
    /**
     * The angles in radians for 90°.
     */
    public static final float RAD90F = toRadians(90.0f);
    /**
     * The angles in radians for 360°.
     */
    public static final float RAD360F = toRadians(360.0f);
    /**
     * The epsilon of single float point.
     * <p>
     * Writing in plain format: {@code 0.000001}
     * </p>
     */
    public static final float EPSILON_SINGLE = 1.0E-06f;
    /**
     * The epsilon of double float point.
     * <p>
     * Writing in plain format: {@code 0.000000000000001}
     * </p>
     */
    public static final double EPSILON_DOUBLE = 1.0E-15;

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

    /**
     * Check if two double float point numbers are equal.
     * <p>
     * Checking method: {@code |a-b| &lt; 10<sup>-15</sup>}
     * </p>
     *
     * @param a The first number.
     * @param b The second number.
     * @return The result.
     */
    public static boolean isEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON_DOUBLE;
    }

    /**
     * Check if two single double point numbers are non-equal.
     * <p>
     * Checking method: {@code |a-b| &gt;= 10<sup>-15</sup>}
     * </p>
     *
     * @param a The first number.
     * @param b The second number.
     * @return The result.
     */
    public static boolean isNonEqual(double a, double b) {
        return Math.abs(a - b) >= EPSILON_DOUBLE;
    }

    /**
     * Check if the number is 0.
     *
     * @param a The number.
     * @return Is zero
     */
    public static boolean isZero(float a) {
        return isEqual(a, 0.0f);
    }

    /**
     * Check if the number is not 0.
     *
     * @param a The number.
     * @return Is non-zero
     */
    public static boolean isNonZero(float a) {
        return isNonEqual(a, 0.0f);
    }

    /**
     * Check if the number is an even number.
     *
     * @param a The number.
     * @return Is an even number
     */
    public static boolean isEven(int a) {
        return (a & 1) == 0;
    }

    /**
     * Check if the number is an even number.
     *
     * @param a The number
     * @throws IllegalArgumentException If {@code a} is an odd number
     */
    public static void checkEven(int a) throws IllegalArgumentException {
        // Check if even
        if (Numbers.isOdd(a))
            throw new IllegalArgumentException("The kvs length must be an even number! Got: " + a + ".");
    }

    /**
     * Check if the number is an odd number.
     *
     * @param a The number.
     * @return Is an odd number
     */
    public static boolean isOdd(int a) {
        return (a & 1) != 0;
    }

    /**
     * Check if the number is power 2.
     *
     * @param a The number.
     * @return Is the number power 2
     */
    public static boolean isPower2(int a) {
        if (a == 0)
            return false;
        return (a & (a - 1)) == 0;
    }

    /**
     * Get {@code a % b}.
     *
     * @param a The number.
     * @param b The modulator
     * @return {@code a % b}
     */
    public static int remainder(int a, int b) {
        if (isPower2(b))
            return a & (b - 1);
        return a % b;
    }

    /**
     * Get the division of {@code a} and {@code b}.
     *
     * @param a the number a
     * @param b the number b
     * @return {@code b == 0 ? 0 : a / b}
     */
    public static int divSafeFast(int a, int b) {
        if (a == 0)
            return 0;
        if (a > 0)
            return switch (b) {
                case 0 -> 0; // safe
                case 1 -> a;
                case 2 -> a >> 1;
                case 4 -> a >> 2;
                case 8 -> a >> 3;
                case 16 -> a >> 4;
                case 32 -> a >> 5;
                case 64 -> a >> 6;
                case 128 -> a >> 7;
                case 256 -> a >> 8;
                case 512 -> a >> 9;
                case 1024 -> a >> 10;
                case 2048 -> a >> 11;
                case 4096 -> a >> 12;
                case 8192 -> a >> 13;
                case 16384 -> a >> 14;
                case 32768 -> a >> 15;
                case 65536 -> a >> 16;
                case 131072 -> a >> 17;
                case 262144 -> a >> 18;
                case 524288 -> a >> 19;
                case 1048576 -> a >> 20;
                case 2097152 -> a >> 21;
                case 4194304 -> a >> 22;
                case 8388608 -> a >> 23;
                case 16777216 -> a >> 24;
                case 33554432 -> a >> 25;
                case 67108864 -> a >> 26;
                case 134217728 -> a >> 27;
                case 268435456 -> a >> 28;
                case 536870912 -> a >> 29;
                case 1073741824 -> a >> 30;
                default -> a / b;
            };
        return a / b;
    }
}
