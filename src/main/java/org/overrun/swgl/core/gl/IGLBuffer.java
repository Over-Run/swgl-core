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

import org.lwjgl.opengl.GL15C;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL15C.*;

/**
 * GL buffer object interface.
 *
 * @author squid233
 * @since 0.2.0
 */
public interface IGLBuffer {
    /**
     * Delete and regenerate the buffer.
     */
    void regenerate();

    /**
     * Whether the buffer has generated.
     *
     * @return {@code true} if this buffer generated
     */
    boolean isGenerated();

    /**
     * Bind this buffer.
     *
     * @return this
     */
    IGLBuffer bind();


    /**
     * For stream-operation.
     *
     * @param t      the object
     * @param action The action to be performed
     * @param <T>    the object type
     * @return this
     */
    default <T> IGLBuffer withAction(T t, Consumer<T> action) {
        action.accept(t);
        return this;
    }

    /**
     * For stream-operation.
     *
     * @param action The action to be performed
     * @return this
     */
    default IGLBuffer withAction(Runnable action) {
        action.run();
        return this;
    }

    /**
     * Unbind this buffer.
     *
     * @return this
     */
    IGLBuffer unbind();

    /**
     * Get the buffer id.
     *
     * @return the buffer id
     */
    int id();

    /**
     * Get the buffer target.
     *
     * @return the buffer target
     */
    int target();

    /**
     * Set the buffer target.
     *
     * @param target the buffer target
     * @return this
     * @see #layout(int, int)
     */
    IGLBuffer target(int target);

    /**
     * Get the buffer usage.
     *
     * @return the buffer usage
     */
    int usage();

    /**
     * Set the buffer usage.
     *
     * @param usage the buffer usage; default to
     *              {@link GL15C#GL_STATIC_DRAW GL_STATIC_DRAW}
     * @return this
     * @see #layout(int, int)
     */
    IGLBuffer usage(int usage);

    /**
     * Set the buffer target and usage.
     *
     * @param target the buffer target
     * @param usage  the buffer usage
     * @return this
     * @see #target(int)
     * @see #usage(int)
     */
    default IGLBuffer layout(int target, int usage) {
        return target(target).usage(usage);
    }

    /**
     * {@code glBufferData}
     *
     * @param data the buffer data
     * @param func the function
     * @param <T>  the data type
     * @return this
     */
    <T> IGLBuffer data(T data, DataFunc<T> func);

    /**
     * {@link GL15C#glBufferData(int, long, int) glBufferData}
     *
     * @param size the buffer size to be allocated
     * @return this
     */
    IGLBuffer data(long size);

    /**
     * {@code glBufferSubData}
     *
     * @param offset the writing offset
     * @param data   the buffer data
     * @param func   the function
     * @param <T>    the data type
     * @return this
     */
    <T> IGLBuffer subData(long offset, T data, SubDataFunc<T> func);

    /**
     * {@link GL15C#glMapBuffer(int, int) glMapBuffer}
     *
     * @param access the access policy, indicating whether it will be possible
     *               to read from, write to, or both read from and write to the
     *               buffer object's mapped data store.
     * @return the mapped buffer of this buffer
     */
    ByteBuffer map(int access);

    /**
     * {@link GL15C#glMapBuffer(int, int, ByteBuffer) glMapBuffer}
     *
     * @param access    the access policy, indicating whether it will be possible
     *                  to read from, write to, or both read from and write to the
     *                  buffer object's mapped data store.
     * @param oldBuffer the old mapped buffer
     * @return the mapped buffer of this buffer or {@code oldBuffer}
     */
    ByteBuffer map(int access, ByteBuffer oldBuffer);

    /**
     * {@link GL15C#glMapBuffer(int, int, long, ByteBuffer) glMapBuffer}
     *
     * @param access    the access policy, indicating whether it will be possible
     *                  to read from, write to, or both read from and write to the
     *                  buffer object's mapped data store.
     * @param length    the buffer size
     * @param oldBuffer the old mapped buffer
     * @return the mapped buffer of this buffer or {@code oldBuffer}
     */
    ByteBuffer map(int access, long length, ByteBuffer oldBuffer);

    /**
     * {@link GL15C#glUnmapBuffer(int) glUnmapBuffer}
     *
     * @return {@link GL15C#glUnmapBuffer(int) UnmapBuffer}
     */
    boolean unmap();

    /**
     * Delete this buffer.
     */
    void delete();

    /**
     * {@code glBufferData} function.
     *
     * @param <T> the data type
     * @author squid233
     * @since 0.2.0
     */
    interface DataFunc<T> {
        /**
         * Call {@code glBufferData}
         *
         * @param target the buffer target
         * @param data   the buffer data
         * @param usage  the buffer usage
         */
        void bufferData(int target, T data, int usage);
    }

    /**
     * {@code glBufferSubData} function.
     *
     * @param <T> the data type
     * @author squid233
     * @since 0.2.0
     */
    interface SubDataFunc<T> {
        /**
         * Call {@code glBufferSubData}
         *
         * @param target the buffer target
         * @param offset the writing offset
         * @param data   the buffer data
         */
        void bufferSubData(int target, long offset, T data);
    }

    /**
     * A GL buffer object.
     *
     * @author squid233
     * @since 0.2.0
     */
    class Single implements AutoCloseable, IGLBuffer {
        private int id;
        private int target;
        private int usage = GL_STATIC_DRAW;
        private boolean deleted = false;

        /**
         * Generate a buffer.
         */
        public Single() {
            id = glGenBuffers();
        }

        @Override
        public void regenerate() {
            delete();
            id = glGenBuffers();
            deleted = false;
        }

        @Override
        public boolean isGenerated() {
            return id() > 0 && glIsBuffer(id());
        }

        @Override
        public Single bind() {
            glBindBuffer(target(), id());
            return this;
        }

        @Override
        public <T> Single withAction(T t, Consumer<T> action) {
            IGLBuffer.super.withAction(t, action);
            return this;
        }

        @Override
        public Single withAction(Runnable action) {
            IGLBuffer.super.withAction(action);
            return this;
        }

        @Override
        public Single unbind() {
            glBindBuffer(target(), 0);
            return this;
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public int target() {
            return target;
        }

        @Override
        public Single target(int target) {
            this.target = target;
            return this;
        }

        @Override
        public int usage() {
            return usage;
        }

        @Override
        public Single usage(int usage) {
            this.usage = usage;
            return this;
        }

        @Override
        public Single layout(int target, int usage) {
            IGLBuffer.super.layout(target, usage);
            return this;
        }

        @Override
        public <T> Single data(T data, DataFunc<T> func) {
            func.bufferData(target(), data, usage());
            return this;
        }

        @Override
        public Single data(long size) {
            glBufferData(target(), size, usage());
            return this;
        }

        @Override
        public <T> Single subData(long offset, T data, SubDataFunc<T> func) {
            func.bufferSubData(target(), offset, data);
            return this;
        }

        @Override
        public ByteBuffer map(int access) {
            return glMapBuffer(target(), access);
        }

        @Override
        public ByteBuffer map(int access, ByteBuffer oldBuffer) {
            return glMapBuffer(target(), access, oldBuffer);
        }

        @Override
        public ByteBuffer map(int access, long length, ByteBuffer oldBuffer) {
            return glMapBuffer(target(), access, length, oldBuffer);
        }

        @Override
        public boolean unmap() {
            return glUnmapBuffer(target());
        }

        /**
         * Whether this buffer has deleted.
         *
         * @return {@code true} if the buffer deleted
         */
        public boolean deleted() {
            return deleted;
        }

        @Override
        public void delete() {
            if (!deleted()) {
                glDeleteBuffers(id());
                id = 0;
                deleted = true;
            }
        }

        @Override
        public void close() {
            delete();
        }
    }

    /**
     * GL buffer objects.
     *
     * @author squid233
     * @since 0.2.0
     */
    class Array implements AutoCloseable, IGLBuffer {
        private final Single[] buffers;

        public Array(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("Buffer count <= 0");
            }
            buffers = new Single[count];
            for (int i = 0; i < count; i++) {
                buffers[i] = new Single();
            }
        }

        /**
         * Get the first buffer.
         *
         * @return the first buffer of the array
         */
        public Single get() {
            return buffers[0];
        }

        /**
         * Get a buffer by the index.
         *
         * @param index the index
         * @return the buffer
         */
        public Single get(int index) {
            return buffers[index];
        }

        @Override
        public void regenerate() {
            regenerate(0);
        }

        public void regenerate(int index) {
            get(index).regenerate();
        }

        public void regenerateAll() {
            for (var buf : buffers) {
                buf.regenerate();
            }
        }

        @Override
        public boolean isGenerated() {
            return isGenerated(0);
        }

        public boolean isGenerated(int index) {
            return get(index).isGenerated();
        }

        @Override
        public Array bind() {
            return bind(0);
        }

        public Array bind(int index) {
            get(index).bind();
            return this;
        }

        @Override
        public <T> Array withAction(T t, Consumer<T> action) {
            return withAction(0, t, action);
        }

        public <T> Array withAction(int index, T t, Consumer<T> action) {
            get(index).withAction(t, action);
            return this;
        }

        @Override
        public Array withAction(Runnable action) {
            return withAction(0, action);
        }

        public Array withAction(int index, Runnable action) {
            get(index).withAction(action);
            return this;
        }

        @Override
        public Array unbind() {
            return unbind(0);
        }

        public Array unbind(int index) {
            get(index).unbind();
            return this;
        }

        @Override
        public int id() {
            return id(0);
        }

        public int id(int index) {
            return get(index).id();
        }

        @Override
        public int target() {
            return getTarget(0);
        }

        public int getTarget(int index) {
            return get(index).target();
        }

        @Override
        public Array target(int target) {
            return target(0, target);
        }

        public Array target(int index, int target) {
            get(index).target(target);
            return this;
        }

        @Override
        public int usage() {
            return getUsage(0);
        }

        public int getUsage(int index) {
            return get(index).usage();
        }

        @Override
        public Array usage(int usage) {
            return usage(0, usage);
        }

        public Array usage(int index, int usage) {
            get(index).usage(usage);
            return this;
        }

        @Override
        public Array layout(int target, int usage) {
            return layout(0, target, usage);
        }

        public Array layout(int index, int target, int usage) {
            get(index).target(target).usage(usage);
            return this;
        }

        @Override
        public <T> Array data(T data, DataFunc<T> func) {
            return data(0, data, func);
        }

        public <T> Array data(int index, T data, DataFunc<T> func) {
            get(index).data(data, func);
            return this;
        }

        @Override
        public Array data(long size) {
            return data(0, size);
        }

        public Array data(int index, long size) {
            get(index).data(size);
            return this;
        }

        @Override
        public <T> Array subData(long offset, T data, SubDataFunc<T> func) {
            return subData(0, offset, data, func);
        }

        public <T> Array subData(int index, long offset, T data, SubDataFunc<T> func) {
            get(index).subData(offset, data, func);
            return this;
        }

        @Override
        public ByteBuffer map(int access) {
            return map(0, access);
        }

        public ByteBuffer map(int index, int access) {
            return get(index).map(access);
        }

        @Override
        public ByteBuffer map(int access, ByteBuffer oldBuffer) {
            return map(0, access, oldBuffer);
        }

        public ByteBuffer map(int index, int access, ByteBuffer oldBuffer) {
            return get(index).map(access, oldBuffer);
        }

        @Override
        public ByteBuffer map(int access, long length, ByteBuffer oldBuffer) {
            return map(0, access, length, oldBuffer);
        }

        public ByteBuffer map(int index, int access, long length, ByteBuffer oldBuffer) {
            return get(index).map(access, length, oldBuffer);
        }

        @Override
        public boolean unmap() {
            return unmap(0);
        }

        public boolean unmap(int index) {
            return get(index).unmap();
        }

        public boolean deleted(int index) {
            return get(index).deleted();
        }

        public boolean allDeleted() {
            for (var buf : buffers) {
                if (!buf.deleted())
                    return false;
            }
            return true;
        }

        /**
         * {@inheritDoc}
         *
         * @deprecated This method is easily confused with {@link #deleteAll()} and {@link #close()}.
         */
        @Override
        @Deprecated(since = "0.2.0")
        public void delete() {
            delete(0);
        }

        public void delete(int index) {
            get(index).delete();
        }

        public void deleteAll() {
            for (var buf : buffers) {
                buf.delete();
            }
        }

        /**
         * {@inheritDoc}
         *
         * @deprecated This method is easily confused with {@link #delete()}.
         */
        @Override
        @Deprecated(since = "0.2.0")
        public void close() {
            deleteAll();
        }

        public void close(int index) {
            delete(index);
        }
    }
}
