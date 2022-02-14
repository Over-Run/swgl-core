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

import org.joml.*;
import org.lwjgl.system.MemoryUtil;
import org.overrun.swgl.core.io.ICleaner;

/**
 * swgl geometry figures.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Geometry {
    private static byte convertNormalToByte(float normal) {
        return (byte) ((255.0f * normal - 1.0f) / 2.0f);
    }

    /**
     * Generate triangles.
     *
     * @param vertexCount The original vertex count, not indices length.
     *                    Must be a multiple of 3.
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
                var pos = positions[i];
                bb.putFloat(pos.x())
                    .putFloat(pos.y())
                    .putFloat(pos.z());
            }
            if (layout.hasColor()) {
                var color = colors[i];
                bb.put((byte) (color.x() * 255))
                    .put((byte) (color.y() * 255))
                    .put((byte) (color.z() * 255))
                    .put((byte) (color.w() * 255));
            }
            if (layout.hasTexture(0)) {
                var tex = texCoords[i];
                bb.putFloat(tex.x())
                    .putFloat(tex.y());
            }
            if (layout.hasNormal()) {
                bb.put(convertNormalToByte(normals[i].x()))
                    .put(convertNormalToByte(normals[i].y()))
                    .put(convertNormalToByte(normals[i].z()));
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
        for (int i = 0; i < indices.length; i += 6) {
            indices[i] = i;
            indices[i + 1] = i + 1;
            indices[i + 2] = i + 2;
            indices[i + 3] = i + 2;
            indices[i + 4] = i + 3;
            indices[i + 5] = i;
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
