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

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
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

    public static Mesh generateTriangle(
        VertexLayout layout,
        Vector3fc[] positions,
        Vector4fc[] colors,
        Vector2fc[] texCoords,
        Vector3fc normal
    ) {
        var bb = MemoryUtil.memAlloc(layout.getStride() * 3);
        for (int i = 0; i < 3; i++) {
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
            if (layout.hasTexture()) {
                var tex = texCoords[i];
                bb.putFloat(tex.x())
                    .putFloat(tex.y());
            }
            if (layout.hasNormal()) {
                bb.put(convertNormalToByte(normal.x()))
                    .put(convertNormalToByte(normal.y()))
                    .put(convertNormalToByte(normal.z()));
            }
        }
        return new Mesh(bb.flip(), 3, ICleaner.MEM_UTIL, null);
    }
}
