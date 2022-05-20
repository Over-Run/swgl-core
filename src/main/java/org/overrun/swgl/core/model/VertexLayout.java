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

import org.lwjgl.opengl.GL20C;

import java.util.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class VertexLayout {
    protected final Map<VertexFormat, Integer> offsetMap = new LinkedHashMap<>();
    protected final Set<VertexFormat> formats;
    protected int stride;
    private final boolean hasPosition, hasColor, hasTexture, hasNormal;

    /**
     * Create a layout with the vertex formats.
     * <p>
     * The formats are order-<b>sensitive</b>.
     * </p>
     *
     * @param formats the vertex formats
     */
    public VertexLayout(Collection<VertexFormat> formats) {
        this.formats = new LinkedHashSet<>();
        this.formats.addAll(formats);
        int property = 0;
        for (var format : formats) {
            property |= addFmt(format);
        }
        hasPosition = ((VertexFormat.PROP_VERTEX) & property) != 0;
        hasColor = ((VertexFormat.PROP_COLOR) & property) != 0;
        hasTexture = ((VertexFormat.PROP_TEX_COORD) & property) != 0;
        hasNormal = ((VertexFormat.PROP_NORMAL) & property) != 0;
    }

    /**
     * Create a layout with the vertex formats.
     * <p>
     * The formats are order-<b>sensitive</b>.
     * </p>
     *
     * @param formats the vertex formats
     */
    public VertexLayout(VertexFormat... formats) {
        this(Arrays.asList(formats));
    }

    /**
     * Create a vertex with the given string.
     * <h4>Example</h4>
     * <pre>{@code var layout = forName("V3f_C4ub");
     * program = new GLProgram(layout);}</pre>
     *
     * @param str The case-insensitive string.
     *            The {@link VertexFormat formats} are separated with underscore.
     * @return the vertex layout
     * @since 0.2.0
     */
    public static VertexLayout forName(String str) {
        str = str.trim().toUpperCase(Locale.ROOT);
        var arr = str.split("_");
        var formats = new VertexFormat[arr.length];
        for (int i = 0; i < arr.length; i++) {
            formats[i] = VertexFormat.valueOf(arr[i]);
        }
        return new VertexLayout(formats);
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

    private int addFmt(VertexFormat format) {
        offsetMap.put(format, stride);
        stride += format.getBytes();
        return format.getProperty();
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
            GL20C.glDisableVertexAttribArray(i);
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

    /**
     * Get the offset map for iteration
     *
     * @return the map
     * @since 0.2.0
     */
    public Map<VertexFormat, Integer> getOffsetMap() {
        return offsetMap;
    }

    public int getOffset(VertexFormat format) {
        return offsetMap.get(format);
    }

    public int getStride() {
        return stride;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexLayout that = (VertexLayout) o;
        return Objects.equals(formats, that.formats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formats);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VertexLayout.class.getSimpleName() + "[", "]")
            .add("offsetMap=" + offsetMap)
            .add("formats=" + formats)
            .add("stride=" + stride)
            .add("hasPosition=" + hasPosition)
            .add("hasColor=" + hasColor)
            .add("hasTexture=" + hasTexture)
            .add("hasNormal=" + hasNormal)
            .toString();
    }
}
