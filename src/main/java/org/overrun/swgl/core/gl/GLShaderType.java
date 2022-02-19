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

import java.util.Locale;

import static org.lwjgl.opengl.GL40C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public enum GLShaderType {
    /**
     * The vertex shader.
     */
    VERTEX_SHADER(GL_VERTEX_SHADER),
    /**
     * The fragment shader.
     */
    FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
    /**
     * The geometry shader.
     */
    GEOMETRY_SHADER(GL_GEOMETRY_SHADER),
    /**
     * The tess control shader.
     */
    TESS_CONTROL_SHADER(GL_TESS_CONTROL_SHADER),
    /**
     * The tess evaluation shader.
     */
    TESS_EVALUATION_SHADER(GL_TESS_EVALUATION_SHADER);

    private final String name;
    private final int type;

    GLShaderType(int type) {
        this.name = name().toLowerCase(Locale.ROOT).replaceAll("_", " ");
        this.type = type;
    }

    /**
     * Get the shader type name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the GL type.
     *
     * @return The type.
     */
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
