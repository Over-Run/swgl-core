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

import org.jetbrains.annotations.ApiStatus;
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
@ApiStatus.Experimental
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
                glBufferData(GL_ARRAY_BUFFER, mesh.genRawData(program.getLayout()), GL_DYNAMIC_DRAW);
            }

            var indices = mesh.getIndices();
            boolean hasIndices = !indices.isEmpty();
            // Has indices
            if (hasIndices) {
                boolean isNotBuf = !glIsBuffer(mesh.ebo);
                if (isNotBuf)
                    mesh.ebo = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ebo);
                if (isNotBuf)
                    glBufferData(GL_ELEMENT_ARRAY_BUFFER, ListArrays.toIntArray(indices), GL_DYNAMIC_DRAW);
            } else
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

            program.layoutBeginDraw();
            if (hasIndices)
                glDrawElements(mesh.getDrawMode(), mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            else
                glDrawArrays(mesh.getDrawMode(), 0, mesh.getVertexCount());
            if (!ENABLE_CORE_PROFILE)
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
            if (glIsBuffer(mesh.vbo))
                glDeleteBuffers(mesh.vbo);
            if (glIsBuffer(mesh.ebo))
                glDeleteBuffers(mesh.ebo);
        }
        if (ENABLE_CORE_PROFILE && glIsVertexArray(vao))
            glDeleteVertexArrays(vao);
    }
}
