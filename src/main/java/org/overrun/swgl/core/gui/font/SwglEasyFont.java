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

package org.overrun.swgl.core.gui.font;

import org.jetbrains.annotations.Range;
import org.joml.Vector2i;
import org.overrun.swgl.core.asset.Asset;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.io.IFileProvider;

import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11C.*;

/**
 * Bitmap font for use in 3D APIs:
 *
 * <ul>
 * <li>Easy-to-deploy</li>
 * <li>ASCII-only</li>
 * </ul>
 *
 * @author squid233
 * @since 0.1.0
 */
public class SwglEasyFont {
    private static final SwglEasyGlyph[] GLYPHS = new SwglEasyGlyph[256];
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static Texture2D texture;
    private static int lastId;

    /**
     * Initialize the texture and glyphs.
     */
    public static void initialize() {
        texture = new Texture2D();
        texture.defaultWidth = texture.defaultHeight = 128;
        texture.setParam(target -> {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        });
        texture.reload(Asset.BUILTIN_RES_BASE_DIR + "/__$$swgl_easy_ascii_font$$__.png",
            FILE_PROVIDER);
        // 0~32( )
        for (int i = 0; i < 33; i++) {
            setGlyph(i, 0, 5);
        }
        // 33~47
        setGlyph('!', 8, 1);
        setGlyph('"', 16, 3);
        setGlyph('#', 24, 5);
        setGlyph('$', 32, 5);
        setGlyph('%', 40, 5);
        setGlyph('&', 48, 5);
        setGlyph('\'', 56, 1);
        setGlyph('(', 64, 3);
        setGlyph(')', 72, 3);
        setGlyph('*', 80, 3);
        setGlyph('+', 88, 5);
        setGlyph(',', 96, 1);
        setGlyph('-', 104, 5);
        setGlyph('.', 112, 1);
        setGlyph('/', 120, 5);
        // 48(0)~57(9)
        for (int i = 0; i < 10; i++) {
            setGlyph(i + 48, i << 3, 5);
        }
        // 58~63
        setGlyph(':', 80, 1);
        setGlyph(';', 88, 1);
        setGlyph('<', 96, 4);
        setGlyph('=', 104, 5);
        setGlyph('>', 112, 4);
        setGlyph('?', 120, 5);
        // 64
        setGlyph('@', 0, 6);
        // 65(A)~72(H)
        for (int i = 1; i < 9; i++) {
            setGlyph(i + 64, i << 3, 5);
        }
        // 73~79
        setGlyph('I', 72, 3);
        setGlyph('J', 80, 5);
        setGlyph('K', 88, 5);
        setGlyph('L', 96, 5);
        setGlyph('M', 104, 5);
        setGlyph('N', 112, 5);
        setGlyph('O', 120, 5);
        // 80(P)~90(Z)
        for (int i = 0; i < 11; i++) {
            setGlyph(i + 80, i << 3, 5);
        }
        // 91~95
        setGlyph('[', 88, 3);
        setGlyph('\\', 96, 5);
        setGlyph(']', 104, 3);
        setGlyph('^', 112, 5);
        setGlyph('_', 120, 5);
        // 96~111
        setGlyph('`', 0, 2);
        setGlyph('a', 8, 5);
        setGlyph('b', 16, 5);
        setGlyph('c', 24, 5);
        setGlyph('d', 32, 5);
        setGlyph('e', 40, 5);
        setGlyph('f', 48, 4);
        setGlyph('g', 56, 5);
        setGlyph('h', 64, 5);
        setGlyph('i', 72, 1);
        setGlyph('j', 80, 5);
        setGlyph('k', 88, 4);
        setGlyph('l', 96, 2);
        setGlyph('m', 104, 5);
        setGlyph('n', 112, 5);
        setGlyph('o', 120, 5);
        // 112~127
        setGlyph('p', 0, 5);
        setGlyph('q', 8, 5);
        setGlyph('r', 16, 5);
        setGlyph('s', 24, 5);
        setGlyph('t', 32, 3);
        setGlyph('u', 40, 5);
        setGlyph('v', 48, 5);
        setGlyph('w', 56, 5);
        setGlyph('x', 64, 5);
        setGlyph('y', 72, 5);
        setGlyph('z', 80, 5);
        setGlyph('{', 88, 3);
        setGlyph('|', 96, 1);
        setGlyph('}', 104, 3);
        setGlyph('~', 112, 6);
        setGlyph(127, 120, 5);
        // 128~143
        for (int i = 0; i < 16; i++) {
            setGlyph(i + 128, 0, 5);
        }
        // 144~155
        for (int i = 0; i < 12; i++) {
            setGlyph(i + 144, 0, 5);
        }
        // 156~159
        setGlyph(156, 96, 5);
        setGlyph(157, 104, 5);
        setGlyph(158, 112, 5);
        setGlyph(159, 120, 5);
        // 160~175
        setGlyph(160, 0, 5);
        setGlyph(161, 8, 5);
        setGlyph(162, 16, 5);
        setGlyph(163, 24, 5);
        setGlyph(164, 32, 5);
        setGlyph(165, 40, 5);
        setGlyph(166, 48, 4);
        setGlyph(167, 56, 4);
        setGlyph(168, 64, 5);
        setGlyph(169, 72, 5);
        setGlyph(170, 80, 5);
        setGlyph(171, 88, 5);
        setGlyph(172, 96, 5);
        setGlyph(173, 104, 5);
        setGlyph(174, 112, 6);
        setGlyph(175, 120, 6);
        // 176~223
        for (int i = 0; i < 48; i++) {
            setGlyph(i + 176, (i & 15) << 3, 8);
        }
        // 224~236
        for (int i = 0; i < 13; i++) {
            setGlyph(i + 224, i << 3, 5);
        }
        // 237~239
        setGlyph(237, 104, 7);
        setGlyph(238, 112, 5);
        setGlyph(239, 120, 5);
        // 240~255
        setGlyph(240, 0, 6);
        setGlyph(241, 8, 5);
        setGlyph(242, 16, 5);
        setGlyph(243, 24, 5);
        setGlyph(244, 32, 7);
        setGlyph(245, 40, 4);
        setGlyph(246, 48, 5);
        setGlyph(247, 56, 6);
        setGlyph(248, 64, 4);
        setGlyph(249, 72, 5);
        setGlyph(250, 80, 5);
        setGlyph(251, 88, 6);
        setGlyph(252, 96, 4);
        setGlyph(253, 104, 4);
        setGlyph(254, 112, 5);
        setGlyph(255, 120, 5);
    }

    private static void setGlyph(@Range(from = 0, to = 0xFFFF) int c,
                                 @Range(from = 0, to = 120) int u0,
                                 @Range(from = 1, to = 8) int width) {
        int v0 = c >> 4 << 3;
        GLYPHS[c] = new SwglEasyGlyph(u0, v0, u0 + width + 1, v0 + 8);
    }

    public static void bindTexture() {
        lastId = GLStateMgr.get2DTextureId();
        texture.bind();
    }

    public static float getTextureSize() {
        return texture.getWidth();
    }

    public static SwglEasyGlyph getGlyph(char c) {
        if (c < 256)
            return GLYPHS[c];
        return GLYPHS[32];
    }

    /**
     * Creates a text descriptor.
     *
     * @param x    the position x on the left-top
     * @param y    the position y on the left-top
     * @param text the text
     * @return the descriptor
     */
    public static SwglEasyTextDesc createTextDesc(
        int x,
        int y,
        String text) {
        int len = text.length();
        var xy0 = new Vector2i[len];
        var xy1 = new Vector2i[len];
        var glyphs = new SwglEasyGlyph[len];
        var cx = new AtomicInteger(x);
        var cy = new AtomicInteger(y);
        var i = new AtomicInteger();
        text.lines().forEachOrdered(s -> {
            for (char c : s.toCharArray()) {
                final int ig = i.get();
                final int cxg = cx.get();
                final int cyg = cy.get();
                final int cw = getWidth(c);
                xy0[ig] = new Vector2i(cxg, cyg);
                xy1[ig] = new Vector2i(cxg + cw, cyg + getHeight(c));
                glyphs[ig] = getGlyph(c);
                cx.addAndGet(cw);
                i.getAndIncrement();
            }
            cy.addAndGet(getHeight(' '));
        });
        return new SwglEasyTextDesc(text, xy0, xy1, glyphs);
    }

    public static int getWidth(char c) {
        if (c == '\n' || c == '\r')
            return 0;
        var g = getGlyph(c);
        return g.u1() - g.u0();
    }

    public static int getWidth(String text) {
        var result = new AtomicInteger();
        text.lines().forEachOrdered(s -> {
            for (char c : s.toCharArray()) {
                result.addAndGet(getWidth(c));
            }
        });
        return result.get();
    }

    /**
     * Get the height of the char.
     *
     * @param c the char
     * @return c = '\n' | '\r' -> 0, 8
     */
    public static int getHeight(char c) {
        if (c == '\n' || c == '\r')
            return 0;
        return 8;
    }

    /**
     * Get the height of the text. Supported to line separators.
     *
     * @param text the text
     * @return the max height of the text
     */
    public static int getHeight(String text) {
        if (text == null)
            return 0;
        int h = getHeight(' ');
        return (int) (text.lines().count()) * h;
    }

    public static void unbindTexture() {
        GLStateMgr.bindTexture2D(lastId);
    }

    /**
     * Destroy the texture.
     */
    public static void destroy() {
        texture.close();
    }
}
