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

package org.overrun.swgl.core.mesh;

import org.overrun.swgl.core.gl.GLDataType;
import org.overrun.swgl.core.gl.GLProgram;

import static org.lwjgl.opengl.GL20C.*;
import static org.overrun.swgl.core.gl.GLDataType.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class VertexFormat {
    public static final VertexFormat POSITION_FMT = new VertexFormat(3, FLOAT, false);
    public static final VertexFormat COLOR_FMT = new VertexFormat(4, UNSIGNED_BYTE, true);
    public static final VertexFormat TEXTURE_FMT = new VertexFormat(2, FLOAT, false);
    public static final VertexFormat NORMAL_FMT = new VertexFormat(3, BYTE, true);
    private final int bytes;
    private final GLDataType dataType;
    private final int length;
    private final boolean normalized;

    public VertexFormat(int length,
                        GLDataType dataType,
                        boolean normalized) {
        this.normalized = normalized;
        this.bytes = dataType.getBytes() * length;
        this.dataType = dataType;
        this.length = length;
    }

    public int getBytes() {
        return bytes;
    }

    public GLDataType getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    public boolean isNormalized() {
        return normalized;
    }

    public void beginDraw(GLProgram program, String name) {
        var loc = program.getAttribLoc(name);
        glEnableVertexAttribArray(loc);
        program.attribPtr(name, this);
    }

    public void endDraw(GLProgram program, String name) {
        glDisableVertexAttribArray(program.getAttribLoc(name));
    }
}
