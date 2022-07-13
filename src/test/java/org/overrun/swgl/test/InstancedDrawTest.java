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

import org.lwjgl.opengl.GL15C;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLDataType;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLVao;
import org.overrun.swgl.core.gl.IGLBuffer;
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
    private GLVao vao;
    private IGLBuffer.Single vbo, instanceVbo;

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

        final float v = 0.05f / mul;
        vao = resManager.addResource(new GLVao())
            .bind()
            .withAction(() -> vbo = resManager.addResource(new IGLBuffer.Single())
                .layout(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
                .bind()
                .data(new float[]{
                    -v, v, 1.0f, 0.0f, 0.0f,
                    v, -v, 0.0f, 1.0f, 0.0f,
                    -v, -v, 0.0f, 0.0f, 1.0f,

                    -v, v, 1.0f, 0.0f, 0.0f,
                    v, -v, 0.0f, 1.0f, 0.0f,
                    v, v, 0.0f, 1.0f, 1.0f
                }, GL15C::glBufferData)
                .withAction(() -> {
                    final int stride = GLDataType.FLOAT.getLength(5);
                    VertexFormat.V2F.beginDraw(0, stride, 0);
                    VertexFormat.C3F.beginDraw(1, stride, GLDataType.FLOAT.getLength(2));
                }))
            .withAction(() -> instanceVbo = resManager.addResource(new IGLBuffer.Single())
                .layout(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
                .bind()
                .data(transitions, GL15C::glBufferData)
                .withAction(() -> {
                    memFree(transitions);
                    VertexFormat.V2F.beginDraw(2, GLDataType.FLOAT.getLength(2), 0);
                    glVertexAttribDivisor(2, 1);
                })
                .unbind()
            )
            .unbind();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);

        program.bind();
        vao.bind();
        glDrawArraysInstanced(GL_TRIANGLES, 0, 6, QUAD_COUNT);
        vao.unbind();
        program.unbind();
    }
}
