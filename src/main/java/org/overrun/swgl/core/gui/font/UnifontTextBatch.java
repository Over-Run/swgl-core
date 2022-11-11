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

import org.overrun.swgl.core.asset.Asset;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.util.math.Numbers;

/**
 * The Unifont text renderer.
 *
 * <ul>
 * <li>Easy-to-deploy</li>
 * <li>UTF-8 Plane 0</li>
 * </ul>
 *
 * @author squid233
 * @since 0.2.0
 */
// TODO: Remove Easy font
public class UnifontTextBatch {
    private static final String FONT_BITMAP = Asset.BUILTIN_RES_BASE_DIR + "/unifont_0.png";
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static UnifontTextBatch instance;

    public UnifontTextBatch() {
    }

    public static UnifontTextBatch getInstance() {
        if (instance == null) {
            instance = new UnifontTextBatch();
        }
        return instance;
    }

    public static int getCharWidth(char c) {
        return (Numbers.inRange(c, 33, 128)) ?
            8 :
            16;
    }

    public static int getCharHeight() {
        return 16;
    }

    public void dispose() {
    }
}
