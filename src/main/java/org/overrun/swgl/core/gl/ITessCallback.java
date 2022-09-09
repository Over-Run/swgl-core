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

import org.overrun.swgl.core.model.IModel;

/**
 * The tesselator callback to emit vertices.
 *
 * @author squid233
 * @since 0.1.0
 */
@FunctionalInterface
public interface ITessCallback {
    /**
     * Emit a vertex.
     *
     * @param x      the pos x
     * @param y      the pos y
     * @param z      the pos z
     * @param w      the pos w
     * @param r      the color red
     * @param g      the color green
     * @param b      the color blue
     * @param a      the color alpha
     * @param s      the texture-coord s
     * @param t      the texture-coord t
     * @param p      the texture-coord r
     * @param q      the texture-coord q
     * @param nx     the normal x
     * @param ny     the normal y
     * @param nz     the normal z
     * @param color  has color
     * @param tex    has tex-coord
     * @param normal has normal
     * @param i      the vertex index
     */
    void emit(float x, float y, float z, float w,
              float r, float g, float b, float a,
              float s, float t, float p, float q,
              float nx, float ny, float nz,
              boolean color, boolean tex, boolean normal,
              int i);

    default void emit(GLVertex vertex,
                      boolean color, boolean tex, boolean normal,
                      int i) {
        emit(vertex.x, vertex.y, vertex.z, vertex.w,
            IModel.byte2color(vertex.r), IModel.byte2color(vertex.g), IModel.byte2color(vertex.b), IModel.byte2color(vertex.a),
            vertex.s, vertex.t, vertex.p, vertex.q,
            vertex.nx, vertex.ny, vertex.nz,
            color, tex, normal,
            i);
    }
}
