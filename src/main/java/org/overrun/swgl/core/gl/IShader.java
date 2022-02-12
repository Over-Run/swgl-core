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

import java.util.function.Function;

/**
 * @author squid233
 * @since 0.1.0
 */
public interface IShader {
    void create(GLShaderType type);

    void setSource(String src);

    default String getSource() {
        return null;
    }

    int getId();

    /**
     * Validate this shader.
     *
     * @param function The function.
     *                 Param: The shader id.
     *                 Return: Should throw an exception.
     * @param t        The exception.
     * @throws Throwable Thrown by param {@code t}.
     */
    default void validate(Function<Integer, Boolean> function,
                          Throwable t) throws Throwable {
        if (function.apply(getId()))
            throw t;
    }

    boolean compile();

    void attachTo(GLProgram program);

    /**
     * Detach and delete this shader.
     */
    void free(GLProgram program);
}
