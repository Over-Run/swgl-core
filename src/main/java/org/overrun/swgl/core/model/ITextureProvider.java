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

package org.overrun.swgl.core.model;

import org.jetbrains.annotations.Nullable;
import org.overrun.swgl.core.asset.Texture;
import org.overrun.swgl.core.util.math.Numbers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The texture provider.
 * <p>
 * The functional interface is available when there's <b>only one</b> (can't be 0 or 2+) texture.
 * </p>
 *
 * @author squid233
 * @since 0.1.0
 */
@FunctionalInterface
public interface ITextureProvider {
    /**
     * Get the texture from specified texture unit.
     *
     * @param unit The texture unit.
     * @return The texture. May be null.
     */
    @Nullable
    Texture getTexture(int unit);

    default int getMinUnit() {
        return 0;
    }

    default int getMaxUnit() {
        return 1;
    }

    static ITextureProvider of(Texture texture) {
        return unit -> texture;
    }

    static ITextureProvider of(int minUnit,
                               int maxUnit,
                               Object... kvs) {
        Numbers.checkEven(kvs.length);
        return new ITextureProvider() {
            private final Map<Integer, Object> map = new LinkedHashMap<>();

            {
                for (int i = 0; i < kvs.length; ) {
                    var unit = (int) kvs[i++];
                    var tex = kvs[i++];
                    map.put(unit, tex);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public @Nullable Texture getTexture(int unit) {
                var p = map.get(unit);
                if (p instanceof Texture texture)
                    return texture;
                if (p instanceof ITextureProvider provider)
                    return provider.getTexture(unit);
                if (p instanceof Supplier<?> supplier)
                    return (Texture) supplier.get();
                if (p instanceof Function<?, ?>)
                    return ((Function<Integer, Texture>) p).apply(unit);
                return null;
            }

            @Override
            public int getMinUnit() {
                return minUnit;
            }

            @Override
            public int getMaxUnit() {
                return maxUnit;
            }
        };
    }

    static ITextureProvider of(ITextureProvider provider,
                               int minUnit,
                               int maxUnit) {
        return new ITextureProvider() {
            @Override
            public @Nullable Texture getTexture(int unit) {
                return provider.getTexture(unit);
            }

            @Override
            public int getMinUnit() {
                return minUnit;
            }

            @Override
            public int getMaxUnit() {
                return maxUnit;
            }
        };
    }
}
