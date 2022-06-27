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

import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLDataType;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.model.VertexLayout;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public final class InstancedDrawTest extends GlfwApplication {
    public static void main(String[] args) {
        new InstancedDrawTest().launch();
    }

    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static final int QUAD_COUNT = 400;
    private GLProgram program;
    private int vao, vbo, instanceVbo;

    @Override
    public void prepare() {
        WindowConfig.initialTitle = "Instance Draw Test";
        WindowConfig.setRequiredGlVer(3, 3);
    }

    @Override
    public void start() {
        clearColor(0.0f, 0.0f, 0.0f, 1.0f);

        resManager = new ResManager();
        program = resManager.addResource(new GLProgram(new VertexLayout(VertexFormat.V2F, VertexFormat.C3F, VertexFormat.V2F)));
        program.create();
        GLShaders.linkSimple(program,
            "shaders/instanced_draw/shader.vert",
            "shaders/instanced_draw/shader.frag",
            FILE_PROVIDER);

        var transitions = memAllocFloat(QUAD_COUNT * 2);
        final int mul = QUAD_COUNT / (int) Math.sqrt(QUAD_COUNT) / 10;
        final float offset = 0.1f / mul;
        int c = 10 * mul;
        for (int y = -c; y < c; y += 2) {
            for (int x = -c; x < c; x += 2) {
                transitions.put((float) x / c + offset)
                    .put((float) y / c + offset);
            }
        }
        transitions.flip();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        float v = 0.05f / mul;
        glBufferData(GL_ARRAY_BUFFER, new float[]{
            -v, v, 1.0f, 0.0f, 0.0f,
            v, -v, 0.0f, 1.0f, 0.0f,
            -v, -v, 0.0f, 0.0f, 1.0f,

            -v, v, 1.0f, 0.0f, 0.0f,
            v, -v, 0.0f, 1.0f, 0.0f,
            v, v, 0.0f, 1.0f, 1.0f
        }, GL_STATIC_DRAW);
        final int stride = GLDataType.FLOAT.getLength(5);
        VertexFormat.V2F.beginDraw(0, stride, 0);
        VertexFormat.C3F.beginDraw(1, stride, GLDataType.FLOAT.getLength(2));

        instanceVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
        glBufferData(GL_ARRAY_BUFFER, transitions, GL_STATIC_DRAW);
        memFree(transitions);
        VertexFormat.V2F.beginDraw(2, GLDataType.FLOAT.getLength(2), 0);
        glVertexAttribDivisor(2, 1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);

        program.bind();
        glBindVertexArray(vao);
        glDrawArraysInstanced(GL_TRIANGLES, 0, 6, QUAD_COUNT);
        glBindVertexArray(0);
        program.unbind();
    }

    @Override
    public void close() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(instanceVbo);
    }
}
