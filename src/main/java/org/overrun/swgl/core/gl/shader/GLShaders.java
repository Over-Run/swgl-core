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

package org.overrun.swgl.core.gl.shader;

import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.util.Pair;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL20C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLShaders {
    /**
     * Create and compile a GL shader.
     *
     * @param type    The shader type.
     * @param src     The shader source.
     * @param success The compiling status output.
     * @return the shader id
     * @since 0.2.0
     */
    public static int compile(GLShaderType type,
                              CharSequence src,
                              boolean[] success) {
        int shader = glCreateShader(type.getType());
        glShaderSource(shader, src);
        glCompileShader(shader);
        success[0] = (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_TRUE);
        return shader;
    }

    /**
     * Create shaders with key-values and link the program.
     *
     * @param program The program.
     * @param pairs   The key-values.
     * @return Is linking success.
     * @throws RuntimeException If failed to compile the shader.
     */
    @SafeVarargs
    public static boolean linkMapped(
        GLProgram program,
        Pair<GLShaderType, CharSequence>... pairs) throws RuntimeException {
        var shaders = new ArrayList<Integer>();
        var pStatus = new boolean[1];
        for (var pair : pairs) {
            var type = pair.left();
            var shader = compile(type, pair.right(), pStatus);
            shaders.add(shader);
            if (!pStatus[0])
                throw new RuntimeException("Failed to compile the " + type + ". " +
                                           glGetShaderInfoLog(shader));
            glAttachShader(program.getId(), shader);
        }

        boolean status = program.link();
        for (var shader : shaders) {
            glDetachShader(program.getId(), shader);
            glDeleteShader(shader);
        }
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
        CharSequence vertSrc,
        CharSequence fragSrc) throws RuntimeException {
        return linkMapped(program,
            Pair.of(GLShaderType.VERTEX_SHADER, vertSrc),
            Pair.of(GLShaderType.FRAGMENT_SHADER, fragSrc));
    }

    /**
     * Create simple shaders and link the program.
     *
     * @param program      The program.
     * @param vertFilename The vertex shader filename.
     * @param fragFilename The fragment shader filename.
     * @param provider     The file provider.
     * @return Is linking success.
     * @throws RuntimeException If failed to compile the shader.
     * @since 0.2.0
     */
    public static boolean linkSimple(
        GLProgram program,
        String vertFilename,
        String fragFilename,
        IFileProvider provider) throws RuntimeException {
        return linkSimple(program,
            PlainTextAsset.createStr(vertFilename, provider),
            PlainTextAsset.createStr(fragFilename, provider));
    }
}
