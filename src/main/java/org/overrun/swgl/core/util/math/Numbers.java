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
     * <code>log<sub>e</sub>2</code>
     *
     * @since 0.2.0
     */
    public static final double LN2 = Math.log(2);

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
     * Check if the number is power of 2.
     *
     * @param a The number.
     * @return Is the number power of 2
     */
    public static boolean isPoT(int a) {
        if (a <= 0)
            return false;
        return (a & (a - 1)) == 0;
    }

    /**
     * Convert {@code a} to a number that is power of 2.
     *
     * @param a the number
     * @return the result
     * @since 0.2.0
     */
    public static int toPoT(int a) {
        --a;
        a |= a >> 1;
        a |= a >> 2;
        a |= a >> 4;
        a |= a >> 8;
        a |= a >> 16;
        return a + 1;
    }

    /**
     * <code>log<sub>a</sub>n</code>
     *
     * @param a a
     * @param n n
     * @return <code>ln(n) / ln(a)</code>
     * @since 0.2.0
     */
    public static double log(double a, double n) {
        return Math.log(n) / Math.log(a);
    }

    /**
     * <code>log<sub>2</sub>n</code>
     *
     * @param a a
     * @return <code>ln(a) / ln2</code>
     * @since 0.2.0
     */
    public static double log2(double a) {
        return Math.log(a) / LN2;
    }

    /**
     * Get {@code a % b}.
     *
     * @param a The number.
     * @param b The modulator
     * @return {@code a % b}
     */
    public static int remainder(int a, int b) {
        if (b == 0)
            throw new ArithmeticException("The modulator must not be zero!");
        if (b < 0)
            return a % b;
        int b1 = b - 1;
        if ((b & b1) == 0)
            return a & b1;
        return a % b;
    }

    /**
     * Parses the string argument as a signed integer.
     *
     * @param s A string. Acceptable format: {@code (0[xXbB])?\\d+}
     * @return The integer value represented by the argument.
     * @throws NumberFormatException If the string does not contain a parsable integer.
     * @since 0.2.0
     */
    public static int parseIntAuto4(String s) throws NumberFormatException {
        if (s.startsWith("0x") || s.startsWith("0X")) {
            return Integer.parseInt(s.substring(2), 16);
        } else if (s.startsWith("0b") || s.startsWith("0B")) {
            return Integer.parseInt(s.substring(2), 2);
        } else if (s.startsWith("0") && s.length() > 1) {
            return Integer.parseInt(s.substring(1), 8);
        } else {
            return Integer.parseInt(s);
        }
    }
}
