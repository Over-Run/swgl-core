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

import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.overrun.swgl.core.io.IFileProvider;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.stb.STBTruetype.*;

/**
 * STB True-type bitmap data.
 * <h2>Example</h2>
 * <pre>{@code
 * var buffer = new STBFontInfoBuffer()
 *     .init("C:/Windows/Fonts/times.ttf", IFileProvider.LOCAL)
 *     .computeVMetrics(64.0f);
 *
 * //...allocate mipmap here
 * buffer.writeBuffer(bitmapWidth, bitmapBuf, "Hello world!");
 *
 * //...render the mipmap...
 * //...at the end of the program
 *
 * //...free all bitmap buffers...
 * buffer.close();}</pre>
 *
 * @author squid233
 * @since 0.2.0
 */
public class STBFontInfoBuffer implements AutoCloseable {
    private STBTTFontinfo fontInfo = null;
    private List<STBFontInfoBuffer> backends = null;
    private float scale = 0.0f;
    private int ascent = 0, descent = 0, lineGap = 0;
    private static int _glyphIndex;

    public STBFontInfoBuffer init(ByteBuffer fontData,
                                  @Nullable Runnable onFail) {
        Objects.requireNonNull(fontData, "Couldn't initialize font from buffer!");
        fontInfo = STBTTFontinfo.calloc();
        if (!stbtt_InitFont(fontInfo(), fontData)) {
            if (onFail != null) {
                onFail.run();
            }
        }
        return this;
    }

    public STBFontInfoBuffer init(ByteBuffer fontData) {
        return init(fontData, null);
    }

    public STBFontInfoBuffer init(String name,
                                  IFileProvider provider,
                                  @Nullable Runnable onFail) {
        return init(provider.res2BBWithRE(name, 8192), onFail);
    }

    public STBFontInfoBuffer init(String name,
                                  IFileProvider provider) {
        return init(name, provider, null);
    }

    /**
     * Add the backend fonts.
     *
     * @param buffers the backend fonts
     */
    public STBFontInfoBuffer addBackendFont(STBFontInfoBuffer... buffers) {
        if (buffers.length > 0) {
            if (backends == null) {
                backends = new ArrayList<>();
            }
            backends.addAll(Arrays.asList(buffers));
        }
        return this;
    }

    private void requireInit() {
        Objects.requireNonNull(fontInfo(), "Didn't initialized font!");
    }

    public STBFontInfoBuffer computeVMetrics(float pixels) {
        requireInit();
        scale = stbtt_ScaleForPixelHeight(fontInfo(), pixels);
        int[] pAscent = {0}, pDescent = {0}, pLineGap = {0};
        stbtt_GetFontVMetrics(fontInfo(), pAscent, pDescent, pLineGap);
        ascent = Math.round(pAscent[0] * scale());
        descent = Math.round(pDescent[0] * scale());
        lineGap = Math.round(pLineGap[0] * scale());
        if (backends != null) {
            for (var buffer : backends) {
                buffer.computeVMetrics(pixels);
            }
        }
        return this;
    }

    private static STBFontInfoBuffer _getInfoBuffer(
        STBFontInfoBuffer buffer,
        char c,
        boolean[] hasBackend) {
        var result = buffer;
        boolean[] hasBackend0 = {false};
        if (buffer.isGlyphEmpty(c)) {
            STBFontInfoBuffer temp = null;
            if (buffer.backends != null) {
                for (var backend : buffer.backends) {
                    temp = _getInfoBuffer(backend, c, hasBackend0);
                    if (hasBackend0[0])
                        break;
                }
            }
            if (temp != null) {
                result = temp;
            }
        } else if (hasBackend != null)
            hasBackend[0] = true;
        return result;
    }

    public STBFontInfoBuffer writeBuffer(int bitmapW,
                                         ByteBuffer bitmapBuf,
                                         String text) {
        requireInit();
        // bitmap x, current line (from 0), current advance y, advance y
        int[] data = {0, 0, 0, 0};
        // horizontal metrics
        int[] pAdvanceWidth = {0}, pLeftSideBearing = {0};
        // char bound box
        int[] pX1 = {0}, pY1 = {0}, pX2 = {0}, pY2 = {0};

        text.lines().forEachOrdered(str -> {
            char[] ca = str.toCharArray();
            for (int i = 0, l = ca.length; i < l; i++) {
                char c = ca[i];

                var infoBuffer = _getInfoBuffer(this, c, null);
                if (infoBuffer == null) {
                    throw new IllegalStateException("Impossible state appear; font buffer is null");
                }
                var fontInfo = infoBuffer.fontInfo();
                float scale = infoBuffer.scale();
                int ascent = infoBuffer.ascent();
                int descent = infoBuffer.descent();
                int lineGap = infoBuffer.lineGap();

                // get horizontal metrics
                stbtt_GetGlyphHMetrics(fontInfo, _glyphIndex, pAdvanceWidth, pLeftSideBearing);

                // get char bound box
                stbtt_GetGlyphBitmapBox(fontInfo, _glyphIndex, scale, scale, pX1, pY1, pX2, pY2);

                // compute bitmap y
                int y = ascent + pY1[0];
                int advanceY = ascent - descent + lineGap + pY2[0];
                if (advanceY > data[2]) {
                    data[2] = advanceY;
                }
                if (data[1] > 0) {
                    y += data[3];
                }

                // render char
                int byteOffset = data[0] + Math.round(pLeftSideBearing[0] * scale) + (y * bitmapW);
                int outW = pX2[0] - pX1[0];
                int outH = pY2[0] - pY1[0];
                if (Checks.CHECKS) {
                    Checks.check(bitmapBuf, (bitmapW != 0 ? bitmapW : outW) * outH);
                }
                nstbtt_MakeGlyphBitmap(fontInfo.address(),
                    MemoryUtil.memAddress(bitmapBuf) + Integer.toUnsignedLong(byteOffset),
                    outW,
                    outH,
                    bitmapW,
                    scale,
                    scale,
                    _glyphIndex);

                data[0] += Math.round(pAdvanceWidth[0] * scale);

                char next;
                if (i + 1 >= l) {
                    next = '\0';
                } else {
                    next = ca[i + 1];
                }
                int kern = stbtt_GetGlyphKernAdvance(fontInfo, _glyphIndex, stbtt_FindGlyphIndex(fontInfo, next));
                data[0] += Math.round(kern * scale);
            }
            data[0] = 0;
            ++data[1];
            data[3] += data[2];
            data[2] = 0;
        });

        return this;
    }

    /**
     * Allocate a bitmap buffer with specified size and write text to it.
     * <p>
     * The buffer must be explicit freed using {@link MemoryUtil#memFree(Buffer) memFree}.
     * </p>
     *
     * @param outputW the bitmap width
     * @param outputH the bitmap height
     * @param text    the text
     * @return the buffer
     */
    public ByteBuffer writeBuffer(int outputW,
                                  int outputH,
                                  String text) {
        var buffer = MemoryUtil.memCalloc(outputW * outputH);
        writeBuffer(outputW, buffer, text);
        return buffer;
    }

    @Override
    public void close() {
        if (fontInfo != null)
            fontInfo.close();
        fontInfo = null;
        if (backends != null) {
            for (var buffer : backends) {
                buffer.close();
            }
            backends.clear();
        }
    }

    /**
     * Returns true if nothing is drawn for this glyph.
     *
     * @param c the char
     * @return true if nothing is drawn for this glyph
     */
    public boolean isGlyphEmpty(char c) {
        _glyphIndex = stbtt_FindGlyphIndex(fontInfo(), c);
        if (_glyphIndex == 0)
            return true;
        return stbtt_IsGlyphEmpty(fontInfo(), _glyphIndex);
    }

    public STBTTFontinfo fontInfo() {
        return fontInfo;
    }

    public float scale() {
        return scale;
    }

    public int ascent() {
        return ascent;
    }

    public int descent() {
        return descent;
    }

    public int lineGap() {
        return lineGap;
    }
}
