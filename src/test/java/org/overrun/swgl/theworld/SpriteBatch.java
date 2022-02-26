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

package org.overrun.swgl.theworld;

import org.overrun.swgl.core.asset.Texture2D;

import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class SpriteBatch {
    public static void draw(
        Tesselator t,
        Texture2D texture,
        float x,
        float y,
        float w,
        float h
    ) {
        int lastUnit = getActiveTexture();
        int lastId = get2DTextureId();
        texture.bind();
        boolean colored = t.hasColor(), textured = t.hasTexture();
        if (!colored)
            t.enableColor();
        if (!textured)
            t.enableTexture();
        t.begin();
        t.quadIndex();
        t.color(1, 1, 1, 1);
        t.tex(0.0f, 0.0f).vertex(x, y + h, 0.0f).emit();
        t.tex(0.0f, 1.0f).vertex(x, y, 0.0f).emit();
        t.tex(1.0f, 1.0f).vertex(x + w, y, 0.0f).emit();
        t.tex(1.0f, 0.0f).vertex(x + w, y + h, 0.0f).emit();
        t.flush();
        if (colored)
            t.disableColor();
        if (textured)
            t.disableTexture();
        texture.unbind();
        bindTexture2D(lastUnit, lastId);
    }
}
