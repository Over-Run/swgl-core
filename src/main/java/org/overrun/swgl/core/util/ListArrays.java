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
import java.util.Collection;
import java.util.List;

/**
 * The list and array utils.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ListArrays {
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

    /**
     * Create a float array from a list.
     *
     * @param collection the list
     * @return the float array
     */
    @Contract("null -> null")
    public static float[] toFloatArray(Collection<Float> collection) {
        if (collection == null)
            return null;
        if (collection.isEmpty())
            return new float[0];
        float[] floats = new float[collection.size()];
        int i = 0;
        for (var f : collection) {
            floats[i] = f;
            ++i;
        }
        return floats;
    }

    /**
     * Create an int array from a list.
     *
     * @param collection the list
     * @return the int array
     */
    @Contract("null -> null")
    public static int[] toIntArray(Collection<Integer> collection) {
        if (collection == null)
            return null;
        if (collection.isEmpty())
            return new int[0];
        return collection.stream().mapToInt(value -> value).toArray();
    }

    /**
     * Create a byte array from a list.
     *
     * @param collection the list
     * @return the byte array
     */
    @Contract("null -> null")
    public static byte[] toByteArray(Collection<Byte> collection) {
        if (collection == null)
            return null;
        if (collection.isEmpty())
            return new byte[0];
        byte[] bytes = new byte[collection.size()];
        int i = 0;
        for (var b : collection) {
            bytes[i] = b;
            ++i;
        }
        return bytes;
    }
}
