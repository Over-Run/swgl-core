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
 * The pair interface with 2 objects.
 *
 * @param <L> the left object
 * @param <R> the right object
 * @author squid233
 * @since 0.1.0
 */
public interface IPair<L, R> {
    /**
     * Get the left value.
     *
     * @return the left value
     */
    L left();

    /**
     * Get the right value.
     *
     * @return the right value
     */
    R right();

    /**
     * Convert a pair to string.
     *
     * @param pair the pair
     * @return the string
     */
    static String toString(IPair<?, ?> pair) {
        return new StringJoiner(", ", pair.getClass().getSimpleName() + "[", "]")
            .add("left=" + pair.left())
            .add("right=" + pair.right())
            .toString();
    }
}
