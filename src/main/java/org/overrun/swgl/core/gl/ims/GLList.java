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

package org.overrun.swgl.core.gl.ims;

import org.overrun.swgl.core.gl.GLDrawMode;

import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glIsBuffer;

/**
 * The IMS OpenGL list includes the buffers.
 *
 * @author squid233
 * @since 0.1.0
 */
@Deprecated(since = "0.2.0", forRemoval = true)
public class GLList implements AutoCloseable {
    private final int id;
    GLDrawMode drawMode;
    int vertexCount;
    int indexCount;
    int vbo, ebo;

    public GLList(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public GLDrawMode getDrawMode() {
        return drawMode;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public int getVbo() {
        return vbo;
    }

    public int getEbo() {
        return ebo;
    }

    @Override
    public void close() {
        if (glIsBuffer(vbo)) {
            glDeleteBuffers(vbo);
            vbo = 0;
        }
        if (glIsBuffer(ebo)) {
            glDeleteBuffers(ebo);
            ebo = 0;
        }
    }
}
