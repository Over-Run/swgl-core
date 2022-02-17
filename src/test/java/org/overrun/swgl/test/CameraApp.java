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

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.math.Transformation;
import org.overrun.swgl.core.mesh.*;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43C.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class CameraApp extends GlfwApplication {
    public static void main(String[] args) {
        var app = new CameraApp();
        app.boot();
    }

    private static final IFileProvider FILE_PROVIDER = IFileProvider.of(CameraApp.class);
    private GLProgram.Default program;
    private Mesh mesh;
    private final Transformation transformation = new Transformation();
    private Texture2D container, awesomeFace;

    @Override
    public void prepare() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialTitle = "Camera Application";
        GlobalConfig.initialSwapInterval = 0;
    }

    @Override
    public void preStart() {
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
    }

    @Override
    public void start() {
        glEnable(GL_DEBUG_OUTPUT);
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.2f, 0.3f, 0.3f, 1.0f);
        program = new GLProgram.Default(
            new VertexLayout(VertexFormat.POSITION_FMT,
                VertexFormat.COLOR_FMT,
                VertexFormat.TEXTURE_FMT) {
                @Override
                public void beginDraw(GLProgram program) {
                    VertexFormat.POSITION_FMT.beginDraw(program, "Position");
                    VertexFormat.COLOR_FMT.beginDraw(program, "Color");
                    VertexFormat.TEXTURE_FMT.beginDraw(program, "UV0");
                }

                @Override
                public void endDraw(GLProgram program) {
                    VertexFormat.POSITION_FMT.endDraw(program, "Position");
                    VertexFormat.COLOR_FMT.endDraw(program, "Color");
                    VertexFormat.TEXTURE_FMT.endDraw(program, "UV0");
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
                    return true;
                }

                @Override
                public boolean hasNormal() {
                    return false;
                }
            });
        program.create();
        var result = Shaders.linkSimple(program,
            PlainTextAsset.createStr(FILE_PROVIDER, "shaders/camera/shader.vert"),
            PlainTextAsset.createStr(FILE_PROVIDER, "shaders/camera/shader.frag"));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                program.getInfoLog());
        program.createUniform("ModelViewMat", GLUniformType.M4F);
        program.createUniform("Sampler0", GLUniformType.I1);
        program.createUniform("Sampler1", GLUniformType.I1);
        program.bind();
        program.getUniform("Sampler0").set(0);
        program.getUniform("Sampler1").set(1);
        program.updateUniforms();
        program.unbind();
        mesh = Geometry.generateQuads(4,
            program.getLayout(),
            new Vector3fc[]{
                new Vector3f(-0.5f, 0.5f, 0.0f),
                new Vector3f(-0.5f, -0.5f, 0.0f),
                new Vector3f(0.5f, -0.5f, 0.0f),
                new Vector3f(0.5f, 0.5f, 0.0f)
            },
            new Vector4fc[]{
                new Vector4f(1.0f, 0.0f, 0.0f, 1.0f),
                new Vector4f(0.0f, 1.0f, 0.0f, 1.0f),
                new Vector4f(0.0f, 0.0f, 1.0f, 1.0f),
                new Vector4f(1.0f, 1.0f, 0.0f, 1.0f)
            },
            new Vector2fc[]{
                new Vector2f(0.0f, 0.0f),
                new Vector2f(0.0f, 1.0f),
                new Vector2f(1.0f, 1.0f),
                new Vector2f(1.0f, 0.0f)
            },
            null);
        mesh.setMaterial(new Material(ITextureProvider.of(
            unit -> switch (unit) {
                case 0 -> container;
                case 1 -> awesomeFace;
                default -> null;
            },
            0,
            1
        )));
        Runnable recorder = () -> {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        };
        container = new Texture2D();
        container.recordTexParam(recorder);
        container.reload("textures/camera/container.png", FILE_PROVIDER);
        awesomeFace = new Texture2D();
        awesomeFace.recordTexParam(recorder);
        awesomeFace.reload("textures/camera/awesomeface.png", FILE_PROVIDER);
    }

    @Override
    public void onResize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        program.bind();
        program.getUniform("ModelViewMat").set(transformation.getMatrix());
        program.updateUniforms();
        mesh.render(program);
        program.unbind();
    }

    @Override
    public void close() {
        if (container != null) {
            container.close();
        }
        if (awesomeFace != null) {
            awesomeFace.close();
        }
        if (mesh != null) {
            mesh.close();
        }
        if (program != null) {
            program.close();
        }
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }
}
