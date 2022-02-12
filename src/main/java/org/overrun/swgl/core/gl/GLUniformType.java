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

/**
 * @author squid233
 * @since 0.1.0
 */
public enum GLUniformType {
    F1(4), F2(8), F3(12), F4(16),
    I1(4), I2(8), I3(12), I4(16),
    UI1(4), UI2(8), UI3(12), UI4(16),
    D1(8), D2(16), D3(24), D4(32),
    M2F(16), M3F(36), M4F(64),
    M2X3F(24), M3X2F(24),
    M2X4F(32), M4X2F(32),
    M3X4F(48), M4X3F(48),
    M2D(32), M3D(72), M4D(128),
    M2X3D(48), M3X2D(48),
    M2X4D(64), M4X2D(64),
    M3X4D(96), M4X3D(96);

    private final int byteLength;

    GLUniformType(int byteLength) {
        this.byteLength = byteLength;
    }

    public int getByteLength() {
        return byteLength;
    }
}
