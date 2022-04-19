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
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * The simple models generator.
 *
 * @author squid233
 * @since 0.1.0
 */
@ApiStatus.Experimental
public class SimpleModels {
    public static SimpleModel genTriangles(
        int vertexCount,
        Vector3fc[] positions,
        Vector4fc[] colors,
        Vector2fc[] texCoords,
        Vector3fc[] normals,
        int[] indices) {
        return new SimpleModel(
            SimpleMeshes.genTriangles(vertexCount, positions, colors, texCoords, normals, indices)
        );
    }

    public static SimpleModel genQuads(
        int vertexCount,
        Vector3fc[] positions,
        Vector4fc[] colors,
        Vector2fc[] texCoords,
        Vector3fc[] normals) {
        return new SimpleModel(
            SimpleMeshes.genQuads(vertexCount, positions, colors, texCoords, normals)
        );
    }
}
