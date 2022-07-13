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

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL30C.*;

/**
 * A GL vertex array.
 *
 * @author squid233
 * @since 0.2.0
 */
public class GLVao implements AutoCloseable {
    private int id;
    private boolean deleted = false;

    /**
     * Generate a vertex array.
     */
    public GLVao() {
        id = glGenVertexArrays();
    }

    /**
     * Delete and regenerate the vertex array.
     */
    public void regenerate() {
        delete();
        id = glGenVertexArrays();
        deleted = false;
    }

    /**
     * Whether the vertex array has generated.
     *
     * @return {@code true} if this vertex array generated
     */
    public boolean isGenerated() {
        return id() > 0 && glIsVertexArray(id());
    }

    /**
     * Bind this vertex array.
     *
     * @return this
     */
    public GLVao bind() {
        glBindVertexArray(id());
        return this;
    }

    /**
     * For stream-operation.
     * <h4>Example</h4>
     * <pre>{@code vao = new GLVao()
     *     .bind()
     *     .withAction(obj, MyObject::set)
     *     .unbind();}</pre>
     *
     * @param t      the object
     * @param action The action to be performed
     * @param <T>    the object type
     * @return this
     */
    public <T> GLVao withAction(T t, Consumer<T> action) {
        action.accept(t);
        return this;
    }

    /**
     * For stream-operation.
     * <h4>Example</h4>
     * <pre>{@code vao = new GLVao()
     *     .bind()
     *     .withAction(() -> obj.set())
     *     .unbind();}</pre>
     *
     * @param action the action to be performed
     * @return this
     */
    public GLVao withAction(Runnable action) {
        action.run();
        return this;
    }

    /**
     * Unbind this vertex array.
     *
     * @return this
     */
    public GLVao unbind() {
        glBindVertexArray(0);
        return this;
    }

    /**
     * Get the vertex array id.
     *
     * @return the vertex array id
     */
    public int id() {
        return id;
    }

    /**
     * Whether this vertex array has deleted.
     *
     * @return {@code true} if the vertex array deleted
     */
    public boolean deleted() {
        return deleted;
    }

    /**
     * Delete this vertex array.
     */
    public void delete() {
        if (!deleted()) {
            glDeleteVertexArrays(id());
            id = 0;
            deleted = true;
        }
    }

    @Override
    public void close() {
        delete();
    }
}
