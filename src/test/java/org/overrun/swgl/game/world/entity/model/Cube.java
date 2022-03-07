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

import org.overrun.swgl.core.gl.GLDrawMode;

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;
import static org.overrun.swgl.core.gl.ims.GLLists.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Cube {
    private final Quad[] quads = new Quad[6];
    private float x, y, z;
    private float yaw, pitch, roll;
    private final int xTexOffs, yTexOffs;
    private boolean compiled = false;
    private int list;

    public Cube(int xTexOffs, int yTexOffs) {
        this.xTexOffs = xTexOffs;
        this.yTexOffs = yTexOffs;
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRot(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public void addBox(int x0, int y0, int z0,
                       int x1, int y1, int z1,
                       int w, int h, int d,
                       int texW, int texH) {
        // Fix facing to CCW
        int orgX0 = x0;
        int orgY0 = y0;
        int orgZ0 = z0;
        int orgX1 = x1;
        int orgY1 = y1;
        int orgZ1 = z1;
        x0 = Math.min(orgX0, orgX1);
        y0 = Math.min(orgY0, orgY1);
        z0 = Math.min(orgZ0, orgZ1);
        x1 = Math.max(orgX0, orgX1);
        y1 = Math.max(orgY0, orgY1);
        z1 = Math.max(orgZ0, orgZ1);
        var v0 = new Vertex(x0, y1, z0, 0, 0);
        var v1 = new Vertex(x0, y0, z0, 0, 0);
        var v2 = new Vertex(x0, y0, z1, 0, 0);
        var v3 = new Vertex(x0, y1, z1, 0, 0);
        var v4 = new Vertex(x1, y1, z1, 0, 0);
        var v5 = new Vertex(x1, y0, z1, 0, 0);
        var v6 = new Vertex(x1, y0, z0, 0, 0);
        var v7 = new Vertex(x1, y1, z0, 0, 0);
        // -x
        quads[0] = new Quad(new Vertex[]{v0.remap(xTexOffs, yTexOffs + d), v1.remap(xTexOffs, yTexOffs + d + h), v2.remap(xTexOffs + d, yTexOffs + d + h), v3.remap(xTexOffs + d, yTexOffs + d)}, texW, texH);
        // +x
        quads[1] = new Quad(new Vertex[]{v4.remap(xTexOffs + d + w, yTexOffs + d), v5.remap(xTexOffs + d + w, yTexOffs + d + h), v6.remap(xTexOffs + d + w + d, yTexOffs + d + h), v7.remap(xTexOffs + d + w + d, yTexOffs + d)}, texW, texH);
        // -y
        quads[2] = new Quad(new Vertex[]{v2.remap(xTexOffs + d + w, yTexOffs), v1.remap(xTexOffs + d + w, yTexOffs + d), v6.remap(xTexOffs + d + w + d, yTexOffs + d), v5.remap(xTexOffs + d + w + d, yTexOffs)}, texW, texH);
        // +y
        quads[3] = new Quad(new Vertex[]{v0.remap(xTexOffs + d, yTexOffs), v3.remap(xTexOffs + d, yTexOffs + d), v4.remap(xTexOffs + d + w, yTexOffs + d), v7.remap(xTexOffs + d + w, yTexOffs)}, texW, texH);
        // -z
        quads[4] = new Quad(new Vertex[]{v7.remap(xTexOffs + d + w + d, yTexOffs + d), v6.remap(xTexOffs + d + w + d, yTexOffs + d + h), v1.remap(xTexOffs + d + w + d + w, yTexOffs + d + h), v0.remap(xTexOffs + d + w + d + w, yTexOffs + d)}, texW, texH);
        // +z
        quads[5] = new Quad(new Vertex[]{v3.remap(xTexOffs + d, yTexOffs + d), v2.remap(xTexOffs + d, yTexOffs + d + h), v5.remap(xTexOffs + d + w, yTexOffs + d + h), v4.remap(xTexOffs + d + w, yTexOffs + d)}, texW, texH);
    }

    private void compile() {
        list = lglGenList();
        lglNewList(list);
        lglBegin(GLDrawMode.TRIANGLES);
        for (var quad : quads) {
            quad.render();
        }
        lglEnd();
        lglEndList();

        compiled = true;
    }

    public void render() {
        if (!compiled) {
            compile();
        }
        lglPushMatrix();
        lglRotateXYZ(pitch, yaw, roll);
        lglTranslate(x, y, z);
        lglCallList(list);
        lglPopMatrix();
    }
}
