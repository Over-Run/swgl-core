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

package org.overrun.swgl.core.gl.batch;

/**
 * The batch vertex.
 *
 * @author squid233
 * @since 0.2.0
 */
public class GLBatchVertex {
    public float x = 0.0f, y = 0.0f, z = 0.0f, w = 1.0f,
        s = 0.0f, t = 0.0f, p = 0.0f, q = 1.0f,
        nx = 0.0f, ny = 0.0f, nz = 1.0f;
    public byte r = -1, g = -1, b = -1, a = -1;

    public GLBatchVertex() {
    }

    public GLBatchVertex(GLBatchVertex other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
        s = other.s;
        t = other.t;
        p = other.p;
        q = other.q;
        nx = other.nx;
        ny = other.ny;
        nz = other.nz;
        r = other.r;
        g = other.g;
        b = other.b;
        a = other.a;
    }
}
