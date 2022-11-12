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

package org.overrun.swgl.core.level;

import org.joml.Vector2fc;
import org.overrun.swgl.core.asset.tex.Texture;
import org.overrun.swgl.core.gl.ITessCallback;

/**
 * The sprite drawer.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SpriteDrawer {
    /**
     * Draw a sprite from a callback.
     *
     * @param x       the pos x
     * @param y       the pos y
     * @param w       the size width
     * @param h       the size height
     * @param texU    the sprite UV-coord u
     * @param texV    the sprite UV-coord v
     * @param spriteW the sprite size width
     * @param spriteH the sprite size height
     * @param texW    the texture size width
     * @param texH    the texture size width
     * @param flipY   flip y-axis to bottom-to-top
     * @param cb      the callback, color is always white and normal is always positive-z
     */
    public static void draw(
        float x,
        float y,
        float w,
        float h,
        float texU,
        float texV,
        float spriteW,
        float spriteH,
        float texW,
        float texH,
        boolean flipY,
        ITessCallback cb
    ) {
        float x1 = x + w;
        float y0 = flipY ? (y + h) : y;
        float y1 = flipY ? y : (y + h);
        float u0 = texU / texW;
        float u1 = (texU + spriteW) / texW;
        float v0 = texV / texH;
        float v1 = (texV + spriteH) / texH;

        cb.emit(x, y0, 0,
            1, 1, 1, 1,
            u0, v0, 0,
            0, 0, 1,
            false, true, false,
            0);
        cb.emit(x, y1, 0,
            1, 1, 1, 1,
            u0, v1, 0,
            0, 0, 1,
            false, true, false,
            1);
        cb.emit(x1, y1, 0,
            1, 1, 1, 1,
            u1, v1, 0,
            0, 0, 1,
            false, true, false,
            2);
        cb.emit(x1, y0, 0,
            1, 1, 1, 1,
            u1, v0, 0,
            0, 0, 1,
            false, true, false,
            3);
    }

    /**
     * Draw a sprite from a callback.
     *
     * @param texture the texture which has size
     * @param x       the pos x
     * @param y       the pos y
     * @param w       the size width
     * @param h       the size height
     * @param texU    the sprite UV-coord u
     * @param texV    the sprite UV-coord v
     * @param spriteW the sprite size width
     * @param spriteH the sprite size height
     * @param flipY   flip y-axis to bottom-to-top
     * @param cb      the callback, color is always white and normal is always positive-z
     * @see #draw(float, float, float, float, float, float, float, float, float, float, boolean, ITessCallback)
     */
    public static void draw(
        Texture texture,
        float x,
        float y,
        float w,
        float h,
        float texU,
        float texV,
        float spriteW,
        float spriteH,
        boolean flipY,
        ITessCallback cb
    ) {
        draw(x, y, w, h, texU, texV, spriteW, spriteH, texture.getWidth(), texture.getHeight(), flipY, cb);
    }

    /**
     * Draw a sprite from a callback.
     *
     * @param pos        the pos
     * @param size       the size
     * @param texUV      the sprite UV-coord
     * @param spriteSize the sprite size
     * @param texSize    the texture size
     * @param flipY      flip y-axis to bottom-to-top
     * @param cb         the callback, color is always white and normal is always positive-z
     * @see #draw(float, float, float, float, float, float, float, float, float, float, boolean, ITessCallback)
     */
    public static void draw(
        Vector2fc pos,
        Vector2fc size,
        Vector2fc texUV,
        Vector2fc spriteSize,
        Vector2fc texSize,
        boolean flipY,
        ITessCallback cb
    ) {
        draw(pos.x(), pos.y(),
            size.x(), size.y(),
            texUV.x(), texUV.y(),
            spriteSize.x(), spriteSize.y(),
            texSize.x(), texSize.y(),
            flipY, cb);
    }

    /**
     * Draw a sprite from a callback.
     *
     * @param texture    the texture which has size
     * @param pos        the pos
     * @param size       the size
     * @param texUV      the sprite UV-coord
     * @param spriteSize the sprite size
     * @param flipY      flip y-axis to bottom-to-top
     * @param cb         the callback, color is always white and normal is always positive-z
     * @see #draw(float, float, float, float, float, float, float, float, float, float, boolean, ITessCallback)
     */
    public static void draw(
        Texture texture,
        Vector2fc pos,
        Vector2fc size,
        Vector2fc texUV,
        Vector2fc spriteSize,
        boolean flipY,
        ITessCallback cb
    ) {
        float texW = texture.getWidth();
        float texH = texture.getHeight();

        draw(pos.x(), pos.y(),
            size.x(), size.y(),
            texUV.x(), texUV.y(),
            spriteSize.x(), spriteSize.y(),
            texW, texH,
            flipY, cb);
    }
}
