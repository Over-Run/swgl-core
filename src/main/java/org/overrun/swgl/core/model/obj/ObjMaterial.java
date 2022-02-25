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

import org.joml.Vector4f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class ObjMaterial {
    public String name;
    public AIMaterial material;
    public final Vector4f ambientColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    public final Vector4f diffuseColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    public final Vector4f specularColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    public final String[] ambientMaps, diffuseMaps, specularMaps;
    public float shininess;

    public ObjMaterial(AIMaterial material, String basePath, String name) {
        this.material = material;
        this.name = name;

        float[] f = new float[1];
        aiGetMaterialFloatArray(material, AI_MATKEY_SHININESS,
            aiTextureType_NONE, 0, f, new int[]{1});
        shininess = f[0];

        var mAmbientColor = AIColor4D.create();
        int hr = aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT,
            aiTextureType_NONE, 0, mAmbientColor);
        if (hr == 0)
            ambientColor.set(mAmbientColor.r(), mAmbientColor.g(), mAmbientColor.b(), mAmbientColor.a());

        var mDiffuseColor = AIColor4D.create();
        hr = aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE,
            aiTextureType_NONE, 0, mDiffuseColor);
        if (hr == 0)
            diffuseColor.set(mDiffuseColor.r(), mDiffuseColor.g(), mDiffuseColor.b(), mDiffuseColor.a());

        var mSpecularColor = AIColor4D.create();
        hr = aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR,
            aiTextureType_NONE, 0, mSpecularColor);
        if (hr == 0)
            specularColor.set(mSpecularColor.r(), mSpecularColor.g(), mSpecularColor.b(), mSpecularColor.a());

        int count = aiGetMaterialTextureCount(material, aiTextureType_AMBIENT);
        ambientMaps = new String[count];
        for (int i = 0; i < count; i++) {
            var path = AIString.create();
            aiGetMaterialTexture(material, aiTextureType_AMBIENT, i, path, (IntBuffer) null, null, null, null, null, null);
            var str = path.dataString();
            if (!str.startsWith("/") && !str.startsWith("\\"))
                str = basePath + str;
            ambientMaps[i] = str;
        }

        count = aiGetMaterialTextureCount(material, aiTextureType_DIFFUSE);
        diffuseMaps = new String[count];
        for (int i = 0; i < count; i++) {
            var path = AIString.create();
            aiGetMaterialTexture(material, aiTextureType_DIFFUSE, i, path, (IntBuffer) null, null, null, null, null, null);
            var str = path.dataString();
            if (!str.startsWith("/") && !str.startsWith("\\"))
                str = basePath + str;
            diffuseMaps[i] = str;
        }

        count = aiGetMaterialTextureCount(material, aiTextureType_SPECULAR);
        specularMaps = new String[count];
        for (int i = 0; i < count; i++) {
            var path = AIString.create();
            aiGetMaterialTexture(material, aiTextureType_SPECULAR, i, path, (IntBuffer) null, null, null, null, null, null);
            var str = path.dataString();
            if (!str.startsWith("/") && !str.startsWith("\\"))
                str = basePath + str;
            specularMaps[i] = str;
        }
    }
}
