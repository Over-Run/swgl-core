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

package org.overrun.swgl.core.io;

import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * The heap in auto-closeable.
 *
 * <h2>Example</h2>
 * <pre>{@code try (var heap = new HeapStackFrame()) {
 *     var bb = heap.utilMemAlloc(8196).put(bytes).flip();
 *     ProcessBuffer(bb);
 * }}</pre>
 *
 * @author squid233
 * @since 0.1.0
 */
public class HeapStackFrame implements AutoCloseable {
    private final List<Buffer> utilBuffers = new ArrayList<>();

    /**
     * Alloc buffer using {@link MemoryUtil#memAlloc(int)}
     *
     * @param size The memory size
     * @return The buffer
     */
    public ByteBuffer utilMemAlloc(int size) {
        var buf = memAlloc(size);
        utilBuffers.add(buf);
        return buf;
    }

    /**
     * Alloc buffer using {@link MemoryUtil#memAllocInt(int)}
     *
     * @param size The memory size
     * @return The buffer
     */
    public IntBuffer utilMemAllocInt(int size) {
        var buf = memAllocInt(size);
        utilBuffers.add(buf);
        return buf;
    }

    /**
     * Alloc buffer using {@link MemoryUtil#memAllocLong(int)}
     *
     * @param size The memory size
     * @return The buffer
     */
    public LongBuffer utilMemAllocLong(int size) {
        var buf = memAllocLong(size);
        utilBuffers.add(buf);
        return buf;
    }

    /**
     * Alloc buffer using {@link MemoryUtil#memAllocDouble(int)}
     *
     * @param size The memory size
     * @return The buffer
     */
    public DoubleBuffer utilMemAllocDouble(int size) {
        var buf = memAllocDouble(size);
        utilBuffers.add(buf);
        return buf;
    }

    /**
     * Alloc buffer using {@link MemoryUtil#memAllocFloat(int)}
     *
     * @param size The memory size
     * @return The buffer
     */
    public FloatBuffer utilMemAllocFloat(int size) {
        var buf = memAllocFloat(size);
        utilBuffers.add(buf);
        return buf;
    }

    /**
     * Alloc buffer using {@link MemoryUtil#memAllocShort(int)}
     *
     * @param size The memory size
     * @return The buffer
     */
    public ShortBuffer utilMemAllocShort(int size) {
        var buf = memAllocShort(size);
        utilBuffers.add(buf);
        return buf;
    }

    @Override
    public void close() {
        for (var buffer : utilBuffers) {
            memFree(buffer);
        }
        utilBuffers.clear();
    }
}
