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

package org.overrun.swgl.core.asset.tex;

/**
 * The texture parameters builder.
 *
 * @author squid233
 * @since 0.2.0
 */
public final class TextureParam {
    public Integer baseLevel, maxLevel;
    public Integer minFilter, magFilter;
    public Float minLod, maxLod;

    public TextureParam baseLevel(int baseLevel) {
        this.baseLevel = baseLevel;
        return this;
    }

    public TextureParam maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public TextureParam minFilter(int minFilter) {
        this.minFilter = minFilter;
        return this;
    }

    public TextureParam magFilter(int magFilter) {
        this.magFilter = magFilter;
        return this;
    }

    public TextureParam minLod(float minLod) {
        this.minLod = minLod;
        return this;
    }

    public TextureParam maxLod(float maxLod) {
        this.maxLod = maxLod;
        return this;
    }

    public TextureParam fromOther(TextureParam param) {
        if (param.baseLevel != null) baseLevel = param.baseLevel;
        if (param.maxLevel != null) maxLevel = param.maxLevel;
        if (param.minLod != null) minLod = param.minLod;
        if (param.maxLod != null) maxLod = param.maxLod;
        return this;
    }
}
