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
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.model.MappedVertexLayout;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.util.Pair;
import org.overrun.swgl.core.util.math.Numbers;
import org.overrun.swgl.theworld.world.World;
import org.overrun.swgl.theworld.world.WorldRenderer;
import org.overrun.swgl.theworld.world.entity.Player;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

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

    public static final float SENSITIVITY = 0.15f;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.CLASSPATH;
    private final ResManager resManager = new ResManager();
    private final GLProgram program = new GLProgram(new MappedVertexLayout(
        Pair.of("Position", VertexFormat.POSITION_FMT),
        Pair.of("Color", VertexFormat.COLOR_FMT),
        Pair.of("UV0", VertexFormat.TEXTURE_FMT)
    ).hasPosition(true).hasColor(true).hasTexture(true));
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f modelMat = new Matrix4f();
    private final Matrix4f viewMat = new Matrix4f();
    private final Matrix4f modelViewMat = new Matrix4f();
    private final FpsCamera camera = new FpsCamera();
    private final Frustum frustum = Frustum.getInstance();
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;
    private Texture2D blocksTexture;
    private Texture2D crossingHairTexture;

    @Override
    public void preStart() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialWidth = 854;
        GlobalConfig.initialHeight = 480;
        GlobalConfig.initialTitle = "TheWorld " + GlobalConfig.SWGL_CORE_VERSION;
        GlobalConfig.initialSwapInterval = 0;
        GlobalConfig.requireGlMinorVer = 3;
    }

    @Override
    public void start() {
        clearColor(0.4f, 0.6f, 0.9f, 1.0f);
        enableDepthTest();
        setDepthFunc(GL_LEQUAL);

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

        Runnable texParam = () -> {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        };
        blocksTexture = new Texture2D();
        blocksTexture.recordTexParam(texParam);
        blocksTexture.reload("theworld/blocks.png", FILE_PROVIDER);
        crossingHairTexture = new Texture2D();
        crossingHairTexture.recordTexParam(texParam);
        crossingHairTexture.reload("theworld/crossing_hair.png", FILE_PROVIDER);

        world = new World(64, 64, 64);
        worldRenderer = new WorldRenderer(world);
        player = new Player(world);
        player.keyboard = keyboard;

        resManager.addResource(program);
        resManager.addResource(blocksTexture);
        resManager.addResource(crossingHairTexture);
        resManager.addResource(worldRenderer);

        camera.restrictPitch = true;

        mouse.setGrabbed(true);
    }

    @Override
    public void onResize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onCursorPos(double x, double y, double xd, double yd) {
        if (mouse.isGrabbed()) {
            player.rotate((float) Math.toRadians(xd * SENSITIVITY),
                (float) -Math.toRadians(yd * SENSITIVITY));
            camera.setRotation(player.rotation.y - Numbers.RAD90F, player.rotation.x);
        }
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            mouse.setGrabbed(!mouse.isGrabbed());
        }
    }

    @Override
    public void tick() {
        camera.update();
        var pos = player.position;
        camera.setPosition(pos.x, pos.y + player.getEyeHeight(), pos.z);
        player.tick();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        projMat.setPerspective(Numbers.RAD90F,
            (float) window.getWidth() / (float) window.getHeight(),
            0.05f,
            1000.0f);
        camera.smoothStep = (float) timer.deltaTime;
        viewMat.translation(0.0f, 0.0f, -0.3f).mul(camera.getMatrix());
        modelMat.identity();
        Frustum.getFrustum(projMat, viewMat);
        enableCullFace();

        worldRenderer.updateDirtyChunks();
        program.bind();
        program.getUniformSafe("ProjMat", GLUniformType.M4F).set(projMat);
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(viewMat.mul(modelMat, modelViewMat));
        program.getUniformSafe("HasColor", GLUniformType.I1).set(true);
        program.getUniformSafe("HasTexture", GLUniformType.I1).set(true);
        program.updateUniforms();
        blocksTexture.bind();
        worldRenderer.render(frustum);
        blocksTexture.unbind();
        drawGui();
        program.unbind();
    }

    private void drawGui() {
        clear(DEPTH_BUFFER_BIT);
        projMat.setOrthoSymmetric(window.getWidth(), window.getHeight(), -300, 300);
        viewMat.identity();
        modelMat.identity();

        program.getUniformSafe("ProjMat", GLUniformType.M4F).set(projMat);
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(viewMat.mul(modelMat, modelViewMat));
        program.updateUniforms();

        SpriteBatch.draw(Tesselator.getInstance(), crossingHairTexture, -16.0f, -16.0f, 32.0f, 32.0f);
    }

    @Override
    public void close() {
        Tesselator.getInstance().close();
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
