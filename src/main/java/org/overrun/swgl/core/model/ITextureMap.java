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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.overrun.swgl.core.asset.tex.Texture;
import org.overrun.swgl.core.util.Tri;

/**
 * The texture mapping.
 * <p>
 * The functional interface should always return a non-null value with min unit and max unit.
 * </p>
 *
 * @author squid233
 * @since 0.1.0
 */
@FunctionalInterface
public interface ITextureMap {
    int UNIT_NOT_CARE = -1;

    /**
     * Get the texture from specified texture unit.
     *
     * @param unit The texture unit.
     * @return The texture tri with (minUnit, maxUnit, texture). The texture may be null.
     */
    @NotNull
    Tri<Integer, Integer, @Nullable Texture> getTexture(int unit);

    default int getMinUnit() {
        return getTexture(UNIT_NOT_CARE).left();
    }

    default int getMaxUnit() {
        return getTexture(UNIT_NOT_CARE).middle();
    }
}
