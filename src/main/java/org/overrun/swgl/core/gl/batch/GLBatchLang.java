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

package org.overrun.swgl.core.gl.batch;

import java.util.List;

/**
 * The batch language.
 *
 * @author squid233
 * @since 0.2.0
 */
public class GLBatchLang {
    public static final String FILE_EXTENSION = "bats";
    public static final String KWD_BEGINF = "beginf";
    public static final String KWD_END = "end";
    public static final String KWD_VERTEX = "v";
    public static final String KWD_COLOR = "c";
    public static final String KWD_TEX_COORD = "t";
    public static final String KWD_NORMAL = "n";
    public static final String KWD_INDEX_BEFORE = "ib";
    public static final String KWD_INDEX_AFTER = "ia";
    public static final String KWD_EMIT = "emit";
    private static final List<String> KEYWORDS = List.of(
        KWD_BEGINF,
        KWD_END,
        KWD_VERTEX,
        KWD_COLOR,
        KWD_TEX_COORD,
        KWD_NORMAL,
        KWD_INDEX_BEFORE,
        KWD_INDEX_AFTER,
        KWD_EMIT
    );

    public static boolean isKeyword(String s) {
        return KEYWORDS.contains(s);
    }
}
