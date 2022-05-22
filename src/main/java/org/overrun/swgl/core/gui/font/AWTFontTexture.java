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
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.gl.ITessCallback;
import org.overrun.swgl.core.gui.font.AWTFontUtils.CharInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The AWT font texture.
 * <p>
 * <b>Note:</b> loading AWT font texture is slow but high quality.
 * </p>
 *
 * @author squid233
 * @since 0.2.0
 */
public class AWTFontTexture implements AutoCloseable {
    private final Map<Character, CharInfo> charMap = new HashMap<>();
    private Font font = null;
    private Charset charset = StandardCharsets.UTF_8;
    private int maxWidth = 0, maxHeight = 0;
    private final Texture2D texture = new Texture2D();
    private Boolean antialias = null;
    private int letterSpacing = 2;
    private Consumer<Texture2D> consumer;
    private int lineHeight = 0;

    public AWTFontTexture font(Font font) {
        this.font = font;
        return this;
    }

    public Font font() {
        return font;
    }

    public AWTFontTexture charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public AWTFontTexture charset(String charsetName) {
        return charset(Charset.forName(charsetName));
    }

    public Charset charset() {
        return charset;
    }

    public AWTFontTexture maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public int maxWidth() {
        return maxWidth;
    }

    public AWTFontTexture maxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public int maxHeight() {
        return maxHeight;
    }

    public AWTFontTexture maxSize(int maxWidth,
                                  int maxHeight) {
        return maxWidth(maxWidth).maxHeight(maxHeight);
    }

    public AWTFontTexture antialias(boolean antialias) {
        this.antialias = antialias;
        return this;
    }

    @Nullable
    public Boolean antialias() {
        return antialias;
    }

    public AWTFontTexture letterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
        return this;
    }

    public int letterSpacing() {
        return letterSpacing;
    }

    public AWTFontTexture consumer(Consumer<Texture2D> consumer) {
        this.consumer = consumer;
        return this;
    }

    public Texture2D texture() {
        return texture;
    }

    public int lineHeight() {
        return lineHeight;
    }

    private Object getValueAntialias() {
        if (antialias == null)
            return RenderingHints.VALUE_ANTIALIAS_DEFAULT;
        if (antialias)
            return RenderingHints.VALUE_ANTIALIAS_ON;
        return RenderingHints.VALUE_ANTIALIAS_OFF;
    }

    private void setGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, getValueAntialias());
        g.setFont(Objects.requireNonNull(font(),
            "Call font(Font) to set the font before building the font texture."));
    }

    /**
     * Build the font GL texture.
     *
     * @return this
     */
    public AWTFontTexture buildTexture() {
        var g2D = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            .createGraphics();
        setGraphics(g2D);
        var fontMetrics = g2D.getFontMetrics();

        // Getting char info
        var allChars = AWTFontUtils.getAvailableChars(charset(), new StringBuilder()).toString();
        var charArr = allChars.toCharArray();
        int maxX = 0;
        int x = 0, y = 0;
        final int height = fontMetrics.getHeight();
        int drawY = fontMetrics.getAscent();
        lineHeight = height;
        for (char c : charArr) {
            int charWidth = fontMetrics.charWidth(c);
            // check bound
            if (maxWidth() > 0 && maxHeight() > 0) {
                // Out of bound x
                if (x + charWidth > maxWidth()) {
                    if (x > maxX) {
                        maxX = x;
                    }
                    // Next line
                    x = 0;
                    y += height;
                    drawY += height;
                }
                // Out of bound y
                if (y + height > maxHeight()) {
                    throw new IllegalStateException("Font texture out of bounds. Max size: (" +
                                                    maxWidth() + ", " + maxHeight() + ")" +
                                                    ", current y: " + y +
                                                    ", char height: " + height +
                                                    ", max height: " + maxHeight());
                }
            }

            var info = new CharInfo(x, y, charWidth, height, c, drawY);
            charMap.put(c, info);

            x += charWidth /*+ letterSpacing()*/;
        }
        g2D.dispose();
        if (x > maxX) {
            maxX = x;
        }

        // Real writing
        var img = new BufferedImage(maxX, y + height, BufferedImage.TYPE_INT_ARGB);
        g2D = img.createGraphics();
        setGraphics(g2D);
        g2D.setColor(Color.WHITE);
        for (char c : charArr) {
            var info = charMap.get(c);
            g2D.drawString("" + c, info.x(), info.drawY());
        }
        g2D.dispose();

        // Upload texture image
        try (var os = new ByteArrayOutputStream()) {
            boolean oldUseCache = ImageIO.getUseCache();
            if (oldUseCache)
                ImageIO.setUseCache(false);
            ImageIO.write(img, "png", os);
            os.flush();
            if (oldUseCache)
                ImageIO.setUseCache(true);
            if (consumer != null)
                consumer.accept(texture);
            texture.defaultWidth = img.getWidth();
            texture.defaultHeight = img.getHeight();
            texture.load(os.toByteArray(),
                "font texture{" + font() + "}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public int lineWidth(String text) {
        int w = 0;
        for (char c : text.toCharArray()) {
            w += charMap.get(c).w() + letterSpacing();
        }
        return w;
    }

    public int textWidth(String text) {
        int[] max = {0};
        text.lines().forEachOrdered(s -> {
            int w = lineWidth(s);
            if (w > max[0]) {
                max[0] = w;
            }
        });
        return max[0];
    }

    public int textHeight(String text) {
        return (int) (text.lines().count() * lineHeight());
    }

    /**
     * Draw a text with the callback.
     *
     * @param text  the text
     * @param flipY flip y to bottom-to-top
     * @param cb    the tess callback
     */
    public void drawText(String text,
                         boolean flipY,
                         ITessCallback cb) {
        // vertex index, draw y
        final int[] data = {0, 0};
        text.lines().forEachOrdered(ln -> {
            float x = 0.0f;
            for (char c : ln.toCharArray()) {
                var tile = charMap.get(c);
                if (tile == null) {
                    x += letterSpacing();
                    continue;
                }
                float x1 = x + tile.w();
                float y0 = flipY ? (data[1] + tile.h()) : data[1];
                float y1 = flipY ? data[1] : (data[1] + tile.h());
                float u0 = (float) tile.x() / texture().getWidth();
                float u1 = ((float) tile.x() + tile.w()) / texture().getWidth();
                float v0 = (float) tile.y() / texture().getHeight();
                float v1 = ((float) tile.y() + tile.h()) / texture().getHeight();
                cb.emit(x, y0, 0, 1,
                    1, 1, 1, 1,
                    u0, v0, 0, 1,
                    0, 0, 1,
                    data[0]++);
                cb.emit(x, y1, 0, 1,
                    1, 1, 1, 1,
                    u0, v1, 0, 1,
                    0, 0, 1,
                    data[0]++);
                cb.emit(x1, y1, 0, 1,
                    1, 1, 1, 1,
                    u1, v1, 0, 1,
                    0, 0, 1,
                    data[0]++);
                cb.emit(x1, y0, 0, 1,
                    1, 1, 1, 1,
                    u1, v0, 0, 1,
                    0, 0, 1,
                    data[0]++);
                x += tile.w() + letterSpacing();
            }
            data[1] += lineHeight;
        });
    }

    @Override
    public void close() {
        texture.close();
    }
}
