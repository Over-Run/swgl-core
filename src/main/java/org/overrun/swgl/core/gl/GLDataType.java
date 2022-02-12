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
    BYTE("Byte", 1, GL_BYTE),
    UNSIGNED_BYTE("Unsigned Byte", 1, GL_UNSIGNED_BYTE),
    SHORT("Short", 2, GL_SHORT),
    UNSIGNED_SHORT("Unsigned Short", 2, GL_UNSIGNED_SHORT),
    INT("Int", 4, GL_INT),
    UNSIGNED_INT("Unsigned Int", 4, GL_UNSIGNED_INT),
    FLOAT("Float", 4, GL_FLOAT),
    DOUBLE("Double", 8, GL_DOUBLE);

    private final String name;
    private final int bytes;
    private final int dataType;

    GLDataType(String name, int bytes, int dataType) {
        this.name = name;
        this.bytes = bytes;
        this.dataType = dataType;
    }

    public int getDataType() {
        return dataType;
    }

    public int getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return name;
    }
}
