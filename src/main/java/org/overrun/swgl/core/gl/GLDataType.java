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

import static org.lwjgl.opengl.GL11C.*;

/**
 * The GL types enum.
 *
 * @author squid233
 * @since 0.1.0
 */
public enum GLDataType {
    /**
     * A byte type has length 1
     */
    BYTE("Byte", 1, GL_BYTE),
    /**
     * An unsigned byte type has length 1
     */
    UNSIGNED_BYTE("Unsigned Byte", 1, GL_UNSIGNED_BYTE),
    /**
     * A short type has length 2
     */
    SHORT("Short", 2, GL_SHORT),
    /**
     * An unsigned short type has length 2
     */
    UNSIGNED_SHORT("Unsigned Short", 2, GL_UNSIGNED_SHORT),
    /**
     * An integer type has length 4
     */
    INT("Int", 4, GL_INT),
    /**
     * An unsigned integer type has length 4
     */
    UNSIGNED_INT("Unsigned Int", 4, GL_UNSIGNED_INT),
    /**
     * A float type has length 4
     */
    FLOAT("Float", 4, GL_FLOAT),
    /**
     * A double type has length 8
     */
    DOUBLE("Double", 8, GL_DOUBLE);

    private final String name;
    private final int bytes;
    private final int dataType;

    GLDataType(String name, int bytes, int dataType) {
        this.name = name;
        this.bytes = bytes;
        this.dataType = dataType;
    }

    /**
     * Get the data type for OpenGL.
     *
     * @return The data type.
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Get the byte length.
     *
     * @return The bytes
     */
    public int getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return name;
    }
}
