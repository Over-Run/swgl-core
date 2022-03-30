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

package org.overrun.swgl.core.model;

import java.util.*;

import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;

/**
 * @author squid233
 * @since 0.1.0
 */
public class VertexLayout {
    protected final Map<VertexFormat, Integer> offsetMap = new LinkedHashMap<>();
    protected final Set<VertexFormat> formats;
    protected int stride;
    private boolean hasPosition, hasColor, hasTexture, hasNormal;

    /**
     * Create a layout with the vertex formats.
     * <p>
     * The formats order is very <b>important</b>.
     * </p>
     *
     * @param formats the vertex formats
     */
    public VertexLayout(Collection<VertexFormat> formats) {
        this.formats = new LinkedHashSet<>();
        this.formats.addAll(formats);
        for (var format : formats) {
            addFmt(format);
        }
    }

    /**
     * Create a layout with the vertex formats.
     * <p>
     * The formats order is very <b>important</b>.
     * </p>
     *
     * @param formats the vertex formats
     */
    public VertexLayout(VertexFormat... formats) {
        this(Arrays.asList(formats));
    }

    /**
     * The layout consumer to accept format, format offset and index.
     *
     * @author squid233
     * @since 0.2.0
     */
    @FunctionalInterface
    public interface LayoutConsumer {
        /**
         * accept a vertex format
         *
         * @param format the vertex format
         * @param offset the format byte offset
         * @param index  the format index
         */
        void accept(VertexFormat format, int offset, int index);
    }

    private void addFmt(VertexFormat format) {
        offsetMap.put(format, stride);
        stride += format.getBytes();
        if (format.hasPosition()) hasPosition = true;
        else if (format.hasColor()) hasColor = true;
        else if (format.hasTexture()) hasTexture = true;
        else if (format.hasNormal()) hasNormal = true;
    }

    public void beginDraw() {
        int i = 0;
        for (var e : formats) {
            e.beginDraw(i, getStride(), getOffset(e));
            ++i;
        }
    }

    public void endDraw() {
        for (int i = 0, c = formats.size(); i < c; i++) {
            glDisableVertexAttribArray(i);
        }
    }

    public boolean hasPosition() {
        return hasPosition;
    }

    public boolean hasColor() {
        return hasColor;
    }

    public boolean hasTexture() {
        return hasTexture;
    }

    public boolean hasNormal() {
        return hasNormal;
    }

    /**
     * check if this has the specified vertex format
     *
     * @param format the vertex format
     * @return {@code true} if this layout has {@code format}
     * @since 0.2.0
     */
    public boolean hasFormat(VertexFormat format) {
        return formats.contains(format);
    }

    /**
     * Performs the given action for each vertex format.
     *
     * @param action The action to be performed for each entry
     * @since 0.2.0
     */
    public void forEachFormat(LayoutConsumer action) {
        int i = 0;
        for (var entry : offsetMap.entrySet()) {
            action.accept(entry.getKey(), entry.getValue(), i++);
        }
    }

    public int getOffset(VertexFormat format) {
        return offsetMap.get(format);
    }

    public int getStride() {
        return stride;
    }
}
