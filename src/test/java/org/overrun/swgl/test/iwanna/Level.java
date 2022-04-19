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

package org.overrun.swgl.test.iwanna;

import org.overrun.swgl.core.gl.batch.GLBatch;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.phys.p2d.AABBox2f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30C.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class Level {
    public static final int SCENE_WIDTH = 32;
    public static final int SCENE_HEIGHT = 25;
    private int vao;
    private int vbo, ebo, indexCount;
    private final Block[][] blocks = new Block[SCENE_HEIGHT][SCENE_WIDTH];
    private boolean dirty = true;
    private GLBatch batch;

    public void buildScene(GLProgram program) {
        if (!dirty)
            return;
        if (!glIsVertexArray(vao))
            vao = glGenVertexArrays();
        if (!glIsBuffer(vbo))
            vbo = glGenBuffers();
        if (!glIsBuffer(ebo))
            ebo = glGenBuffers();
        if (batch == null)
            batch = new GLBatch();
        batch.begin(program.getLayout(), 1024);
        for (int x = 0; x < SCENE_WIDTH; x++) {
            for (int y = 0; y < SCENE_HEIGHT; y++) {
                var b = getBlock(x, y);
                if (b != null)
                    b.generateModel(batch, x, y);
            }
        }
        batch.end();
        indexCount = batch.getIndexCount();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        if (batch.isVtExpanded()) {
            glBufferData(GL_ARRAY_BUFFER, batch.getBuffer(), GL_STATIC_DRAW);
        } else {
            glBufferSubData(GL_ARRAY_BUFFER, 0L, batch.getBuffer());
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        if (batch.isIxExpanded()) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, batch.getIndexBuffer().orElseThrow(), GL_STATIC_DRAW);
        } else {
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0L, batch.getIndexBuffer().orElseThrow());
        }
        program.getLayout().beginDraw();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        dirty = false;
    }

    public void renderScene(GLProgram program) {
        buildScene(program);
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);
    }

    public void deleteScene() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        vao = vbo = ebo = 0;
        batch.close();
        batch = null;
    }

    public ArrayList<AABBox2f> getCubes(AABBox2f origin) {
        var lst = new ArrayList<AABBox2f>();
        int x0 = (int) origin.getMinX();
        int y0 = (int) origin.getMinY();
        int x1 = (int) (origin.getMaxX() + 1.0f);
        int y1 = (int) (origin.getMaxY() + 1.0f);

        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (x1 > SCENE_WIDTH) {
            x1 = SCENE_WIDTH;
        }
        if (y1 > SCENE_HEIGHT) {
            y1 = SCENE_HEIGHT;
        }

        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                var block = getBlock(x, y);
                if (block != null)
                    lst.add(AABBox2f.ofSize(x, y, 1.0f, 1.0f));
            }
        }
        return lst;
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setBlock(int x, int y, Block block) {
        blocks[y][x] = block;
        markDirty();
    }

    public Block getBlock(int x, int y) {
        return blocks[y][x];
    }
}
