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

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.overrun.swgl.core.gl.GLStateMgr.activeTexture;

/**
 * A swgl mesh that describes the vertex data.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SimpleMesh {
    @NotNull
    private final List<Vector3fc> positions = new ArrayList<>();
    @NotNull
    private final List<Vector4fc> colors = new ArrayList<>();
    @NotNull
    private final List<Vector2fc> texCoords = new ArrayList<>();
    @NotNull
    private final List<Vector3fc> normals = new ArrayList<>();
    @NotNull
    private final List<Integer> indices = new ArrayList<>();
    private final int vertexCount;
    private SimpleMaterial material;
    private int drawMode = GL_TRIANGLES;

    public SimpleMesh(Collection<Vector3fc> positions,
                      Collection<Vector4fc> colors,
                      Collection<Vector2fc> texCoords,
                      Collection<Vector3fc> normals,
                      int vertexCount,
                      Collection<Integer> indices) {
        if (positions != null)
            this.positions.addAll(positions);
        if (colors != null)
            this.colors.addAll(colors);
        if (texCoords != null)
            this.texCoords.addAll(texCoords);
        if (normals != null)
            this.normals.addAll(normals);
        this.vertexCount = indices == null ? vertexCount : indices.size();
        if (indices != null)
            this.indices.addAll(indices);
    }

    public void setupMaterial() {
        if (getMaterial() != null) {
            for (int i = getMaterial().getMinUnit(),
                 u = getMaterial().getMaxUnit() + 1; i < u; i++) {
                var tex = getMaterial().getTexture(i);
                if (tex != null) {
                    activeTexture(i);
                    tex.bind();
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

    @NotNull
    public List<Vector3fc> getPositions() {
        return positions;
    }

    @NotNull
    public List<Vector4fc> getColors() {
        return colors;
    }

    @NotNull
    public List<Vector2fc> getTexCoords() {
        return texCoords;
    }

    @NotNull
    public List<Vector3fc> getNormals() {
        return normals;
    }

    @NotNull
    public List<Integer> getIndices() {
        return indices;
    }

    public boolean hasIndices() {
        return !getIndices().isEmpty();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }
}
