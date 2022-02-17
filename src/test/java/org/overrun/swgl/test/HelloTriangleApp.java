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

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.math.Transformation;
import org.overrun.swgl.core.mesh.Geometry;
import org.overrun.swgl.core.mesh.Mesh;
import org.overrun.swgl.core.mesh.VertexFormat;
import org.overrun.swgl.core.mesh.VertexLayout;

import java.util.Objects;

import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class HelloTriangleApp extends GlfwApplication {
    public static void main(String[] args) {
        var app = new HelloTriangleApp();
        app.boot();
    }

    private GLProgram.Default program;
    private Mesh mesh;
    private final Transformation transformation = new Transformation();

    @Override
    public void prepare() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialTitle = "Hello Triangle Application";
        GlobalConfig.initialSwapInterval = 0;
    }

    @Override
    public void start() {
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        program = new GLProgram.Default(
            new VertexLayout(VertexFormat.POSITION_FMT, VertexFormat.COLOR_FMT) {
                @Override
                public void beginDraw(GLProgram program) {
                    VertexFormat.POSITION_FMT.beginDraw(program, "Position");
                    VertexFormat.COLOR_FMT.beginDraw(program, "Color");
                }

                @Override
                public void endDraw(GLProgram program) {
                    VertexFormat.POSITION_FMT.endDraw(program, "Position");
                    VertexFormat.COLOR_FMT.endDraw(program, "Color");
                }

                @Override
                public boolean hasPosition() {
                    return true;
                }

                @Override
                public boolean hasColor() {
                    return true;
                }

                @Override
                public boolean hasTexture() {
                    return false;
                }

                @Override
                public boolean hasNormal() {
                    return false;
                }
            });
        program.create();
        var fs = IFileProvider.of(HelloTriangleApp.class);
        var result = Shaders.linkSimple(program,
            PlainTextAsset.createStr(fs, "shaders/hellotriangle/shader.vert"),
            PlainTextAsset.createStr(fs, "shaders/hellotriangle/shader.frag"));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                program.getInfoLog());
        program.createUniform("ModelViewMat", GLUniformType.M4F);
        mesh = Geometry.generateTriangles(3,
            program.getLayout(),
            new Vector3fc[]{
                new Vector3f(0.0f, 0.5f, 0.0f),
                new Vector3f(-0.5f, -0.5f, 0.0f),
                new Vector3f(0.5f, -0.5f, 0.0f)
            },
            new Vector4fc[]{
                new Vector4f(1.0f, 0.0f, 0.0f, 1.0f),
                new Vector4f(0.0f, 1.0f, 0.0f, 1.0f),
                new Vector4f(0.0f, 0.0f, 1.0f, 1.0f)
            },
            null,
            null,
            null);
    }

    @Override
    public void onResize(int width, int height) {
        GL11C.glViewport(0, 0, width, height);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        program.bind();
        var pTime = getTime() * 10;
        transformation.setRotation(0, 0, (float) ((pTime * 0.2 + 0.4) * 0.5));
        program.getUniform("ModelViewMat").set(transformation.getMatrix());
        program.updateUniforms();
        mesh.render(program);
        program.unbind();
    }

    @Override
    public void close() {
        mesh.close();
        program.close();
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }
}
