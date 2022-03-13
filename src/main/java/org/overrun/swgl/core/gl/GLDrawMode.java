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

import org.jetbrains.annotations.Contract;

import static org.lwjgl.opengl.GL40C.*;

/**
 * The OpenGL drawing modes.
 *
 * @author squid233
 * @since 0.1.0
 */
public enum GLDrawMode {
    POINTS(GL_POINTS, false),
    LINES(GL_LINES, false),
    LINE_LOOP(GL_LINE_LOOP, false),
    LINE_STRIP(GL_LINE_STRIP, false),
    TRIANGLES(GL_TRIANGLES, false),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP, false),
    TRIANGLE_FAN(GL_TRIANGLE_FAN, false),
    QUADS(GL_TRIANGLES, true),
//    QUAD_STRIP(GL_QUAD_STRIP, true),
//    POLYGON(GL_POLYGON, true),
    LINES_ADJACENCY(GL_LINES_ADJACENCY, false),
    LINE_STRIP_ADJACENCY(GL_LINE_STRIP_ADJACENCY, false),
    TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY, false),
    TRIANGLE_STRIP_ADJACENCY(GL_TRIANGLE_STRIP_ADJACENCY, false),
    PATCHES(GL_PATCHES, false);

    private final int glType;
    private final boolean isNotCore;

    @Contract(pure = true)
    GLDrawMode(int glType, boolean isNotCore) {
        this.glType = glType;
        this.isNotCore = isNotCore;
    }

    @Contract(pure = true)
    public int getGlType() {
        return glType;
    }

    @Contract(pure = true)
    public boolean isNotCore() {
        return isNotCore;
    }
}
