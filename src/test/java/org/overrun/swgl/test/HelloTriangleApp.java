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
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.Geometry;
import org.overrun.swgl.core.model.MappedVertexLayout;
import org.overrun.swgl.core.model.mesh.Mesh;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.util.Timer;
import org.overrun.swgl.core.util.math.Transformation;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
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

    private final ResManager resManager = new ResManager();
    private GLProgram program;
    private Mesh mesh;
    private final Transformation transformation = new Transformation();

    @Override
    public void prepare() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialTitle = "Hello Triangle Application";
        GlobalConfig.initialSwapInterval = 0;
    }

    @Override
    public void preStart() {
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
    }

    @Override
    public void start() {
        GLStateMgr.enableDebugOutput();
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        addResManager(resManager);
        program = resManager.addResource(new GLProgram(
            new MappedVertexLayout(
                "Position", VertexFormat.POSITION_FMT,
                "Color", VertexFormat.COLOR_FMT
            ).hasPosition(true)
                .hasColor(true)
        ));
        program.create();
        var fs = IFileProvider.of(HelloTriangleApp.class);
        var result = Shaders.linkSimple(program,
            PlainTextAsset.createStr("shaders/hellotriangle/shader.vert", fs),
            PlainTextAsset.createStr("shaders/hellotriangle/shader.frag", fs));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                program.getInfoLog());
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
        resManager.addResource(mesh);
    }

    @Override
    public void onResize(int width, int height) {
        GL11C.glViewport(0, 0, width, height);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        program.bind();
        var pTime = Timer.getTime() * 10;
        transformation.setRotation(0, 0, (float) ((pTime * 0.2 + 0.4) * 0.5));
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(transformation.getMatrix());
        program.updateUniforms();
        mesh.render(program);
        program.unbind();
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}