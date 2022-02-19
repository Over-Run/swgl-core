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
 * The GL uniform types enum.
 *
 * @author squid233
 * @since 0.1.0
 */
public enum GLUniformType {
    /**
     * The 1 float uniform.
     */
    F1(4),
    /**
     * The 2 floats uniform.
     */
    F2(8),
    /**
     * The 3 floats uniform.
     */
    F3(12),
    /**
     * The 4 floats uniform.
     */
    F4(16),
    /**
     * The 1 int uniform.
     */
    I1(4),
    /**
     * The 2 ints uniform.
     */
    I2(8),
    /**
     * The 3 ints uniform.
     */
    I3(12),
    /**
     * The 4 ints uniform.
     */
    I4(16),
    /**
     * The 1 unsigned int uniform.
     */
    UI1(4),
    /**
     * The 2 unsigned ints uniform.
     */
    UI2(8),
    /**
     * The 3 unsigned ints uniform.
     */
    UI3(12),
    /**
     * The 4 unsigned ints uniform.
     */
    UI4(16),
    /**
     * The 1 double uniform.
     */
    D1(8),
    /**
     * The 2 doubles uniform.
     */
    D2(16),
    /**
     * The 3 doubles uniform.
     */
    D3(24),
    /**
     * The 4 doubles uniform.
     */
    D4(32),
    /**
     * The 2&times;2 floats matrix uniform.
     */
    M2F(16),
    /**
     * The 3&times;3 floats matrix uniform.
     */
    M3F(36),
    /**
     * The 4&times;4 floats matrix uniform.
     */
    M4F(64),
    /**
     * The 2&times;3 floats matrix uniform.
     */
    M2X3F(24),
    /**
     * The 3&times;2 floats matrix uniform.
     */
    M3X2F(24),
    /**
     * The 2&times;4 floats matrix uniform.
     */
    M2X4F(32),
    /**
     * The 4&times;2 floats matrix uniform.
     */
    M4X2F(32),
    /**
     * The 3&times;4 floats matrix uniform.
     */
    M3X4F(48),
    /**
     * The 4&times;3 floats matrix uniform.
     */
    M4X3F(48),
    /**
     * The 2&times;2 doubles matrix uniform.
     */
    M2D(32),
    /**
     * The 3&times;3 doubles matrix uniform.
     */
    M3D(72),
    /**
     * The 4&times;4 doubles matrix uniform.
     */
    M4D(128),
    /**
     * The 2&times;3 doubles matrix uniform.
     */
    M2X3D(48),
    /**
     * The 3&times;2 doubles matrix uniform.
     */
    M3X2D(48),
    /**
     * The 2&times;4 doubles matrix uniform.
     */
    M2X4D(64),
    /**
     * The 4&times;2 doubles matrix uniform.
     */
    M4X2D(64),
    /**
     * The 3&times;4 doubles matrix uniform.
     */
    M3X4D(96),
    /**
     * The 4&times;3 doubles matrix uniform.
     */
    M4X3D(96);

    private final int byteLength;
    private final boolean isMatrix;

    GLUniformType(int byteLength) {
        this.byteLength = byteLength;
        isMatrix = name().startsWith("M");
    }

    /**
     * Get the byte length of the type.
     *
     * @return the byte length
     */
    public int getByteLength() {
        return byteLength;
    }

    /**
     * True if this is matrix type.
     *
     * @return is this a matrix type
     */
    public boolean isMatrix() {
        return isMatrix;
    }
}
