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

package org.overrun.swgl.game.world.entity.model;

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Quad {
    private final Vertex[] vertices;
    private final float texW, texH;

    public Quad(Vertex[] vertices, float texW, float texH) {
        this.vertices = vertices;
        this.texW = texW;
        this.texH = texH;
    }

    public void render() {
        lglColor(1, 1, 1);
        lglIndices(0, 1, 2, 2, 3, 0);
        for (var vertex : vertices) {
            lglTexCoord(vertex.u / texW, vertex.v / texH);
            lglVertex(vertex.x, vertex.y, vertex.z);
            lglEmit();
        }
    }
}
