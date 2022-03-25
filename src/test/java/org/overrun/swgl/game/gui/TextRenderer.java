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

package org.overrun.swgl.game.gui;

import org.lwjgl.opengl.GL20;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.gui.font.SwglEasyFont;

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;
import static org.overrun.swgl.core.gl.ims.GLLists.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class TextRenderer {
    public static int createText(int x, int y,
                                 String text) {
        return createText(x, y, 1.0f, 1.0f, 1.0f, 1.0f, text);
    }

    public static int createText(int x, int y,
                                 float r, float g, float b, float a,
                                 String text) {
        var desc = SwglEasyFont.createTextDesc(x, y, text);
        float invTexSz = 1.0f / SwglEasyFont.getTextureSize();
        int lst = lglGenList();
        lglNewList(lst);
        lglBegin(GLDrawMode.QUADS);
        lglColor(r, g, b, a);
        for (int i = 0, c = desc.getLength(); i < c; i++) {
            var xy0 = desc.getXY0(i);
            var xy1 = desc.getXY1(i);
            var glyph = desc.getGlyph(i);
            lglTexCoord(glyph.u0() * invTexSz, glyph.v0() * invTexSz);
            lglVertex(xy0.x, xy0.y);
            lglEmit();
            lglTexCoord(glyph.u0() * invTexSz, glyph.v1() * invTexSz);
            lglVertex(xy0.x, xy1.y);
            lglEmit();
            lglTexCoord(glyph.u1() * invTexSz, glyph.v1() * invTexSz);
            lglVertex(xy1.x, xy1.y);
            lglEmit();
            lglTexCoord(glyph.u1() * invTexSz, glyph.v0() * invTexSz);
            lglVertex(xy1.x, xy0.y);
            lglEmit();
            GL20.glVertexAttribPointer(1,1,1,false,1,1);
        }
        lglEnd();
        lglEndList();
        return lst;
    }

    public static void drawText(int x, int y,
                                String text) {
        drawText(x, y, 1.0f, 1.0f, 1.0f, 1.0f, text);
    }

    public static void drawText(int x, int y,
                                float r, float g, float b, float a,
                                String text) {
        SwglEasyFont.bindTexture();
        GLStateMgr.enableTexture2D();
        lglSetTexCoordArrayState(true);
        var desc = SwglEasyFont.createTextDesc(x, y, text);
        float invTexSz = 1.0f / SwglEasyFont.getTextureSize();
        lglBegin(GLDrawMode.QUADS);
        lglColor(r, g, b, a);
        for (int i = 0, c = desc.getLength(); i < c; i++) {
            var xy0 = desc.getXY0(i);
            var xy1 = desc.getXY1(i);
            var glyph = desc.getGlyph(i);
            lglTexCoord(glyph.u0() * invTexSz, glyph.v0() * invTexSz);
            lglVertex(xy0.x, xy0.y);
            lglEmit();
            lglTexCoord(glyph.u0() * invTexSz, glyph.v1() * invTexSz);
            lglVertex(xy0.x, xy1.y);
            lglEmit();
            lglTexCoord(glyph.u1() * invTexSz, glyph.v1() * invTexSz);
            lglVertex(xy1.x, xy1.y);
            lglEmit();
            lglTexCoord(glyph.u1() * invTexSz, glyph.v0() * invTexSz);
            lglVertex(xy1.x, xy0.y);
            lglEmit();
        }
        lglEnd();
        lglSetTexCoordArrayState(false);
        SwglEasyFont.unbindTexture();
    }

    public static void drawText(int text) {
        SwglEasyFont.bindTexture();
        GLStateMgr.enableTexture2D();
        lglSetTexCoordArrayState(true);
        lglCallList(text);
        lglSetTexCoordArrayState(false);
        SwglEasyFont.unbindTexture();
    }
}
