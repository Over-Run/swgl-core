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

package org.overrun.swgl.core.model;

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import org.overrun.swgl.core.io.ICleaner;
import org.overrun.swgl.core.model.mesh.Mesh;

/**
 * swgl geometry figures and mesh manager.
 *
 * @author squid233
 * @since 0.1.0
 */
@Deprecated(since = "0.1.0")
public class Geometry {
    /**
     * Generate triangles.
     *
     * @param vertexCount The original vertex count, not indices length.
     * @param layout      The vertex layout descriptor.
     * @param positions   The positions.
     * @param colors      The colors.
     * @param texCoords   The texture coordinates.
     * @param normals     The normals.
     * @param indices     The indices.
     * @return The mesh.
     */
    public static Mesh generateTriangles(
        int vertexCount,
        VertexLayout layout,
        Vector3fc[] positions,
        Vector4fc[] colors,
        Vector2fc[] texCoords,
        Vector3fc[] normals,
        int[] indices
    ) {
        var bb = MemoryUtil.memAlloc(layout.getStride() * vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            if (layout.hasPosition()) {
                Vector3fc pos;
                if (i < positions.length) pos = positions[i];
                else pos = positions[i % positions.length];
                bb.putFloat(pos.x())
                    .putFloat(pos.y())
                    .putFloat(pos.z());
            }
            if (layout.hasColor()) {
                Vector4fc color;
                if (i < colors.length) color = colors[i];
                else color = colors[i % colors.length];
                bb.put(IModel.color2byte(color.x()))
                    .put(IModel.color2byte(color.y()))
                    .put(IModel.color2byte(color.z()))
                    .put(IModel.color2byte(color.w()));
            }
            if (layout.hasTexture()) {
                Vector2fc tex;
                if (i < texCoords.length) tex = texCoords[i];
                else tex = texCoords[i % texCoords.length];
                bb.putFloat(tex.x())
                    .putFloat(tex.y());
            }
            if (layout.hasNormal()) {
                Vector3fc normal;
                if (i < normals.length) normal = normals[i];
                else normal = normals[i % normals.length];
                bb.put(IModel.normal2byte(normal.x()))
                    .put(IModel.normal2byte(normal.y()))
                    .put(IModel.normal2byte(normal.z()));
            }
        }
        return new Mesh(bb.flip(), vertexCount, ICleaner.MEM_UTIL, indices);
    }

    /**
     * Generate quads.
     *
     * @param vertexCount The vertex count. Must be a multiple of 4.
     * @param layout      The vertex layout descriptor.
     * @param positions   The positions.
     * @param colors      The colors.
     * @param texCoords   The texture coordinates.
     * @param normals     The normals.
     * @return The mesh.
     */
    public static Mesh generateQuads(
        int vertexCount,
        VertexLayout layout,
        Vector3fc[] positions,
        Vector4fc[] colors,
        Vector2fc[] texCoords,
        Vector3fc[] normals
    ) {
        var indices = new int[vertexCount / 4 * 6];
        for (int i = 0, j = 0; i < indices.length; j += 4) {
            indices[i++] = j;
            indices[i++] = j + 1;
            indices[i++] = j + 2;
            indices[i++] = j + 2;
            indices[i++] = j + 3;
            indices[i++] = j;
        }
        return generateTriangles(
            vertexCount,
            layout,
            positions,
            colors,
            texCoords,
            normals,
            indices
        );
    }
}
