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

import org.overrun.swgl.core.mesh.VertexFormat;
import org.overrun.swgl.core.mesh.VertexLayout;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public abstract class GLProgram implements AutoCloseable {
    private final Map<String, Integer> attribLocations = new HashMap<>();
    private final Map<String, GLUniform> uniformMap = new HashMap<>();
    protected int id;

    /**
     * Default GLProgram with a layout
     *
     * @author squid233
     * @since 0.1.0
     */
    public static class Default extends GLProgram {
        public final VertexLayout layout;

        public Default(VertexLayout layout) {
            this.layout = layout;
        }

        @Override
        public VertexLayout getLayout() {
            return layout;
        }
    }

    public abstract VertexLayout getLayout();

    public boolean create() {
        id = glCreateProgram();
        return glIsProgram(id);
    }

    public boolean link() {
        glLinkProgram(id);
        return glGetProgrami(id, GL_LINK_STATUS) == GL_TRUE;
    }

    public String getInfoLog() {
        return glGetProgramInfoLog(id);
    }

    public int getAttribLoc(String name) {
        return attribLocations.computeIfAbsent(name,
            s -> glGetAttribLocation(id, name));
    }

    public void enableAttrib(String name) {
        glEnableVertexAttribArray(getAttribLoc(name));
    }

    public void disableAttrib(String name) {
        glDisableVertexAttribArray(getAttribLoc(name));
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void attribPtr(String name,
                          VertexFormat format) {
        glVertexAttribPointer(
            getAttribLoc(name),
            format.getLength(),
            format.getDataType().getDataType(),
            format.isNormalized(),
            getLayout().getStride(),
            getLayout().getOffset(format)
        );
    }

    public void createUniform(String name, GLUniformType type) {
        uniformMap.put(name, new GLUniform(glGetUniformLocation(id, name), type));
    }

    public GLUniform getUniform(String name) {
        return uniformMap.get(name);
    }

    public void updateUniforms() {
        for (var uni : uniformMap.values()) {
            uni.upload();
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public void close() {
        for (var uni : uniformMap.values()) {
            uni.close();
        }
        glDeleteProgram(id);
    }
}
