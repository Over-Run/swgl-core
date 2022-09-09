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
import org.overrun.swgl.core.model.VertexFormat;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.StringJoiner;

import static org.overrun.swgl.core.model.IModel.color2byte;

/**
 * The batch vertex.
 *
 * @author squid233
 * @since 0.2.0
 */
public class GLVertex {
    public float x = 0.0f, y = 0.0f, z = 0.0f, w = 1.0f,
        s = 0.0f, t = 0.0f, p = 0.0f, q = 1.0f,
        nx = 0.0f, ny = 0.0f, nz = 1.0f;
    public byte r = -1, g = -1, b = -1, a = -1;

    public GLVertex() {
    }

    public GLVertex(GLVertex other) {
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

    public GLVertex copy() {
        return new GLVertex(this);
    }

    public GLVertex x(float x) {
        this.x = x;
        return this;
    }

    public GLVertex y(float y) {
        this.y = y;
        return this;
    }

    public GLVertex z(float z) {
        this.z = z;
        return this;
    }

    public GLVertex w(float w) {
        this.w = w;
        return this;
    }

    public GLVertex s(float s) {
        this.s = s;
        return this;
    }

    public GLVertex t(float t) {
        this.t = t;
        return this;
    }

    public GLVertex p(float p) {
        this.p = p;
        return this;
    }

    public GLVertex q(float q) {
        this.q = q;
        return this;
    }

    public GLVertex nx(float nx) {
        this.nx = nx;
        return this;
    }

    public GLVertex ny(float ny) {
        this.ny = ny;
        return this;
    }

    public GLVertex nz(float nz) {
        this.nz = nz;
        return this;
    }

    public GLVertex r(byte r) {
        this.r = r;
        return this;
    }

    public GLVertex g(byte g) {
        this.g = g;
        return this;
    }

    public GLVertex b(byte b) {
        this.b = b;
        return this;
    }

    public GLVertex a(byte a) {
        this.a = a;
        return this;
    }

    public GLVertex position(float x, float y, float z, float w) {
        return x(x).y(y).z(z).w(w);
    }

    public GLVertex position(float x, float y, float z) {
        return position(x, y, z, 1.0f);
    }

    public GLVertex position(float x, float y) {
        return position(x, y, 0.0f);
    }

    public GLVertex color(byte r, byte g, byte b, byte a) {
        return r(r).g(g).b(b).a(a);
    }

    public GLVertex color(byte r, byte g, byte b) {
        return color(r, g, b, -1);
    }

    public GLVertex color(float r, float g, float b, float a) {
        return color(color2byte(r),
            color2byte(g),
            color2byte(b),
            color2byte(a));
    }

    public GLVertex color(float r, float g, float b) {
        return color(r, g, b, 1.0f);
    }

    public GLVertex texCoords(float s, float t, float r, float q) {
        return s(s).t(t).p(r).q(q);
    }

    public GLVertex texCoords(float s, float t, float r) {
        return texCoords(s, t, r, 1.0f);
    }

    public GLVertex texCoords(float s, float t) {
        return texCoords(s, t, 0.0f);
    }

    public GLVertex normal(float nx, float ny, float nz) {
        return nx(nx).ny(ny).nz(nz);
    }

    public void processBuffer(VertexFormat format, ByteBuffer buffer) {
        if (format.hasPosition()) {
            format.processBuffer(buffer, x, y, z, w);
        } else if (format.hasColor()) {
            switch (format.getDataType()) {
                case FLOAT -> format.processBuffer(buffer,
                    IModel.byte2color(r),
                    IModel.byte2color(g),
                    IModel.byte2color(b),
                    IModel.byte2color(a));
                case UNSIGNED_BYTE -> format.processBuffer(buffer,
                    r, g, b, a);
            }
        } else if (format.hasTexture()) {
            format.processBuffer(buffer,
                s, t, p, q);
        } else if (format.hasNormal()) {
            switch (format.getDataType()) {
                case FLOAT -> format.processBuffer(buffer,
                    nx, ny, nz, null);
                case BYTE -> format.processBuffer(buffer,
                    IModel.normal2byte(nx),
                    IModel.normal2byte(ny),
                    IModel.normal2byte(nz),
                    null);
            }
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GLVertex.class.getSimpleName() + "[", "]")
            .add("position=(" + x + ", " + y + ", " + z + ", " + w + ")")
            .add("color=(" + Byte.toUnsignedInt(r) + ", " + Byte.toUnsignedInt(g) + ", " + Byte.toUnsignedInt(b) + ", " + Byte.toUnsignedInt(a) + ")")
            .add("texCoords=(" + s + ", " + t + ", " + p + ", " + q + ")")
            .add("normal=(" + nx + ", " + ny + ", " + nz + ")")
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GLVertex that = (GLVertex) o;
        return Float.compare(that.x, x) == 0 &&
               Float.compare(that.y, y) == 0 &&
               Float.compare(that.z, z) == 0 &&
               Float.compare(that.w, w) == 0 &&
               Float.compare(that.s, s) == 0 &&
               Float.compare(that.t, t) == 0 &&
               Float.compare(that.p, p) == 0 &&
               Float.compare(that.q, q) == 0 &&
               Float.compare(that.nx, nx) == 0 &&
               Float.compare(that.ny, ny) == 0 &&
               Float.compare(that.nz, nz) == 0 &&
               r == that.r &&
               g == that.g &&
               b == that.b &&
               a == that.a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w, s, t, p, q, nx, ny, nz, r, g, b, a);
    }
}
