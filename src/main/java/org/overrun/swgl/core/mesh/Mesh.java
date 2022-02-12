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

import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.io.ICleaner;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30C.*;
import static org.overrun.swgl.core.gl.GLStateMgr.ENABLE_CORE_PROFILE;

/**
 * A swgl mesh.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Mesh implements AutoCloseable {
    private final ByteBuffer rawData;
    private final int vertexCount;
    private final ICleaner cleaner;
    private final int[] indices;
    private int vao, vbo, ebo;

    public Mesh(ByteBuffer rawData,
                int vertexCount,
                ICleaner cleaner,
                int[] indices) {
        this.rawData = rawData;
        this.vertexCount = indices == null ? vertexCount : (vertexCount < 3 ? indices.length : vertexCount);
        this.cleaner = cleaner;
        this.indices = indices;
    }

    public void render(GLProgram program) {
        if (ENABLE_CORE_PROFILE) {
            if (!glIsVertexArray(vao)) {
                vao = glGenVertexArrays();
            }
            glBindVertexArray(vao);
        }
        if (!glIsBuffer(vbo))
            vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, rawData, GL_STATIC_DRAW);
        if (indices != null) {
            if (!glIsBuffer(ebo))
                ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }
        program.getLayout().beginDraw();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        if (indices != null)
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        else
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        program.getLayout().endDraw();
        if (ENABLE_CORE_PROFILE)
            glBindVertexArray(0);
    }

    @Override
    public void close() {
        cleaner.free(rawData);
    }
}
