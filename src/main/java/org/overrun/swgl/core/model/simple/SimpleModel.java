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

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.model.IModel;
import org.overrun.swgl.core.util.ListArrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30C.*;
import static org.overrun.swgl.core.gl.GLStateMgr.ENABLE_CORE_PROFILE;

/**
 * The simple model contains a list of the meshes.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SimpleModel implements IModel, AutoCloseable {
    private final List<SimpleMesh> meshes = new ArrayList<>();
    private int vao;

    public SimpleModel(SimpleMesh mesh0, SimpleMesh... meshes) {
        this.meshes.add(mesh0);
        this.meshes.addAll(Arrays.asList(meshes));
    }

    public SimpleModel(Collection<SimpleMesh> meshes) {
        this.meshes.addAll(meshes);
    }

    public void addMesh(SimpleMesh mesh) {
        meshes.add(mesh);
    }

    public SimpleMesh getMesh(int index) {
        return meshes.get(index);
    }

    public void render(GLProgram program) {
        if (ENABLE_CORE_PROFILE) {
            if (!glIsVertexArray(vao))
                vao = glGenVertexArrays();
            glBindVertexArray(vao);
        }
        for (var mesh : meshes) {
            mesh.setupMaterial();
            if (!glIsBuffer(mesh.vbo))
                mesh.vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo);
            if (mesh.rawData == null) {
                var positions = mesh.getPositions();
                var colors = mesh.getColors();
                var texCoords = mesh.getTexCoords();
                var normals = mesh.getNormals();
                mesh.rawData = MemoryUtil.memCalloc(mesh.getVertexCount() * program.getLayout().getStride());
                for (int i = 0, c = mesh.getVertexCount(); i < c; i++) {
                    if (positions.size() > 0) {
                        Vector3fc pos;
                        if (i < positions.size()) pos = positions.get(i);
                        else pos = positions.get(i % positions.size());
                        mesh.rawData.putFloat(pos.x())
                            .putFloat(pos.y())
                            .putFloat(pos.z());
                    }
                    if (colors.size() > 0) {
                        Vector4fc color;
                        if (i < colors.size()) color = colors.get(i);
                        else color = colors.get(i % colors.size());
                        mesh.rawData.put(IModel.color2byte(color.x()))
                            .put(IModel.color2byte(color.y()))
                            .put(IModel.color2byte(color.z()))
                            .put(IModel.color2byte(color.w()));
                    }
                    if (texCoords.size() > 0) {
                        Vector2fc tex;
                        if (i < texCoords.size()) tex = texCoords.get(i);
                        else tex = texCoords.get(i % texCoords.size());
                        mesh.rawData.putFloat(tex.x())
                            .putFloat(tex.y());
                    }
                    if (normals.size() > 0) {
                        Vector3fc normal;
                        if (i < normals.size()) normal = normals.get(i);
                        else normal = normals.get(i % normals.size());
                        mesh.rawData.put(IModel.normal2byte(normal.x()))
                            .put(IModel.normal2byte(normal.y()))
                            .put(IModel.normal2byte(normal.z()));
                    }
                }
                glBufferData(GL_ARRAY_BUFFER, mesh.rawData.flip(), GL_DYNAMIC_DRAW);
            }
            var indices = mesh.getIndices();
            // Has indices
            if (!indices.isEmpty()) {
                if (!glIsBuffer(mesh.ebo))
                    mesh.ebo = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ebo);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, ListArrays.toIntArray(indices), GL_DYNAMIC_DRAW);
            } else
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            program.layoutBeginDraw();
            if (!indices.isEmpty())
                glDrawElements(mesh.getDrawMode(), mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            else
                glDrawArrays(mesh.getDrawMode(), 0, mesh.getVertexCount());
            program.layoutEndDraw();
        }
        if (ENABLE_CORE_PROFILE)
            glBindVertexArray(0);
    }

    /**
     * Get the list of the meshes.
     *
     * @return the list of the meshes
     */
    public List<SimpleMesh> getMeshes() {
        return meshes;
    }

    @Override
    public void close() {
        for (var mesh : meshes) {
            if (mesh.rawData != null) {
                MemoryUtil.memFree(mesh.rawData);
                mesh.rawData = null;
            }
            if (glIsBuffer(mesh.vbo))
                glDeleteBuffers(mesh.vbo);
            if (glIsBuffer(mesh.ebo))
                glDeleteBuffers(mesh.ebo);
        }
        if (ENABLE_CORE_PROFILE && glIsVertexArray(vao))
            glDeleteVertexArrays(vao);
    }
}
