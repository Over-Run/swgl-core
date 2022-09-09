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

package org.overrun.swgl.core.gl;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.overrun.swgl.core.model.IModel;
import org.overrun.swgl.core.model.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * The vertex builder with a fixed size.
 *
 * @author squid233
 * @see GLBatch size-auto-extend vertex builder
 * @since 0.6.0
 */
public class GLFixedBatch implements ITessCallback, AutoCloseable {
    private VertexLayout layout;
    private final GLVertex[] vertexInfoList;
    private final GLVertex vertexInfo = new GLVertex();
    private final int size, indexSize;
    private final ByteBuffer buffer;
    private final IntBuffer indexBuffer;
    private int writtenBytes = 0;
    private int lastVertexCount = 0;
    private int vertexCount = 0;
    private int indexCount = 0;
    private boolean destroyed = false;
    private boolean hasColor = false, hasTexture = false, hasNormal = false;
    private final boolean useMemUtil;

    /**
     * Create a vertex builder using {@link org.lwjgl.system.MemoryUtil MemoryUtil}.
     *
     * @param size      The size in bytes.
     * @param indexSize The index buffer size, non-positive for no-index-buffer.
     * @see #GLFixedBatch(int, int, boolean) GLFixedBatch(int, int, bool)
     */
    public GLFixedBatch(int size, int indexSize) {
        this(size, indexSize, true);
    }

    /**
     * Create a vertex builder.
     *
     * @param size       The size in bytes.
     * @param indexSize  The index buffer size, non-positive for no-index-buffer.
     * @param useMemUtil Use {@link org.lwjgl.system.MemoryUtil MemoryUtil} if {@code true};
     *                   otherwise use {@link BufferUtils}. Defaults to {@code true}.
     * @see #GLFixedBatch(int, int) GLFixedBatch(int, int)
     */
    public GLFixedBatch(int size, int indexSize, boolean useMemUtil) {
        this.size = size;
        this.indexSize = indexSize;
        this.useMemUtil = useMemUtil;
        if (useMemUtil) {
            buffer = memAlloc(size);
            if (indexSize > 0)
                indexBuffer = memAllocInt(indexSize);
            else
                indexBuffer = null;
        } else {
            buffer = BufferUtils.createByteBuffer(size);
            if (indexSize > 0)
                indexBuffer = BufferUtils.createIntBuffer(indexSize);
            else
                indexBuffer = null;
        }
        vertexInfoList = new GLVertex[size];
    }

    /**
     * Gets the vertex builder size in bytes.
     *
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Gets the size of the index buffer of this vertex builder.
     *
     * @return the index buffer size
     */
    public int indexSize() {
        return indexSize;
    }

    /**
     * Begin drawing by a vertex layout.
     *
     * @param layout the vertex layout
     */
    public void begin(@NotNull VertexLayout layout) {
        this.layout = layout;
        buffer.clear();
        if (indexBuffer != null) {
            indexBuffer.clear();
        }
        writtenBytes = 0;
        lastVertexCount = 0;
        vertexCount = 0;
        indexCount = 0;
    }

    /**
     * Flip buffers and end drawing.
     */
    public void end() {
        for (var vertexInfo : vertexInfoList) {
            // not filled
            if (vertexInfo == null) break;
            layout.forEachFormat((format, offset, index) ->
                vertexInfo.processBuffer(format, buffer));
            writtenBytes += layout.getStride();
        }
        if (buffer.position() > 0) {
            buffer.flip();
        }
        if (indexBuffer != null && indexBuffer.position() > 0) {
            indexBuffer.flip();
        }
    }

    /**
     * Set current vertex 4d.
     *
     * @param x the vertex x
     * @param y the vertex y
     * @param z the vertex z
     * @param w the vertex w
     * @return this
     */
    public GLFixedBatch vertex(float x, float y, float z, float w) {
        vertexInfo.x = x;
        vertexInfo.y = y;
        vertexInfo.z = z;
        vertexInfo.w = w;
        return this;
    }

    /**
     * Set current vertex 3d. {@code w} defaults to 1.
     *
     * @param x the vertex x
     * @param y the vertex y
     * @param z the vertex z
     * @return this
     * @see #vertex(float, float, float, float) vertex4f
     */
    public GLFixedBatch vertex(float x, float y, float z) {
        return vertex(x, y, z, 1.0f);
    }

    /**
     * Set current vertex 2d. {@code z} defaults to 0 and {@code w} defaults to 1.
     *
     * @param x the vertex x
     * @param y the vertex y
     * @return this
     * @see #vertex(float, float, float) vertex3f
     */
    public GLFixedBatch vertex(float x, float y) {
        return vertex(x, y, 0.0f);
    }

    /**
     * Set current color 4d.
     *
     * @param r the color r
     * @param g the color g
     * @param b the color b
     * @param a the color a
     * @return this
     */
    public GLFixedBatch color(float r, float g, float b, float a) {
        return color(IModel.color2byte(r),
            IModel.color2byte(g),
            IModel.color2byte(b),
            IModel.color2byte(a));
    }

    /**
     * Set current color 3d. {@code a} defaults to 1.
     *
     * @param r the color r
     * @param g the color g
     * @param b the color b
     * @return this
     * @see #color(float, float, float, float) color4f
     */
    public GLFixedBatch color(float r, float g, float b) {
        return color(r, g, b, 1.0f);
    }

    /**
     * Set current color 4d.
     *
     * @param r the color r
     * @param g the color g
     * @param b the color b
     * @param a the color a
     * @return this
     */
    public GLFixedBatch color(byte r, byte g, byte b, byte a) {
        vertexInfo.r = r;
        vertexInfo.g = g;
        vertexInfo.b = b;
        vertexInfo.a = a;
        hasColor = true;
        return this;
    }

    /**
     * Set current color 3d. {@code a} defaults to 0xff.
     *
     * @param r the color r
     * @param g the color g
     * @param b the color b
     * @return this
     * @see #color(byte, byte, byte, byte) color4b
     */
    public GLFixedBatch color(byte r, byte g, byte b) {
        return color(r, g, b, (byte) -1);
    }

    /**
     * Set current tex coord 4d.
     *
     * @param s the tex coord s
     * @param t the tex coord t
     * @param r the tex coord r
     * @param q the tex coord q
     * @return this
     */
    public GLFixedBatch texCoord(float s, float t, float r, float q) {
        vertexInfo.s = s;
        vertexInfo.t = t;
        vertexInfo.p = r;
        vertexInfo.q = q;
        hasTexture = true;
        return this;
    }

    /**
     * Set current tex coord 3d. {@code q} defaults to 1.
     *
     * @param s the tex coord s
     * @param t the tex coord t
     * @param r the tex coord r
     * @return this
     * @see #texCoord(float, float, float, float) texCoord4f
     */
    public GLFixedBatch texCoord(float s, float t, float r) {
        return texCoord(s, t, r, 1.0f);
    }

    /**
     * Set current tex coord 2d. {@code r} defaults to 0 and {@code q} defaults to 1.
     *
     * @param s the tex coord s
     * @param t the tex coord t
     * @return this
     * @see #texCoord(float, float, float) texCoord3f
     */
    public GLFixedBatch texCoord(float s, float t) {
        return texCoord(s, t, 0.0f);
    }

    /**
     * Set current tex coord 1d. {@code t} defaults to 0, {@code r} defaults to 0 and {@code q} defaults to 1.
     *
     * @param s the tex coord s
     * @return this
     * @see #texCoord(float, float) texCoord2f
     */
    public GLFixedBatch texCoord(float s) {
        return texCoord(s, 0.0f);
    }

    /**
     * Set current normal 3d.
     *
     * @param nx the normal x
     * @param ny the normal y
     * @param nz the normal z
     * @return this
     */
    public GLFixedBatch normal(float nx, float ny, float nz) {
        vertexInfo.nx = nx;
        vertexInfo.ny = ny;
        vertexInfo.nz = nz;
        hasNormal = true;
        return this;
    }

    /**
     * Add indices before adding vertices.
     * <h4>Example</h4>
     * <pre>{@code begin(layout);
     * indexBefore(0, 1, 2, 2, 3, 0);
     * vertex(x0, y0).emit();
     * vertex(x0, y1).emit();
     * vertex(x1, y1).emit();
     * vertex(x1, y0).emit();
     * end();}</pre>
     *
     * @param indices the indices
     * @return this
     */
    public GLFixedBatch indexBefore(int... indices) {
        for (int index : indices) {
            indexBuffer.put(index + vertexCount);
        }
        indexCount += indices.length;
        return this;
    }

    /**
     * Add indices after adding vertices.
     * <h4>Example</h4>
     * <pre>{@code begin(layout);
     * vertex(x0, y0).emit();
     * vertex(x0, y1).emit();
     * vertex(x1, y1).emit();
     * vertex(x1, y0).emit();
     * indexAfter(0, 1, 2, 2, 3, 0);
     * end();}</pre>
     *
     * @param indices the indices
     * @return this
     */
    public GLFixedBatch indexAfter(int... indices) {
        for (int index : indices) {
            indexBuffer.put(index + lastVertexCount);
        }
        indexCount += indices.length;
        lastVertexCount = vertexCount;
        return this;
    }

    /**
     * Emit a vertex with the vertex layout.
     */
    public void emit() {
        vertexInfoList[vertexCount++] = new GLVertex(vertexInfo);
    }

    @Override
    public void emit(float x, float y, float z, float w,
                     float r, float g, float b, float a,
                     float s, float t, float p, float q,
                     float nx, float ny, float nz,
                     boolean color, boolean tex, boolean normal,
                     int i) {
        if (color) color(r, g, b, a);
        if (tex) texCoord(s, t, p, q);
        if (normal) normal(nx, ny, nz);
        vertex(x, y, z, w).emit();
    }

    /**
     * Puts a buffer.
     *
     * @param buf the buffer
     */
    public void buffer(ByteBuffer buf) {
        buffer.put(buf);
        writtenBytes += buf.limit();
        vertexCount += buf.limit() / layout.getStride();
    }

    /**
     * Puts an index buffer.
     *
     * @param buf the index buffer
     */
    public void indexBuffer(IntBuffer buf) {
        indexBuffer.put(buf);
        indexCount += buf.limit();
    }

    /**
     * Gets the written bytes. It's usually the {@link ByteBuffer#limit() buffer limit}.
     *
     * @return the written bytes
     */
    public int getWrittenBytes() {
        return writtenBytes;
    }

    /**
     * Get the buffer off-heap.
     *
     * @return the buffer; don't modify it
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * Get the index buffer off-heap.
     *
     * @return the index buffer; don't modify it
     */
    public Optional<IntBuffer> getIndexBuffer() {
        return Optional.ofNullable(indexBuffer);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    /**
     * Return {@code true} if the batch has color.
     *
     * @return has color
     */
    public boolean hasColor() {
        return hasColor;
    }

    /**
     * Return {@code true} if the batch has texture coordinate.
     *
     * @return has texture coordinate
     */
    public boolean hasTexture() {
        return hasTexture;
    }

    /**
     * Return {@code true} if the batch has normal.
     *
     * @return has normal
     */
    public boolean hasNormal() {
        return hasNormal;
    }

    @Override
    public void close() {
        if (destroyed)
            return;
        destroyed = true;
        if (useMemUtil) {
            memFree(buffer);
            memFree(indexBuffer);
        }
    }
}
