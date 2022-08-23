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

import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.*;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.BuiltinVertexLayouts;
import org.overrun.swgl.core.model.simple.SimpleMesh;
import org.overrun.swgl.core.util.LogFactory9;
import org.overrun.swgl.core.util.math.Transformation;
import org.overrun.swgl.core.util.timing.Timer;
import org.slf4j.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class HelloTriangleApp extends GlfwApplication {
    public static void main(String[] args) {
        new HelloTriangleApp().launch();
    }

    private static final Logger logger = LogFactory9.getLoggerS();
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private GLProgram program;
    private SimpleMesh triangle;
    private final Transformation transformation = new Transformation();

    @Override
    public void prepare() {
        logger.info("Preparing!");
        WindowConfig.initialTitle = "Hello Triangle Application";
        WindowConfig.initialSwapInterval = 0;
    }

    @Override
    public void preStart() {
        logger.info("Pre starting!");
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
    }

    @Override
    public void start() {
        logger.info("Starting!");
        GLStateMgr.enableDebugOutput();
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        resManager = new ResManager();
        program = resManager.addResource(new GLProgram(BuiltinVertexLayouts::C4UB_V3F));
        program.bindAttribLoc(0, "Color");
        program.bindAttribLoc(1, "Position");
        var result = GLShaders.linkSimple(program,
            "shaders/hellotriangle/shader.vert",
            "shaders/hellotriangle/shader.frag",
            FILE_PROVIDER);
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                                       program.getInfoLog());
        triangle = new SimpleMesh(program.getLayout(),
            new GLVertex().position(0.0f, 0.5f, 0.0f).color(1.0f, 0.0f, 0.0f),
            new GLVertex().position(-0.5f, -0.5f, 0.0f).color(0.0f, 1.0f, 0.0f),
            new GLVertex().position(0.5f, -0.5f, 0.0f).color(0.0f, 0.0f, 1.0f));
        resManager.addResource(triangle);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        program.bind();
        var pTime = Timer.getTime() * 10;
        transformation.setRotation(0, 0, (float) ((pTime * 0.2 + 0.4) * 0.5));
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(transformation.getMatrix());
        program.updateUniforms();
        triangle.render(GLDrawMode.TRIANGLES);
        program.unbind();
    }
}
