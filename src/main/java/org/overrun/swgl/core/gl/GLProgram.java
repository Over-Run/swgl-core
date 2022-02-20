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
public class GLProgram implements AutoCloseable {
    private final Map<String, Integer> attribLocations = new HashMap<>();
    private final Map<CharSequence, GLUniform> uniformMap = new HashMap<>();
    private VertexLayout layout;
    /**
     * The program GL id.
     */
    protected int id;

    /**
     * Construct the program with the specified vertex layout.
     *
     * @param layout The vertex layout.
     */
    public GLProgram(VertexLayout layout) {
        this.layout = layout;
    }

    /**
     * Construct the program without vertex layout.
     * You have to set the layout manually.
     */
    public GLProgram() {
    }

    /**
     * Get the vertex layout.
     *
     * @return The vertex layout.
     */
    public VertexLayout getLayout() {
        return layout;
    }

    /**
     * Set the vertex layout.
     *
     * @param layout The vertex layout.
     */
    public void setLayout(VertexLayout layout) {
        this.layout = layout;
    }

    /**
     * Create the program.
     *
     * @return Is creating success
     */
    public boolean create() {
        id = glCreateProgram();
        return glIsProgram(id);
    }

    /**
     * Link this program.
     *
     * @return Is linking success
     */
    public boolean link() {
        glLinkProgram(id);
        return glGetProgrami(id, GL_LINK_STATUS) == GL_TRUE;
    }

    /**
     * Get the info log from this program.
     *
     * @return The info log.
     */
    public String getInfoLog() {
        return glGetProgramInfoLog(id);
    }

    /**
     * Get the attribute location.
     *
     * @param name The attribute name.
     * @return The location.
     */
    public int getAttribLoc(String name) {
        return attribLocations.computeIfAbsent(name,
            s -> glGetAttribLocation(id, name));
    }

    /**
     * Enable an attribute.
     *
     * @param name The attribute name.
     */
    public void enableAttrib(String name) {
        glEnableVertexAttribArray(getAttribLoc(name));
    }

    /**
     * Disable an attribute.
     *
     * @param name The attribute name.
     */
    public void disableAttrib(String name) {
        glDisableVertexAttribArray(getAttribLoc(name));
    }

    /**
     * Use this program.
     */
    public void bind() {
        GLStateMgr.useProgram(id);
    }

    /**
     * Drop this program.
     */
    public void unbind() {
        GLStateMgr.useProgram(0);
    }

    /**
     * Set the vertex attribute pointer.
     *
     * @param name   The attribute name.
     * @param format The vertex format of the attribute.
     */
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

    /**
     * Get the uniform location.
     *
     * @param name The uniform name.
     * @return The location.
     */
    public int getUniformLoc(CharSequence name) {
        return glGetUniformLocation(id, name);
    }

    /**
     * Create a uniform.
     *
     * @param name The uniform name.
     * @param type The uniform type.
     */
    public void createUniform(CharSequence name, GLUniformType type) {
        int loc = getUniformLoc(name);
        if (loc == -1)
            throw new IllegalArgumentException("Couldn't found uniform '"
                + name
                + "' for program "
                + id
                + "#"
                + this
                + "!");
        uniformMap.put(name, new GLUniform(loc, type));
    }

    /**
     * Get the uniform.
     *
     * @param name The uniform name.
     * @return The uniform. Null if not created.
     */
    public GLUniform getUniform(CharSequence name) {
        return uniformMap.get(name);
    }

    /**
     * Get the uniform. Will auto create a uniform when it's absent.
     *
     * @param name The uniform name.
     * @param type The uniform type.
     * @return The uniform.
     */
    public GLUniform getUniformSafe(CharSequence name, GLUniformType type) {
        if (!uniformMap.containsKey(name))
            createUniform(name, type);
        return getUniform(name);
    }

    /**
     * Update all dirty uniforms.
     */
    public void updateUniforms() {
        for (var uni : uniformMap.values()) {
            uni.upload();
        }
    }

    /**
     * Get the program id.
     *
     * @return The GL id.
     */
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
