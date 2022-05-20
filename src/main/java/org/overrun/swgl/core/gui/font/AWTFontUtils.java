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

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * The AWT font utils.
 *
 * @author squid233
 * @since 0.2.0
 */
public final class AWTFontUtils {
    /**
     * The AWT font char info.
     *
     * @param x     the char u
     * @param y     the char v
     * @param w     the char width
     * @param h     the char height
     * @param c     the char
     * @param drawY the char draw baseline y
     * @author squid233
     * @since 0.2.0
     */
    public record CharInfo(int x, int y,
                           int w, int h,
                           char c, int drawY) {
    }

    /**
     * Get the default font.
     *
     * @return dialog font
     */
    public static Font getDialogFont() {
        return Font.decode(Font.DIALOG);
    }

    /**
     * Get all available chars for the specified charset.
     *
     * @param charset the charset
     * @param builder the string builder to storage result
     * @return builder
     */
    public static StringBuilder getAvailableChars(
        Charset charset,
        StringBuilder builder) {
        var encoder = charset.newEncoder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (encoder.canEncode(c))
                builder.append(c);
        }
        return builder;
    }

    /**
     * Get all available chars for the specified charset.
     *
     * @param charsetName the charset name
     * @param builder     the string builder to storage result
     * @return builder
     */
    public static StringBuilder getAvailableChars(
        String charsetName,
        StringBuilder builder) {
        return getAvailableChars(Charset.forName(charsetName), builder);
    }

    /**
     * Get all UTF-8 available chars.
     *
     * @param builder the string builder to storage result
     * @return builder
     */
    public static StringBuilder getAvailableChars(StringBuilder builder) {
        return getAvailableChars(StandardCharsets.UTF_8, builder);
    }
}
