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

import static org.lwjgl.opengl.GL20C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Shader implements IShader {
    protected int id;
    protected String source;

    @Override
    public void create(GLShaderType type) {
        id = glCreateShader(type.getType());
    }

    @Override
    public void setSource(String source) {
        this.source = source;
        glShaderSource(id, source);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean compile() {
        glCompileShader(id);
        return glGetShaderi(id, GL_COMPILE_STATUS) == GL_TRUE;
    }

    @Override
    public void attachTo(GLProgram program) {
        glAttachShader(program.getId(), id);
    }

    @Override
    public void free(GLProgram program) {
        glDetachShader(program.getId(), id);
        glDeleteShader(id);
    }
}
