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

import static org.lwjgl.opengl.GL43C.*;

/**
 * The texture parameters builder.
 *
 * @author squid233
 * @since 0.2.0
 */
public final class TextureParam {
    private Integer depthStencilTextureMode;
    private Integer compareFunc;
    private Integer baseLevel, maxLevel;
    private Integer minFilter, magFilter;
    private Integer swizzleR, swizzleG, swizzleB, swizzleA;
    private Integer wrapS, wrapT, wrapR;
    private Float lodBias;
    private Float minLod, maxLod;
    private float[] borderColor;

    public TextureParam depthStencilTextureMode(int mode) {
        depthStencilTextureMode = mode;
        return this;
    }

    public TextureParam compareFunc(int compareFunc) {
        this.compareFunc = compareFunc;
        return this;
    }

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

    public TextureParam swizzleR(int swizzleR) {
        this.swizzleR = swizzleR;
        return this;
    }

    public TextureParam swizzleG(int swizzleG) {
        this.swizzleG = swizzleG;
        return this;
    }

    public TextureParam swizzleB(int swizzleB) {
        this.swizzleB = swizzleB;
        return this;
    }

    public TextureParam swizzleA(int swizzleA) {
        this.swizzleA = swizzleA;
        return this;
    }

    public TextureParam swizzleRGBA(int swizzleRGBA) {
        return swizzleR(swizzleRGBA).swizzleG(swizzleRGBA).swizzleB(swizzleRGBA).swizzleA(swizzleRGBA);
    }

    public TextureParam wrapS(int wrapS) {
        this.wrapS = wrapS;
        return this;
    }

    public TextureParam wrapT(int wrapT) {
        this.wrapT = wrapT;
        return this;
    }

    public TextureParam wrapR(int wrapR) {
        this.wrapR = wrapR;
        return this;
    }

    public TextureParam lodBias(float lodBias) {
        this.lodBias = lodBias;
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

    public TextureParam borderColor(float[] borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public TextureParam fromOther(TextureParam param) {
        if (param.depthStencilTextureMode != null) depthStencilTextureMode = param.depthStencilTextureMode;
        if (param.compareFunc != null) compareFunc = param.compareFunc;
        if (param.baseLevel != null) baseLevel = param.baseLevel;
        if (param.maxLevel != null) maxLevel = param.maxLevel;
        if (param.minFilter != null) minFilter = param.minFilter;
        if (param.magFilter != null) magFilter = param.magFilter;
        if (param.swizzleR != null) swizzleR = param.swizzleR;
        if (param.swizzleG != null) swizzleG = param.swizzleG;
        if (param.swizzleB != null) swizzleB = param.swizzleB;
        if (param.swizzleA != null) swizzleA = param.swizzleA;
        if (param.wrapS != null) wrapS = param.wrapS;
        if (param.wrapT != null) wrapT = param.wrapT;
        if (param.wrapR != null) wrapR = param.wrapR;
        if (param.lodBias != null) lodBias = param.lodBias;
        if (param.minLod != null) minLod = param.minLod;
        if (param.maxLod != null) maxLod = param.maxLod;
        if (param.borderColor != null) borderColor = param.borderColor;
        return this;
    }

    public void pushToGL(int target) {
        if (depthStencilTextureMode != null)
            glTexParameteri(target, GL_DEPTH_STENCIL_TEXTURE_MODE, depthStencilTextureMode);
        if (compareFunc != null) glTexParameteri(target, GL_TEXTURE_COMPARE_FUNC, compareFunc);
        if (baseLevel != null) glTexParameteri(target, GL_TEXTURE_BASE_LEVEL, baseLevel);
        if (maxLevel != null) glTexParameteri(target, GL_TEXTURE_MAX_LEVEL, maxLevel);
        if (minFilter != null) glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
        if (magFilter != null) glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
        if (swizzleR != null) glTexParameteri(target, GL_TEXTURE_SWIZZLE_R, swizzleR);
        if (swizzleG != null) glTexParameteri(target, GL_TEXTURE_SWIZZLE_G, swizzleG);
        if (swizzleB != null) glTexParameteri(target, GL_TEXTURE_SWIZZLE_B, swizzleB);
        if (swizzleA != null) glTexParameteri(target, GL_TEXTURE_SWIZZLE_A, swizzleA);
        if (wrapS != null) glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
        if (wrapT != null) glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
        if (wrapR != null) glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
        if (lodBias != null) glTexParameterf(target, GL_TEXTURE_LOD_BIAS, lodBias);
        if (minLod != null) glTexParameterf(target, GL_TEXTURE_MIN_LOD, minLod);
        if (maxLod != null) glTexParameterf(target, GL_TEXTURE_MAX_LOD, maxLod);
        if (borderColor != null) glTexParameterfv(target, GL_TEXTURE_BORDER_COLOR, borderColor);
    }
}
