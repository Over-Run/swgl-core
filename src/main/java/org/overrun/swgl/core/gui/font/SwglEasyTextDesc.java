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

import org.joml.Vector2i;

/**
 * The descriptor of swgl easy text.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SwglEasyTextDesc {
    private final String text;
    private final Vector2i[] xy0, xy1;
    private final SwglEasyGlyph[] glyphs;

    /**
     * @param text   The text.
     * @param glyphs The glyphs.
     */
    public SwglEasyTextDesc(String text,
                            Vector2i[] xy0,
                            Vector2i[] xy1,
                            SwglEasyGlyph[] glyphs) {
        this.text = text;
        this.xy0 = xy0;
        this.xy1 = xy1;
        this.glyphs = glyphs;
    }

    public int getLength() {
        return glyphs.length;
    }

    public String getText() {
        return text;
    }

    /**
     * Get the vertex on the left-top.
     *
     * @param index the text index
     * @return left-top vertex
     */
    public Vector2i getXY0(int index) {
        return xy0[index];
    }

    /**
     * Get the vertex on the right-bottom.
     *
     * @param index the text index
     * @return right-bottom vertex
     */
    public Vector2i getXY1(int index) {
        return xy1[index];
    }

    public SwglEasyGlyph getGlyph(int index) {
        return glyphs[index];
    }
}
