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
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.asset.tex.TextureParam;
import org.overrun.swgl.core.gl.ITessCallback;
import org.overrun.swgl.core.io.IFileProvider;

import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.overrun.swgl.core.util.math.Numbers.inRange;

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
public class UnifontTextBatch implements AutoCloseable {
    private static final String FONT_BITMAP = Asset.BUILTIN_RES_BASE_DIR + "/unifont_0.png";
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    public static final int CHAR_HEIGHT = 16;
    private static UnifontTextBatch instance;
    private final Texture2D texture;

    public UnifontTextBatch() {
        texture = new Texture2D(FONT_BITMAP, FILE_PROVIDER,
            (t, buffer) -> {
                t.setParam(new TextureParam()
                    .minFilter(GL_NEAREST)
                    .magFilter(GL_NEAREST));
                var intBuffer = buffer.asIntBuffer();
                for (int i = 0; i < intBuffer.limit(); i++) {
                    if (intBuffer.get(i) == 0xff000000) {
                        intBuffer.put(i, 0x00000000);
                    }
                }
            });
    }

    public static UnifontTextBatch getInstance() {
        if (instance == null) {
            instance = new UnifontTextBatch();
        }
        return instance;
    }

    public static int getCharWidth(char c) {
        return (inRange(c, 0x0020, 0x007e) ||
                inRange(c, 0x00a0, 0x00ac) ||
                inRange(c, 0x00ae, 0x4e03)) ? 8 : 16;
    }

    public static int getCharHeight() {
        return CHAR_HEIGHT;
    }

    /**
     * Draws the text.
     * <p>
     * If <i>{@code flipY}</i> is {@code true}, the <i>{@code y}</i> is base-line; otherwise it is base-top.
     *
     * @param text     The text.
     * @param x        The draw position x.
     * @param y        The draw position y.
     * @param flipY    Flip y to bottom-to-top.
     * @param callback The tess callback.
     */
    public void drawText(String text, int x, int y, boolean flipY, ITessCallback callback) {
        int drawX = x;
        int index = 0;
        int ych = y + CHAR_HEIGHT;
        int y0 = (flipY ? ych : y);
        int y1 = (flipY ? y : ych);
        for (char c : text.toCharArray()) {
            float w = getCharWidth(c);
            float u0 = (c % 256) * 16f / getTextureWidth();
            float v0 = (c / 256) * 16f / getTextureHeight();
            float u1 = ((c % 256) * 16f + w) / getTextureWidth();
            float v1 = (c / 256 + 1) * 16f / getTextureHeight();
            callback.emit(drawX, y0, 0,
                1, 1, 1, 1,
                u0, v0, 0,
                0, 0, 1,
                false, true, false,
                index++);
            callback.emit(drawX, y1, 0,
                1, 1, 1, 1,
                u0, v1, 0,
                0, 0, 1,
                false, true, false,
                index++);
            callback.emit(drawX + w, y1, 0,
                1, 1, 1, 1,
                u1, v1, 0,
                0, 0, 1,
                false, true, false,
                index++);
            callback.emit(drawX + w, y0, 0,
                1, 1, 1, 1,
                u1, v0, 0,
                0, 0, 1,
                false, true, false,
                index++);
            drawX += w;
        }
    }

    public void bindTexture() {
        texture.bind();
    }

    public void unbindTexture() {
        texture.unbind();
    }

    public int getTextureWidth() {
        return texture.getWidth();
    }

    public int getTextureHeight() {
        return texture.getHeight();
    }

    public void dispose() {
        texture.close();
    }

    @Override
    public void close() {
        dispose();
    }
}
