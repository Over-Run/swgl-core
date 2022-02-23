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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

/**
 * The list array utils.
 *
 * @author squid233
 * @since 0.1.0
 */
public class ListArrays {
    /**
     * Create a list from an array.
     *
     * @param elements the elements
     * @param <E>      the element type
     * @return the unmodifiable list
     */
    @Nullable
    @SafeVarargs
    @Unmodifiable
    @Contract(value = "null -> null", pure = true)
    public static <E> List<E> of(E... elements) {
        if (elements == null)
            return null;
        return List.of(elements);
    }

    /**
     * Create an int list from an array.
     *
     * @param elements the elements
     * @return the unmodifiable list
     */
    @Contract("null -> null")
    public static List<Integer> ofInts(int... elements) {
        if (elements == null)
            return null;
        return Arrays.stream(elements).boxed().toList();
    }
}
