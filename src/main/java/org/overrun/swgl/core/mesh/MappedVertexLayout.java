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

package org.overrun.swgl.core.mesh;

import org.overrun.swgl.core.gl.GLProgram;

import java.util.HashMap;
import java.util.Map;

/**
 * The VertexLayout, with element name and value mapping.
 *
 * @author squid233
 * @since 0.1.0
 */
public class MappedVertexLayout extends VertexLayout {
    private final Map<String, VertexFormat> formatMap;
    private boolean hasPosition, hasColor, hasTexture, hasNormal;

    public MappedVertexLayout(Map<String, VertexFormat> map) {
        formatMap = map;
        for (var format : formatMap.values()) {
            offsetMap.put(format, stride);
            stride += format.getBytes();
        }
    }

    /**
     * Construct the layout with key-values.
     *
     * @param kvs The key-values. The length must be an even number.
     *            <p>The elements are in order with:
     *            {@code Object, VertexFormat, Object, VertexFormat...}</p>
     */
    public MappedVertexLayout(Object... kvs) {
        // Check if even
        if ((kvs.length & 1) != 0)
            throw new IllegalArgumentException("The kvs length must be an even number! Got: " + kvs.length + ".");
        formatMap = new HashMap<>();
        for (int i = 0; i < kvs.length; ) {
            var nm = String.valueOf(kvs[i++]);
            var format = (VertexFormat) kvs[i++];
            formatMap.put(nm, format);
            offsetMap.put(format, stride);
            stride += format.getBytes();
        }
    }

    public MappedVertexLayout hasPosition(boolean hasPosition) {
        this.hasPosition = hasPosition;
        return this;
    }

    public MappedVertexLayout hasColor(boolean hasColor) {
        this.hasColor = hasColor;
        return this;
    }

    public MappedVertexLayout hasTexture(boolean hasTexture) {
        this.hasTexture = hasTexture;
        return this;
    }

    public MappedVertexLayout hasNormal(boolean hasNormal) {
        this.hasNormal = hasNormal;
        return this;
    }

    @Override
    public void beginDraw(GLProgram program) {
        for (var e : formatMap.entrySet()) {
            e.getValue().beginDraw(program, e.getKey());
        }
    }

    @Override
    public void endDraw(GLProgram program) {
        for (var e : formatMap.entrySet()) {
            e.getValue().endDraw(program, e.getKey());
        }
    }

    @Override
    public boolean hasPosition() {
        return hasPosition;
    }

    @Override
    public boolean hasColor() {
        return hasColor;
    }

    @Override
    public boolean hasTexture() {
        return hasTexture;
    }

    @Override
    public boolean hasNormal() {
        return hasNormal;
    }
}
