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

import java.util.StringJoiner;

/**
 * The triple interface with 3 objects.
 *
 * @param <L> the left object
 * @param <M> the middle object
 * @param <R> the right object
 * @author squid233
 * @since 0.1.0
 */
public interface ITri<L, M, R> {
    /**
     * Get the left value.
     *
     * @return the left value
     */
    L left();

    /**
     * Get the middle value.
     *
     * @return the middle value
     */
    M middle();

    /**
     * Get the right value.
     *
     * @return the right value
     */
    R right();

    /**
     * Get the first value.
     *
     * @return the first value
     * @see #left()
     * @since 0.2.0
     */
    default L first() {
        return left();
    }

    /**
     * Get the second value.
     *
     * @return the second value
     * @see #middle()
     * @since 0.2.0
     */
    default M second() {
        return middle();
    }

    /**
     * Get the third value.
     *
     * @return the third value
     * @see #right()
     * @since 0.2.0
     */
    default R third() {
        return right();
    }

    /**
     * Get the x value.
     *
     * @return the x value
     * @see #left()
     * @since 0.2.0
     */
    default L x() {
        return left();
    }

    /**
     * Get the y value.
     *
     * @return the y value
     * @see #middle()
     * @since 0.2.0
     */
    default M y() {
        return middle();
    }

    /**
     * Get the z value.
     *
     * @return the z value
     * @see #right()
     * @since 0.2.0
     */
    default R z() {
        return right();
    }

    /**
     * Convert a triple to string.
     *
     * @param tri the triple
     * @return the string
     */
    static String toString(ITri<?, ?, ?> tri) {
        return new StringJoiner(", ", tri.getClass().getSimpleName() + "[", "]")
            .add("left=" + tri.left())
            .add("middle=" + tri.middle())
            .add("right=" + tri.right())
            .toString();
    }
}
