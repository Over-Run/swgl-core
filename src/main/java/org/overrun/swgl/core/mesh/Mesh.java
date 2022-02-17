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
import static org.overrun.swgl.core.gl.GLStateMgr.*;

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
    private boolean rendered;
    private Material material;

    public Mesh(ByteBuffer rawData,
                int vertexCount,
                ICleaner cleaner,
                int[] indices,
                Material material) {
        this(rawData, vertexCount, cleaner, indices);
        this.material = material;
    }

    public Mesh(ByteBuffer rawData,
                int vertexCount,
                ICleaner cleaner,
                int[] indices) {
        this.rawData = rawData;
        this.vertexCount = indices == null ? vertexCount : indices.length;
        this.cleaner = cleaner;
        this.indices = indices;
    }

    public void render(GLProgram program) {
        if (material != null) {
            for (int i = material.getMinUnit(),
                 u = material.getMaxUnit() + 1; i < u; i++) {
                var tex = material.getTexture(i);
                if (tex != null) {
                    activeTexture2D(i);
                    tex.bind();
                }
            }
        }
        if (ENABLE_CORE_PROFILE) {
            if (!glIsVertexArray(vao)) {
                vao = glGenVertexArrays();
            }
            glBindVertexArray(vao);
        }
        if (!glIsBuffer(vbo))
            vbo = glGenBuffers();
        if (!rendered || !ENABLE_CORE_PROFILE) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, rawData, GL_STATIC_DRAW);
            if (indices != null) {
                if (!glIsBuffer(ebo))
                    ebo = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
            }
            program.getLayout().beginDraw(program);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        if (indices != null)
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        else
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        if (!ENABLE_CORE_PROFILE) {
            program.getLayout().endDraw(program);
            if (indices != null)
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        if (ENABLE_CORE_PROFILE)
            glBindVertexArray(0);
        rendered = true;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void close() {
        cleaner.free(rawData);
        if (ENABLE_CORE_PROFILE) {
            if (glIsVertexArray(vao))
                glDeleteVertexArrays(vao);
        }
        if (glIsBuffer(vbo))
            glDeleteBuffers(vbo);
        if (glIsBuffer(ebo))
            glDeleteBuffers(ebo);
    }
}
