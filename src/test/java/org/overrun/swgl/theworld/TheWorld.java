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

package org.overrun.swgl.theworld;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.MappedVertexLayout;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.util.Pair;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * TheWorld game. Only for learning.
 *
 * @author squid233
 * @since 0.1.0
 */
public class TheWorld extends GlfwApplication {
    public static void main(String[] args) {
        var game = new TheWorld();
        game.boot();
    }

    private static final IFileProvider FILE_PROVIDER = IFileProvider.CLASSPATH;
    private final ResManager resManager = new ResManager();
    private final GLProgram program = new GLProgram(new MappedVertexLayout(
        Pair.of("Position", VertexFormat.POSITION_FMT),
        Pair.of("Color", VertexFormat.COLOR_FMT),
        Pair.of("UV0", VertexFormat.TEXTURE_FMT)
    ).hasPosition(true).hasColor(true).hasTexture(true));
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f modelViewMat = new Matrix4f();
    private Texture2D blocksTexture;

    @Override
    public void preStart() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialWidth = 854;
        GlobalConfig.initialHeight = 480;
        GlobalConfig.initialTitle = "TheWorld " + GlobalConfig.SWGL_CORE_VERSION;
        GlobalConfig.initialSwapInterval = 0;
    }

    @Override
    public void start() {
        clearColor(0.4f, 0.6f, 0.9f, 1.0f);

        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        addResManager(resManager);

        program.create();
        Shaders.linkSimple(program,
            PlainTextAsset.createStr("theworld/tesselator.vert", FILE_PROVIDER),
            PlainTextAsset.createStr("theworld/tesselator.frag", FILE_PROVIDER));
        program.bind();
        program.getUniformSafe("Sampler0", GLUniformType.I1).set(0);
        program.updateUniforms();
        program.unbind();

        blocksTexture = new Texture2D();
        blocksTexture.recordTexParam(() -> {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        });
        blocksTexture.reload("theworld/blocks.png", FILE_PROVIDER);

        resManager.addResource(program);
        resManager.addResource(blocksTexture);
    }

    @Override
    public void onResize(int width, int height) {
        glViewport(0,0,width,height);
    }

    @Override
    public void run() {
        projMat.setPerspective((float) Math.toRadians(90.0),
            (float) window.getWidth() / (float) window.getHeight(),
            0.01f,
            1000.0f);

        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

        program.bind();
        program.getUniformSafe("ProjMat", GLUniformType.M4F).set(projMat);
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(modelViewMat);
        program.updateUniforms();
        program.unbind();
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
