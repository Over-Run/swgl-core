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

import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.overrun.swgl.core.model.IModel;
import org.overrun.swgl.core.util.IntTri;

import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.overrun.swgl.core.gl.GLStateMgr.ENABLE_CORE_PROFILE;

/**
 * The obj model that contains the materials, meshes and groups.
 *
 * @author squid233
 * @since 0.1.0
 */
public class ObjModel implements IModel, AutoCloseable {
    public AIScene scene;
    public List<ObjMesh> meshes = new ArrayList<>();
    public Map<String, ObjMaterial> materials = new HashMap<>();
    public int vao = 0;

    public ObjModel(AIScene scene, String basePath) {
        this.scene = scene;

        int meshCount = scene.mNumMeshes();
        var meshesBuffer = scene.mMeshes();
        for (int i = 0; i < meshCount; i++) {
            meshes.add(new ObjMesh(AIMesh.create(Objects.requireNonNull(meshesBuffer).get(i))));
        }

        int materialCount = scene.mNumMaterials();
        var materialsBuffer = scene.mMaterials();
        for (int i = 0; i < materialCount; i++) {
            var mtl = AIMaterial.create(Objects.requireNonNull(materialsBuffer).get(i));
            var name = AIString.create();
            aiGetMaterialString(mtl, AI_MATKEY_NAME, aiTextureType_NONE, 0, name);
            var nm = name.dataString();
            materials.put(nm, new ObjMaterial(mtl, basePath, nm));
        }
    }

    public void render(IntTri locations, Consumer<ObjMaterial> consumer) {
        if (ENABLE_CORE_PROFILE) {
            if (!glIsVertexArray(vao))
                vao = glGenVertexArrays();
            glBindVertexArray(vao);
        }
        for (var mtl : materials.values())
            consumer.accept(mtl);
        for (var mesh : meshes) {
            mesh.setupBuffers(locations);
            glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0);
        }
        if (ENABLE_CORE_PROFILE)
            glBindVertexArray(0);
    }

    public Optional<ObjMaterial> getMaterial(String name) {
        return Optional.ofNullable(materials.get(name));
    }

    @Override
    public void close() {
        aiReleaseImport(scene);
        scene = null;
        meshes = null;
        materials = null;
        if (ENABLE_CORE_PROFILE && glIsVertexArray(vao))
            glDeleteVertexArrays(vao);
    }
}
