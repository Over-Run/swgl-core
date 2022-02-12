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

import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Shaders {
    public static boolean linkSimple(
        GLProgram program,
        String vertSrc,
        String fragSrc) throws RuntimeException {
        // Create the vertex shader
        var vert = new Shader();
        vert.create(GLShaderType.VERTEX_SHADER);
        vert.attachTo(program);
        vert.setSource(vertSrc);
        if (!vert.compile()) {
            throw new RuntimeException("Failed to create the vertex shader. " +
                glGetShaderInfoLog(vert.getId()));
        }

        // Create the fragment shader
        var frag = new Shader();
        frag.create(GLShaderType.FRAGMENT_SHADER);
        frag.attachTo(program);
        frag.setSource(fragSrc);
        if (!frag.compile()) {
            throw new RuntimeException("Failed to create the fragment shader. " +
                glGetShaderInfoLog(frag.getId()));
        }

        var status = program.link();
        frag.free(program);
        vert.free(program);
        return status;
    }
}
