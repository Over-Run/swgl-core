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

package org.overrun.swgl.core.gui;

import org.jetbrains.annotations.Nullable;
import org.overrun.swgl.core.gl.ITessCallback;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.text.AttributedString;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL12C.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.swgl.core.gl.GLStateMgr.bindTexture2D;
import static org.overrun.swgl.core.gl.GLStateMgr.get2DTextureId;

/**
 * @author squid233
 * @since 1.0
 */
public class AWTDirectDraw implements AutoCloseable {
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private BufferedImage image;
    private Graphics2D graphics;
    private IntBuffer buffer;
    private int textureId;
    private int width, height;
    private int fmAscent;
    private Font font, fallbackFont = null;
    private int lineSpacing = 1;

    /**
     * Create an empty ADDraw.
     * You need to {@link #resize(int, int) resize} it.
     */
    public AWTDirectDraw() {
    }

    public AWTDirectDraw(int width, int height) {
        resize(width, height);
    }

    public AWTDirectDraw resize(int width, int height) {
        if (width <= 0 || height <= 0 ||
            this.width == width && this.height == height) {
            return this;
        }
        this.width = width;
        this.height = height;
        if (textureId == 0 || !glIsTexture(textureId)) {
            textureId = glGenTextures();
        }
        int lastId = get2DTextureId();
        bindTexture2D(textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width, height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            NULL);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        buffer = memRealloc(buffer, width * height);
        if (graphics != null) {
            graphics.dispose();
        }
        graphics = image.createGraphics();
        font = graphics.getFont();
        fmAscent = graphics.getFontMetrics().getAscent();
        bindTexture2D(lastId);
        return this;
    }

    public AWTDirectDraw bind() {
        bindTexture2D(textureId);
        return this;
    }

    public AWTDirectDraw unbind() {
        bindTexture2D(0);
        return this;
    }

    public AWTDirectDraw clear() {
        var c = graphics.getBackground();
        graphics.setBackground(TRANSPARENT);
        graphics.clearRect(0, 0, width, height);
        graphics.setBackground(c);
        return this;
    }

    public AWTDirectDraw withGraphics(Consumer<Graphics2D> consumer) {
        consumer.accept(graphics);
        return this;
    }

    public AWTDirectDraw setLineSpacing(int spacing) {
        lineSpacing = spacing;
        return this;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public AWTDirectDraw setFont(Font font) {
        if (font != null) {
            graphics.setFont(font);
            this.font = font;
            fmAscent = graphics.getFontMetrics().getAscent();
        }
        return this;
    }

    @Deprecated(since = "0.2.0")
    public AWTDirectDraw setFallbackFont(@Nullable Font font) {
        fallbackFont = font;
        return this;
    }

    public AWTDirectDraw setColor(Color c) {
        graphics.setColor(c);
        return this;
    }

    @Deprecated(since = "0.2.0")
    private AttributedString createFallbackStr(String s) {
        var str = new AttributedString(s);
        str.addAttribute(TextAttribute.FONT, font);
        boolean fallback = false;
        int fallbackBegin = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            boolean curFallback = !font.canDisplay(s.charAt(i));
            if (curFallback != fallback) {
                fallback = curFallback;
                if (fallback) {
                    fallbackBegin = i;
                } else {
                    str.addAttribute(TextAttribute.FONT, fallbackFont, fallbackBegin, i);
                }
            }
        }
        return str;
    }

    public AWTDirectDraw drawText(String text, int x, int y, boolean baseline) {
        final int[] drawY = {y + (baseline ? 0 : fmAscent)};
        text.lines().forEachOrdered(s -> {
//            if (fallbackFont != null) {
//                graphics.drawString(createFallbackStr(s).getIterator(), x, drawY[0]);
//            } else {
            graphics.drawString(s, x, drawY[0]);
//            }
            drawY[0] += graphics.getFontMetrics().getLineMetrics(s, graphics).getHeight() + getLineSpacing();
        });
        return this;
    }

    public AWTDirectDraw drawText(String text, int x, int y) {
        return drawText(text, x, y, false);
    }

    public AWTDirectDraw drawText(String text, float x, float y, boolean baseline) {
        final float[] drawY = {y + (baseline ? 0 : fmAscent)};
        text.lines().forEachOrdered(s -> {
//            if (fallbackFont != null) {
//                graphics.drawString(createFallbackStr(s).getIterator(), x, drawY[0]);
//            } else {
            graphics.drawString(s, x, drawY[0]);
//            }
            drawY[0] += graphics.getFontMetrics().getLineMetrics(s, graphics).getHeight() + getLineSpacing();
        });
        return this;
    }

    public AWTDirectDraw drawText(String text, float x, float y) {
        return drawText(text, x, y, false);
    }

    public AWTDirectDraw build() {
        buffer.clear()
            .put(image.getRGB(0, 0,
                width, height,
                new int[width * height],
                0,
                width))
            .flip();
        int lastId = get2DTextureId();
        bindTexture2D(textureId);
        glTexSubImage2D(GL_TEXTURE_2D,
            0,
            0, 0,
            width, height,
            GL_BGRA,
            GL_UNSIGNED_BYTE,
            buffer);
        bindTexture2D(lastId);
        return this;
    }

    /**
     * Flush and draw
     *
     * @param x        position x
     * @param y        position y
     * @param flipY    flip y-axis to bottom-to-top
     * @param callback the callback
     */
    public void flush(int x, int y,
                      boolean flipY,
                      ITessCallback callback) {
        int x1 = x + width;
        int y0 = flipY ? (y + height) : y;
        int y1 = flipY ? y : (y + height);
        callback.emit(x, y0, 0, 1,
            1, 1, 1, 1,
            0, 0, 0, 1,
            0, 0, 1,
            false, false, false,
            0);
        callback.emit(x, y1, 0, 1,
            1, 1, 1, 1,
            0, 1, 0, 1,
            0, 0, 1,
            false, false, false,
            1);
        callback.emit(x1, y1, 0, 1,
            1, 1, 1, 1,
            1, 1, 0, 1,
            0, 0, 1,
            false, false, false,
            2);
        callback.emit(x1, y0, 0, 1,
            1, 1, 1, 1,
            1, 0, 0, 1,
            0, 0, 1,
            false, false, false,
            3);
    }

    @Override
    public void close() {
        memFree(buffer);
        glDeleteTextures(textureId);
    }
}
