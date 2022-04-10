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

package org.overrun.swgl.test;

import org.joml.Matrix4f;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.gl.GLBatch;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.shader.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.BuiltinVertexLayouts;
import org.overrun.swgl.core.util.timing.Timer;

import static org.lwjgl.opengl.GL30C.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class GLBatchTest extends GlfwApplication {
    public static void main(String[] args) {
        var test = new GLBatchTest();
        test.launch();
    }

    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private GLProgram program = null;
    private int vao = 0;
    private final int[] buffers = {0, 0};
    private int indexCount = 0;
    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f modelView = new Matrix4f();

    private static void color4batch(GLBatch batch) {
        batch.color(1.0f, 0.0f, 0.0f).vertex(-0.5f, 0.5f, 0.5f).emit();
        batch.color(0.0f, 1.0f, 0.0f).vertex(-0.5f, -0.5f, 0.5f).emit();
        batch.color(0.0f, 0.0f, 1.0f).vertex(0.5f, -0.5f, 0.5f).emit();
        batch.color(1.0f, 1.0f, 1.0f).vertex(0.5f, 0.5f, 0.5f).emit();
        batch.indexAfter(0, 1, 2, 2, 3, 0);
    }

    private static void color8batch(GLBatch batch) {
        batch.color(0.0f, 0.0f, 0.0f).vertex(-1.0f, 1.0f, 0.0f).emit(); // 0
        batch.color(1.0f, 0.0f, 0.0f).vertex(-1.0f, 0.0f, 0.0f).emit(); // 1
        batch.color(0.0f, 1.0f, 0.0f).vertex(-1.0f, -1.0f, 0.0f).emit(); // 2
        batch.color(1.0f, 1.0f, 0.0f).vertex(0.0f, -1.0f, 0.0f).emit(); // 3
        batch.color(0.0f, 0.0f, 1.0f).vertex(1.0f, -1.0f, 0.0f).emit(); // 4
        batch.color(1.0f, 0.0f, 1.0f).vertex(1.0f, 0.0f, 0.0f).emit(); // 5
        batch.color(0.0f, 1.0f, 1.0f).vertex(1.0f, 1.0f, 0.0f).emit(); // 6
        batch.color(1.0f, 1.0f, 1.0f).vertex(0.0f, 1.0f, 0.0f).emit(); // 7
        batch.color(0.5f, 0.5f, 0.5f).vertex(0.0f, 0.0f, 0.0f).emit(); // 8
        batch.indexAfter(
            0, 1, 8, 8, 7, 0,
            2, 1, 8, 8, 3, 2,
            6, 5, 8, 8, 7, 6,
            4, 5, 8, 8, 3, 4
        );
    }

    @Override
    public void start() {
        var rm = new ResManager(this);
        program = rm.addResource(new GLProgram(BuiltinVertexLayouts.C4UB_V3F));
        program.create();
        program.bindAttribLoc(0, "Color");
        program.bindAttribLoc(1, "Position");
        Shaders.linkSimple(program,
            "shaders/glbatch/shader.vert",
            "shaders/glbatch/shader.frag",
            FILE_PROVIDER);
        var batch = new GLBatch();
        batch.begin(program.getLayout());
//        color4batch(batch);
        color8batch(batch);
        batch.end();
        indexCount = batch.getIndexCount();
        vao = glGenVertexArrays();
        glGenBuffers(buffers);
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        glBufferData(GL_ARRAY_BUFFER, batch.getBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, batch.getIndexBuffer().orElseThrow(), GL_STATIC_DRAW);
        program.getLayout().beginDraw();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        batch.close();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        program.bind();
        program.getUniformSafe("ProjMat", GLUniformType.M4F).set(proj.setOrthoSymmetric(window.getWidth(), window.getHeight(), -1, 1));
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(modelView.rotationZ((float) Timer.getTime()).scale(256.0f));
        program.updateUniforms();
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);
        program.unbind();
    }

    @Override
    public void close() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(buffers);
    }
}
