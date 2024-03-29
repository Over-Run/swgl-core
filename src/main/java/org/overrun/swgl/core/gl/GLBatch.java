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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * The vertex builder.
 *
 * @author squid233
 * @see GLFixedBatch size-fixed vertex builder
 * @since 0.2.0
 */
public class GLBatch implements ITessCallback, AutoCloseable {
    private static GLBatch globalInstance;
    private VertexLayout layout;
    private final List<GLVertex> vertexInfoList = new ArrayList<>();
    private final GLVertex vertexInfo = new GLVertex();
    private ByteBuffer buffer;
    private IntBuffer indexBuffer;
    private int lastWrittenBytes = 0;
    private int writtenBytes = 0;
    private int lastVertexCount = 0;
    private int vertexCount = 0;
    private int lastIndexCount = 0;
    private int indexCount = 0;
    private boolean destroyed = false;
    private boolean drawing = false;
    private boolean hasColor = false, hasTexture = false, hasNormal = false;
    private final boolean useMemUtil;
    /**
     * The addend to expand the batch.
     */
    private double expandAddend = 0.6180339887498949; // golden ratio (Math.sqrt(5.0) - 1.0) * 0.5

    /**
     * Create a batch using {@link org.lwjgl.system.MemoryUtil MemoryUtil}.
     *
     * @see #GLBatch(boolean) GLBatch(bool)
     */
    public GLBatch() {
        this(true);
    }

    /**
     * Create a batch.
     *
     * @param useMemUtil Use {@link org.lwjgl.system.MemoryUtil MemoryUtil} if {@code true};
     *                   otherwise use {@link BufferUtils}. Defaults to {@code true}.
     * @see #GLBatch() GLBatch()
     */
    public GLBatch(boolean useMemUtil) {
        this.useMemUtil = useMemUtil;
    }

    /**
     * Create a global GLBatch instance.
     *
     * @return the instance
     */
    public static GLBatch createGlobalInstance() {
        return globalInstance = new GLBatch();
    }

    /**
     * Create the global GLBatch instance.
     *
     * @return the instance
     */
    public static GLBatch getGlobalInstance() {
        return globalInstance;
    }

    /**
     * Destroy the global GLBatch instance.
     */
    public static void destroyGlobalInstance() {
        globalInstance.close();
        globalInstance = null;
    }

    /**
     * Set the addend to expand the batch.
     *
     * @param expandAddend The addend. Defaults to {@code 0.618}. Must be greater than {@code 0.0}.
     */
    public void setExpandAddend(double expandAddend) {
        if (expandAddend <= 0.0) {
            throw new IllegalArgumentException("expandAddend must be greater than 0.0");
        }
        this.expandAddend = expandAddend;
    }

    /**
     * Get the addend to expand the batch.
     *
     * @return the addend
     */
    public double getExpandAddend() {
        return expandAddend;
    }

    /**
     * Begin drawing by a vertex layout.
     * <p>
     * The initial count was used to alloc buffer.
     * </p>
     *
     * @param layout       the vertex layout
     * @param initialCount the initial vertex count
     */
    public void begin(@NotNull VertexLayout layout, int initialCount) {
        this.layout = layout;
        if (buffer == null) {
            // Default in 16 vertices
            final int count = Math.max(initialCount, 16);
            if (useMemUtil)
                buffer = memAlloc(count * layout.getStride());
            else
                buffer = BufferUtils.createByteBuffer(count * layout.getStride());
        } else {
            buffer.clear();
            if (buffer.capacity() < initialCount) {
                if (useMemUtil)
                    buffer = memRealloc(buffer, initialCount);
                else
                    buffer = BufferUtils.createByteBuffer(initialCount);
            }
        }
        if (indexBuffer != null) {
            indexBuffer.clear();
        }
        vertexInfoList.clear();
        lastWrittenBytes = writtenBytes;
        writtenBytes = 0;
        lastVertexCount = 0;
        vertexCount = 0;
        lastIndexCount = indexCount;
        indexCount = 0;
        drawing = true;
    }

    /**
     * Begin drawing by a vertex layout.
     *
     * @param layout the vertex layout
     */
    public void begin(@NotNull VertexLayout layout) {
        begin(layout, 16);
    }

    /**
     * Flip buffers and end drawing.
     */
    public void end() {
        buffer = (ByteBuffer) tryGrowBuffer(buffer, layout.getStride() * vertexInfoList.size());
        for (var vertexInfo : vertexInfoList) {
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
        drawing = false;
    }

    /**
     * Set current vertex 3d..
     *
     * @param x the vertex x
     * @param y the vertex y
     * @param z the vertex z
     * @return this
     */
    public GLBatch vertex(float x, float y, float z) {
        vertexInfo.x = x;
        vertexInfo.y = y;
        vertexInfo.z = z;
        return this;
    }

    /**
     * Set current vertex 2d. {@code z} defaults to 0.
     *
     * @param x the vertex x
     * @param y the vertex y
     * @return this
     * @see #vertex(float, float, float) vertex3f
     */
    public GLBatch vertex(float x, float y) {
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
    public GLBatch color(float r, float g, float b, float a) {
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
    public GLBatch color(float r, float g, float b) {
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
    public GLBatch color(byte r, byte g, byte b, byte a) {
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
    public GLBatch color(byte r, byte g, byte b) {
        return color(r, g, b, (byte) -1);
    }

    /**
     * Set current tex coord 3d.
     *
     * @param s the tex coord s
     * @param t the tex coord t
     * @param r the tex coord r
     * @return this
     */
    public GLBatch texCoord(float s, float t, float r) {
        vertexInfo.s = s;
        vertexInfo.t = t;
        vertexInfo.p = r;
        hasTexture = true;
        return this;
    }

    /**
     * Set current tex coord 2d. {@code r} defaults to 0.
     *
     * @param s the tex coord s
     * @param t the tex coord t
     * @return this
     * @see #texCoord(float, float, float) texCoord3f
     */
    public GLBatch texCoord(float s, float t) {
        return texCoord(s, t, 0.0f);
    }

    /**
     * Set current tex coord 1d. {@code t} defaults to 0, {@code r} defaults to 0 and {@code q} defaults to 1.
     *
     * @param s the tex coord s
     * @return this
     * @see #texCoord(float, float) texCoord2f
     */
    public GLBatch texCoord(float s) {
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
    public GLBatch normal(float nx, float ny, float nz) {
        vertexInfo.nx = nx;
        vertexInfo.ny = ny;
        vertexInfo.nz = nz;
        hasNormal = true;
        return this;
    }

    private Buffer tryGrowBuffer(Buffer buffer, int len) {
        if (buffer.position() + len >= buffer.capacity()) {
            int increment = Math.max(len, (int) (buffer.capacity() * expandAddend));
            // Grows buffer for (1+expandAddend)x or len
            int sz = buffer.capacity() + increment;
            if (buffer instanceof IntBuffer b) {
                if (useMemUtil)
                    return memRealloc(b, sz);
                return BufferUtils.createIntBuffer(sz).put(b);
            }
            if (buffer instanceof ByteBuffer b) {
                if (useMemUtil)
                    return memRealloc(b, sz);
                return BufferUtils.createByteBuffer(sz).put(b);
            }
        }
        return buffer;
    }

    private void createIB(int len) {
        if (indexBuffer == null) {
            final int sz = Math.max(len, 2);
            if (useMemUtil)
                indexBuffer = memAllocInt(sz);
            else
                indexBuffer = BufferUtils.createIntBuffer(sz);
        } else {
            indexBuffer = (IntBuffer) tryGrowBuffer(indexBuffer, len);
        }
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
    public GLBatch indexBefore(int... indices) {
        if (indices.length > 0) {
            createIB(indices.length);
            for (int index : indices) {
                indexBuffer.put(index + vertexCount);
            }
            indexCount += indices.length;
        }
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
    public GLBatch indexAfter(int... indices) {
        if (indices.length > 0) {
            createIB(indices.length);
            for (int index : indices) {
                indexBuffer.put(index + lastVertexCount);
            }
            indexCount += indices.length;
            lastVertexCount = vertexCount;
        }
        return this;
    }

    /**
     * Emit a vertex with the vertex layout.
     */
    public void emit() {
        vertexInfoList.add(new GLVertex(vertexInfo));
        ++vertexCount;
    }

    @Override
    public void emit(float x, float y, float z,
                     float r, float g, float b, float a,
                     float s, float t, float p,
                     float nx, float ny, float nz,
                     boolean color, boolean tex, boolean normal,
                     int i) {
        if (color) color(r, g, b, a);
        if (tex) texCoord(s, t, p);
        if (normal) normal(nx, ny, nz);
        vertex(x, y, z).emit();
    }

    /**
     * Puts a buffer.
     *
     * @param buf the buffer
     */
    public void buffer(ByteBuffer buf) {
        buffer = (ByteBuffer) tryGrowBuffer(buffer, buf.limit());
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
        indexBuffer = (IntBuffer) tryGrowBuffer(indexBuffer, buf.limit());
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
     * Return {@code true} if the vertex buffer of this batch is expanded.
     * <h4>Example</h4>
     * <pre>{@code boolean b = batch.isVtExpanded();
     * if (b) {
     *     CopyToBuffer(batch.getBuffer());
     * }
     * }</pre>
     *
     * @return is vertex buffer expanded
     */
    public boolean isVtExpanded() {
        return writtenBytes > lastWrittenBytes;
    }

    /**
     * Return {@code true} if the index buffer of this batch is expanded.
     * <h4>Example</h4>
     * <pre>{@code boolean b = batch.isIxExpanded();
     * if (b) {
     *     CopyToBuffer(batch.getIndexBuffer());
     * }
     * }</pre>
     *
     * @return is vertex buffer expanded
     */
    public boolean isIxExpanded() {
        return indexCount > lastIndexCount;
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

    /**
     * Convert the properties to bits.
     * <h4>Bit layout</h4>
     * <table><tr>
     *     <td>NormalBit</td>
     *     <td>TextureBit</td>
     *     <td>ColorBit</td>
     * </tr></table>
     *
     * @return the bits
     */
    public int propertiesToBits() {
        int ret = 0b000;
        if (hasColor())
            ret |= 0b001;
        if (hasTexture())
            ret |= 0b010;
        if (hasNormal())
            ret |= 0b100;
        return ret;
    }

    /**
     * Return {@code true} if the batch has same properties with the other batch.
     *
     * @param other the other batch
     * @return has same property
     */
    public boolean hasSameProperty(GLBatch other) {
        return hasColor() == other.hasColor()
               && hasTexture() == other.hasTexture()
               && hasNormal() == other.hasNormal();
    }

    /**
     * Get the vertex info list.
     *
     * @return the list
     */
    public List<GLVertex> getVertexInfoList() {
        return vertexInfoList;
    }

    @Override
    public void close() {
        if (destroyed)
            return;
        destroyed = true;
        if (useMemUtil) {
            memFree(buffer);
            buffer = null;
            memFree(indexBuffer);
            indexBuffer = null;
        }
    }
}
