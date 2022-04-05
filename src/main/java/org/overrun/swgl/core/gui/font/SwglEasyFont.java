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
 * @deprecated Easy font is going to be removed and replaced with true type
 */
@Deprecated(since = "0.2.0")
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
        setGlyph(0, 0, 5);
        setGlyph(1, 0, 5);
        setGlyph(2, 0, 5);
        setGlyph(3, 0, 5);
        setGlyph(4, 0, 5);
        setGlyph(5, 0, 5);
        setGlyph(6, 0, 5);
        setGlyph(7, 0, 5);
        setGlyph(8, 0, 5);
        setGlyph(9, 0, 5);
        setGlyph(10, 0, 5);
        setGlyph(11, 0, 5);
        setGlyph(12, 0, 5);
        setGlyph(13, 0, 5);
        setGlyph(14, 0, 5);
        setGlyph(15, 0, 5);
        setGlyph(16, 0, 5);
        setGlyph(17, 0, 5);
        setGlyph(18, 0, 5);
        setGlyph(19, 0, 5);
        setGlyph(20, 0, 5);
        setGlyph(21, 0, 5);
        setGlyph(22, 0, 5);
        setGlyph(23, 0, 5);
        setGlyph(24, 0, 5);
        setGlyph(25, 0, 5);
        setGlyph(26, 0, 5);
        setGlyph(27, 0, 5);
        setGlyph(28, 0, 5);
        setGlyph(29, 0, 5);
        setGlyph(30, 0, 5);
        setGlyph(31, 0, 5);
        setGlyph(' ', 0, 5);
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
        setGlyph('0', 0, 5);
        setGlyph('1', 8, 5);
        setGlyph('2', 16, 5);
        setGlyph('3', 24, 5);
        setGlyph('4', 32, 5);
        setGlyph('5', 40, 5);
        setGlyph('6', 48, 5);
        setGlyph('7', 56, 5);
        setGlyph('8', 64, 5);
        setGlyph('9', 72, 5);
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
        setGlyph('A', 8, 5);
        setGlyph('B', 16, 5);
        setGlyph('C', 24, 5);
        setGlyph('D', 32, 5);
        setGlyph('E', 40, 5);
        setGlyph('F', 48, 5);
        setGlyph('G', 56, 5);
        setGlyph('H', 64, 5);
        // 73~79
        setGlyph('I', 72, 3);
        setGlyph('J', 80, 5);
        setGlyph('K', 88, 5);
        setGlyph('L', 96, 5);
        setGlyph('M', 104, 5);
        setGlyph('N', 112, 5);
        setGlyph('O', 120, 5);
        // 80(P)~90(Z)
        setGlyph('P', 0, 5);
        setGlyph('Q', 8, 5);
        setGlyph('R', 16, 5);
        setGlyph('S', 24, 5);
        setGlyph('T', 32, 5);
        setGlyph('U', 40, 5);
        setGlyph('V', 48, 5);
        setGlyph('W', 56, 5);
        setGlyph('X', 64, 5);
        setGlyph('Y', 72, 5);
        setGlyph('Z', 80, 5);
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
        setGlyph(128, 0, 5);
        setGlyph(129, 0, 5);
        setGlyph(130, 0, 5);
        setGlyph(131, 0, 5);
        setGlyph(132, 0, 5);
        setGlyph(133, 0, 5);
        setGlyph(134, 0, 5);
        setGlyph(135, 0, 5);
        setGlyph(136, 0, 5);
        setGlyph(137, 0, 5);
        setGlyph(138, 0, 5);
        setGlyph(139, 0, 5);
        setGlyph(140, 0, 5);
        setGlyph(141, 0, 5);
        setGlyph(142, 0, 5);
        setGlyph(143, 0, 5);
        // 144~155
        setGlyph(144, 0, 5);
        setGlyph(145, 0, 5);
        setGlyph(146, 0, 5);
        setGlyph(147, 0, 5);
        setGlyph(148, 0, 5);
        setGlyph(149, 0, 5);
        setGlyph(150, 0, 5);
        setGlyph(151, 0, 5);
        setGlyph(152, 0, 5);
        setGlyph(153, 0, 5);
        setGlyph(154, 0, 5);
        setGlyph(155, 0, 5);
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
        setGlyph(176, 0, 8);
        setGlyph(177, 8, 8);
        setGlyph(178, 16, 8);
        setGlyph(179, 24, 8);
        setGlyph(180, 32, 8);
        setGlyph(181, 40, 8);
        setGlyph(182, 48, 8);
        setGlyph(183, 56, 8);
        setGlyph(184, 64, 8);
        setGlyph(185, 72, 8);
        setGlyph(186, 80, 8);
        setGlyph(187, 88, 8);
        setGlyph(188, 96, 8);
        setGlyph(189, 104, 8);
        setGlyph(190, 112, 8);
        setGlyph(191, 120, 8);
        setGlyph(192, 0, 8);
        setGlyph(193, 8, 8);
        setGlyph(194, 16, 8);
        setGlyph(195, 24, 8);
        setGlyph(196, 32, 8);
        setGlyph(197, 40, 8);
        setGlyph(198, 48, 8);
        setGlyph(199, 56, 8);
        setGlyph(200, 64, 8);
        setGlyph(201, 72, 8);
        setGlyph(202, 80, 8);
        setGlyph(203, 88, 8);
        setGlyph(204, 96, 8);
        setGlyph(205, 104, 8);
        setGlyph(206, 112, 8);
        setGlyph(207, 120, 8);
        setGlyph(208, 0, 8);
        setGlyph(209, 8, 8);
        setGlyph(210, 16, 8);
        setGlyph(211, 24, 8);
        setGlyph(212, 32, 8);
        setGlyph(213, 40, 8);
        setGlyph(214, 48, 8);
        setGlyph(215, 56, 8);
        setGlyph(216, 64, 8);
        setGlyph(217, 72, 8);
        setGlyph(218, 80, 8);
        setGlyph(219, 88, 8);
        setGlyph(220, 96, 8);
        setGlyph(221, 104, 8);
        setGlyph(222, 112, 8);
        setGlyph(223, 120, 8);
        // 224~236
        setGlyph(224, 0, 5);
        setGlyph(225, 8, 5);
        setGlyph(226, 16, 5);
        setGlyph(227, 24, 5);
        setGlyph(228, 32, 5);
        setGlyph(229, 40, 5);
        setGlyph(230, 48, 5);
        setGlyph(231, 56, 5);
        setGlyph(232, 64, 5);
        setGlyph(233, 72, 5);
        setGlyph(234, 80, 5);
        setGlyph(235, 88, 5);
        setGlyph(236, 96, 5);
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
