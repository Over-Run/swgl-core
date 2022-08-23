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

import org.lwjgl.opengl.GL15C;
import org.overrun.swgl.core.gl.GLVao;
import org.overrun.swgl.core.gl.IGLBuffer;
import org.overrun.swgl.core.gl.GLBatch;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.phys.p2d.AABRect2f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30C.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class Level {
    public static final int SCENE_WIDTH = 32;
    public static final int SCENE_HEIGHT = 25;
    private GLVao vao;
    private IGLBuffer.Single vbo, ebo;
    private int indexCount;
    private final Block[][] blocks = new Block[SCENE_HEIGHT][SCENE_WIDTH];
    private boolean dirty = true;
    private GLBatch batch;

    public void buildScene(GLProgram program) {
        if (!dirty)
            return;
        if (vao == null)
            vao = new GLVao();
        else if (!vao.isGenerated())
            vao.regenerate();
        if (vbo == null)
            vbo = new IGLBuffer.Single();
        else if (!vbo.isGenerated())
            vbo.regenerate();
        if (ebo == null)
            ebo = new IGLBuffer.Single();
        else if (!ebo.isGenerated())
            ebo.regenerate();
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
        vao.bind();
        vbo.layout(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
            .bind();
        if (batch.isVtExpanded()) {
            vbo.data(batch.getBuffer(), GL15C::glBufferData);
        } else {
            vbo.subData(0L, batch.getBuffer(), GL15C::glBufferSubData);
        }
        ebo.layout(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)
            .bind();
        if (batch.isIxExpanded()) {
            ebo.data(batch.getIndexBuffer().orElseThrow(), GL15C::glBufferData);
        } else {
            ebo.subData(0L, batch.getIndexBuffer().orElseThrow(), GL15C::glBufferSubData);
        }
        program.getLayout().beginDraw();
        vbo.unbind();
        vao.unbind();
        dirty = false;
    }

    public void renderScene(GLProgram program) {
        buildScene(program);
        vao.bind();
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L);
        vao.unbind();
    }

    public void deleteScene() {
        vao.delete();
        vbo.delete();
        ebo.delete();
        batch.close();
        batch = null;
    }

    public ArrayList<AABRect2f> getCubes(AABRect2f origin) {
        var lst = new ArrayList<AABRect2f>();
        int x0 = (int) origin.minX();
        int y0 = (int) origin.minY();
        int x1 = (int) (origin.maxX() + 1.0f);
        int y1 = (int) (origin.maxY() + 1.0f);

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
                    lst.add(AABRect2f.ofSize(x, y, 1.0f, 1.0f));
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
