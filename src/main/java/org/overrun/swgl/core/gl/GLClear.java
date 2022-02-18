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

import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11C.*;

/**
 * GL clear functions for users.
 *
 * @author squid233
 * @since 0.1.0
 */
public class GLClear {
    /**
     * AttribMask
     */
    public static final int COLOR_BUFFER_BIT = GL_COLOR_BUFFER_BIT;
    /**
     * AttribMask
     */
    public static final int DEPTH_BUFFER_BIT = GL_DEPTH_BUFFER_BIT;
    /**
     * AttribMask
     */
    public static final int STENCIL_BUFFER_BIT = GL_STENCIL_BUFFER_BIT;

    /**
     * Call {@link GL11C#glClear glClear}
     *
     * @param mask The mask
     */
    public static void clear(int mask) {
        glClear(mask);
    }

    /**
     * Call {@link GL11C#glClearColor glClearColor}
     *
     * @param r Red value
     * @param g Green value
     * @param b Blue value
     * @param a Alpha value
     */
    public static void clearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    /**
     * Call {@link GL11C#glClearDepth glClearDepth}
     *
     * @param depth The depth
     */
    public static void clearDepth(double depth) {
        glClearDepth(depth);
    }

    /**
     * Call {@link GL11C#glClearStencil glClearStencil}
     *
     * @param s The stencil
     */
    public static void clearStencil(int s) {
        glClearStencil(s);
    }
}
