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

import org.lwjgl.opengl.GL20C;
import org.overrun.swgl.core.util.math.Numbers;

import java.util.ArrayList;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Shaders {
    /**
     * Create shaders with key-values and link the program.
     *
     * @param program The program.
     * @param kvs     The key-values. The length must be an even number.
     *                <p>The elements are in order with:
     *                {@code GLShaderType, String, GLShaderType, String...}</p>
     * @return Is linking success.
     * @throws RuntimeException If failed to compile the shader.
     */
    public static boolean linkMapped(
        GLProgram program,
        Object... kvs) throws RuntimeException {
        Numbers.checkEven(kvs.length);
        var shaders = new ArrayList<Shader>();
        for (int i = 0; i < kvs.length; ) {
            var type = (GLShaderType) kvs[i++];
            var src = String.valueOf(kvs[i++]);
            var shader = new Shader();
            shaders.add(shader);
            shader.create(type);
            shader.attachTo(program);
            shader.setSource(src);
            if (!shader.compile())
                throw new RuntimeException("Failed to compile the " + type + ". " +
                    GL20C.glGetShaderInfoLog(shader.getId()));
        }

        boolean status = program.link();
        for (var shader : shaders)
            shader.free(program);
        return status;
    }

    /**
     * Create simple shaders and link the program.
     *
     * @param program The program.
     * @param vertSrc The vertex shader source.
     * @param fragSrc The fragment shader source.
     * @return Is linking success.
     * @throws RuntimeException If failed to compile the shader.
     */
    public static boolean linkSimple(
        GLProgram program,
        String vertSrc,
        String fragSrc) throws RuntimeException {
        return linkMapped(program,
            GLShaderType.VERTEX_SHADER, vertSrc,
            GLShaderType.FRAGMENT_SHADER, fragSrc);
    }
}
