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

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.BuiltinVertexLayouts;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class IWannaSwgl extends GlfwApplication {
    public static void main(String[] args) {
        var game = new IWannaSwgl();
        game.launch();
    }

    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private GLProgram t2c4v3;
    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4fStack model = new Matrix4fStack(8);
    private final Level level = new Level();

    @Override
    public void prepare() {
        GlobalConfig.initialWidth = 768;
        GlobalConfig.initialHeight = 600;
        GlobalConfig.initialTitle = "I wanna Swgl";
        GlobalConfig.requiredGlMinorVer = 3;
    }

    @Override
    public void preStart() {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    }

    @Override
    public void start() {
        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        clearColor(0.4f, 0.6f, 0.9f, 1.0f);
        var rm = new ResManager(this);
        t2c4v3 = rm.addResource(new GLProgram(BuiltinVertexLayouts.T2F_C4UB_V3F));
        t2c4v3.create();
        Shaders.linkSimple(t2c4v3,
            "shaders/iwannaswgl/t2c4v3.vert",
            "shaders/iwannaswgl/t2c4v3.frag",
            FILE_PROVIDER);
        t2c4v3.getUniformSafe("Sampler0", GLUniformType.I1).set(0);

        level.setBlock(0, 0, Blocks.PLACEHOLDER);
        level.setBlock(0, 1, Blocks.PLACEHOLDER);
        level.setBlock(0, 2, Blocks.PLACEHOLDER);
        level.setBlock(0, 3, Blocks.PLACEHOLDER);
    }

    private void setTexState(boolean enabled) {
        t2c4v3.getUniformSafe("TextureEnabled", GLUniformType.I1).set(enabled);
    }

    @Override
    public void run() {
        proj.setOrtho2D(0, window.getWidth(), 0, window.getHeight());
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        t2c4v3.bind();
        t2c4v3.getUniformSafe("ProjMat", GLUniformType.M4F).set(proj);
        t2c4v3.getUniformSafe("ViewMat", GLUniformType.M4F).set(view);
        model.pushMatrix();
        model.scale(24.0f);
        t2c4v3.getUniformSafe("ModelMat", GLUniformType.M4F).set(model);
        model.popMatrix();
        setTexState(false);
        t2c4v3.updateUniforms();
        level.renderScene(t2c4v3);
        setTexState(true);
        t2c4v3.updateUniforms();
        t2c4v3.unbind();
    }

    @Override
    public void close() {
        level.deleteScene();
    }
}
