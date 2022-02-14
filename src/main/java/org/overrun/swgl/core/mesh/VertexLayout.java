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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author squid233
 * @since 0.1.0
 */
public abstract class VertexLayout {
    protected int stride;
    protected final Map<VertexFormat, Integer> offsetMap = new HashMap<>();

    public VertexLayout(List<VertexFormat> formats) {
        for (var format : formats) {
            offsetMap.put(format, stride);
            stride += format.getBytes();
        }
    }

    public VertexLayout(VertexFormat... formats) {
        for (var format : formats) {
            offsetMap.put(format, stride);
            stride += format.getBytes();
        }
    }

    public abstract void beginDraw();

    public abstract void endDraw();

    public abstract boolean hasPosition();

    public abstract boolean hasColor();

    public int getMaxTextureUnits() {
        return 1;
    }

    public abstract boolean hasTexture(int unit);

    public abstract boolean hasNormal();

    public int getOffset(VertexFormat format) {
        return offsetMap.get(format);
    }

    public int getStride() {
        return stride;
    }
}
