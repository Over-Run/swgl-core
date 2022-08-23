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

package org.overrun.swgl.core.model.simple;

import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.gl.GLVertex;
import org.overrun.swgl.core.model.VertexLayout;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.swgl.core.gl.GLStateMgr.activeTexture;
import static org.overrun.swgl.core.model.IModel.byte2color;
import static org.overrun.swgl.core.model.IModel.normal2byte;

/**
 * A swgl mesh that describes the vertex data.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SimpleMesh implements AutoCloseable {
    private final List<GLVertex> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
    private SimpleMaterial material;
    private int vao = 0, vbo = 0, ebo = 0;
    private GLDrawMode drawMode = GLDrawMode.TRIANGLES;
    private boolean closed = false;

    public SimpleMesh(VertexLayout layout,
                      GLVertex vertex,
                      GLVertex... vertices) {
        this.vertices.add(vertex);
        indices.add(0);
        for (var vert : vertices) {
            int index = this.vertices.indexOf(vert);
            if (index >= 0) {
                indices.add(index);
            } else {
                this.vertices.add(vert);
                indices.add(this.vertices.indexOf(vert));
            }
        }
        System.out.println(this.vertices);
        System.out.println(indices);
        genGLObj(layout);
    }

    private void genGLObj(VertexLayout layout) {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        var buf = memAlloc(layout.getStride() * vertices.size());
        for (var vert : vertices) {
            layout.forEachFormat((format, offset, index) -> {
                switch (format) {
                    case V2F, V3F, V4F -> format.processBuffer(buf, vert.x, vert.y, vert.z, vert.w);
                    case C3UB, C4UB -> format.processBuffer(buf, vert.r, vert.g, vert.b, vert.a);
                    case C3F, C4F ->
                        format.processBuffer(buf, byte2color(vert.r), byte2color(vert.g), byte2color(vert.b), byte2color(vert.a));
                    case T2F, T3F, T4F -> format.processBuffer(buf, vert.s, vert.t, vert.p, vert.q);
                    case N3F -> format.processBuffer(buf, vert.nx, vert.ny, vert.nz, null);
                    case N3B ->
                        format.processBuffer(buf, normal2byte(vert.nx), normal2byte(vert.ny), normal2byte(vert.nz), null);
                    case GENERIC -> {
                    }
                    default -> throw new RuntimeException("The vertex format " + format.name() + " isn't supported!");
                }
            });
        }
        buf.flip();
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        memFree(buf);

        var indexBuf = memAllocInt(indices.size());
        for (int i : indices) {
            indexBuf.put(i);
        }
        indexBuf.flip();
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuf, GL_STATIC_DRAW);
        memFree(indexBuf);

        layout.beginDraw();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void setupMaterial() {
        final var mtl = getMaterial();
        if (mtl != null) {
            for (int i = mtl.getMinUnit(), u = mtl.getMaxUnit() + 1; i < u; i++) {
                var tex = mtl.getTexture(i);
                if (tex.isPresent()) {
                    activeTexture(i);
                    tex.get().bind();
                }
            }
        }
    }

    public void setMaterial(SimpleMaterial material) {
        this.material = material;
    }

    public SimpleMaterial getMaterial() {
        return material;
    }

    public GLDrawMode getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(GLDrawMode drawMode) {
        this.drawMode = drawMode;
    }

    /**
     * Render
     *
     * @param mode draw mode
     * @since 0.2.0
     */
    public void render(GLDrawMode mode) {
        setupMaterial();
        glBindVertexArray(vao);
        glDrawElements(mode.getGlType(), indices.size(), GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);
    }

    /**
     * Render with drawMode
     *
     * @since 0.2.0
     */
    public void render() {
        render(getDrawMode());
    }

    @Override
    public void close() {
        if (closed)
            return;
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
        closed = true;
    }
}
