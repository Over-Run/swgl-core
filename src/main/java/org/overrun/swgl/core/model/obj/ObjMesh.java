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

package org.overrun.swgl.core.model.obj;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryUtil;
import org.overrun.swgl.core.util.IntTri;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.AI_MAX_NUMBER_OF_TEXTURECOORDS;
import static org.lwjgl.opengl.GL30C.*;

/**
 * The obj model mesh.
 *
 * @author squid233
 * @since 0.1.0
 */
public class ObjMesh {
    public AIMesh mesh;
    public int materialIndex;
    public int vao, vbo, vnbo, ebo;
    public final List<Integer> vtbos = new ArrayList<>();
    public int vertexCount;

    public ObjMesh(AIMesh mesh,
                   IntTri vaIndices) {
        this.mesh = mesh;
        materialIndex = mesh.mMaterialIndex();

        bindVao();
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        var vertices = mesh.mVertices();
        nglBufferData(GL_ARRAY_BUFFER, (long) AIVector3D.SIZEOF * vertices.remaining(),
            vertices.address(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(vaIndices.x());
        glVertexAttribPointer(vaIndices.x(),
            3,
            GL_FLOAT,
            false,
            12,
            0);

        for (int i = 0; i < AI_MAX_NUMBER_OF_TEXTURECOORDS; i++) {
            var texCoords = mesh.mTextureCoords(i);
            if (texCoords != null) {
                int vtbo = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vtbo);
                nglBufferData(GL_ARRAY_BUFFER, (long) AIVector3D.SIZEOF * texCoords.remaining(),
                    texCoords.address(), GL_STATIC_DRAW);
                glEnableVertexAttribArray(vaIndices.y());
                glVertexAttribPointer(vaIndices.y(),
                    3,
                    GL_FLOAT,
                    false,
                    12,
                    0);
                vtbos.add(vtbo);
            }
        }

        var normals = mesh.mNormals();
        if (normals != null) {
            vnbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vnbo);
            nglBufferData(GL_ARRAY_BUFFER, (long) AIVector3D.SIZEOF * normals.remaining(),
                normals.address(), GL_STATIC_DRAW);
            glEnableVertexAttribArray(vaIndices.z());
            glVertexAttribPointer(vaIndices.z(),
                3,
                GL_FLOAT,
                false,
                12,
                0);
        }

        int faceCount = mesh.mNumFaces();
        vertexCount = faceCount * 3;
        IntBuffer ib = null;
        try {
            ib = MemoryUtil.memCallocInt(vertexCount);
            var facesBuf = mesh.mFaces();
            for (int i = 0; i < faceCount; i++) {
                var face = facesBuf.get(i);
                ib.put(face.mIndices());
            }
            ib.flip();
            ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);
        } finally {
            MemoryUtil.memFree(ib);
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void bindVao() {
        if (!glIsVertexArray(vao))
            vao = glGenVertexArrays();
        glBindVertexArray(vao);
    }
}
