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

package org.overrun.swgl.game;

import org.joml.Random;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.util.Timer;
import org.overrun.swgl.core.util.math.Numbers;
import org.overrun.swgl.game.world.World;
import org.overrun.swgl.game.world.WorldRenderer;
import org.overrun.swgl.game.world.block.Block;
import org.overrun.swgl.game.world.block.Blocks;
import org.overrun.swgl.game.world.entity.Player;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * Swgl game. Only for learning.
 *
 * @author squid233
 * @since 0.1.0
 */
public class SwglGame extends GlfwApplication {
    public static void main(String[] args) {
        var game = new SwglGame();
        game.boot();
    }

    public static final float SENSITIVITY = 0.15f;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.of(SwglGame.class);
    private static final boolean PLACE_PREVIEW = true;
    private static final float GAMMA = 1.0f;
    private final ResManager resManager = new ResManager();
    private final FpsCamera camera = new FpsCamera();
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;
    private Texture2D blocksTexture;
    private Texture2D crossingHairTexture;
    private HitResult hitResult;
    private int lastDestroyTick = 0;
    private int lastPlaceTick = 0;
    private int gameTicks = 0;
    private boolean paused = false;
    private Block handBlock = Blocks.STONE;

    @Override
    public void prepare() {
        GlobalConfig.initialWidth = 854;
        GlobalConfig.initialHeight = 480;
        GlobalConfig.initialTitle = "SWGL Game " + GlobalConfig.SWGL_CORE_VERSION;
        GlobalConfig.initialSwapInterval = 0;
    }

    @Override
    public void start() {
        clearColor(0.4f, 0.6f, 0.9f, 1.0f);
        enableDepthTest();
        setDepthFunc(GL_LEQUAL);
        lglRequestContext();

        window.setIcon(FILE_PROVIDER, "swgl_game/openjdk.png");
        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        addResManager(resManager);

        Runnable texParam = () -> {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        };
        blocksTexture = new Texture2D();
        blocksTexture.recordTexParam(texParam);
        blocksTexture.reload("swgl_game/blocks.png", FILE_PROVIDER);
        crossingHairTexture = new Texture2D();
        crossingHairTexture.recordTexParam(texParam);
        crossingHairTexture.reload("swgl_game/crossing_hair.png", FILE_PROVIDER);

        world = new World(Random.newSeed(), 256, 64, 256);
        worldRenderer = new WorldRenderer(world);
        player = new Player(world);
        player.keyboard = keyboard;

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
        switch (key) {
            case GLFW_KEY_ESCAPE -> {
                paused = !paused;
                mouse.setGrabbed(!paused);
                timer.timescale = paused ? 0.0f : 1.0f;
            }
            case GLFW_KEY_1 -> handBlock = Blocks.STONE;
            case GLFW_KEY_2 -> handBlock = Blocks.GRASS_BLOCK;
            case GLFW_KEY_3 -> handBlock = Blocks.DIRT;
            case GLFW_KEY_4 -> handBlock = Blocks.BEDROCK;
        }
    }

    @Override
    public void onMouseBtnPress(int btn, int mods) {
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_LEFT)) {
            destroyBlock(true);
        }
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            placeBlock(true);
        }
    }

    private void destroyBlock(boolean unlockTick) {
        if (hitResult != null) {
            if (unlockTick) {
                world.setBlock(hitResult.x(), hitResult.y(), hitResult.z(), Blocks.AIR);
                lastDestroyTick = gameTicks;
            }
        }
    }

    private void placeBlock(boolean unlockTick) {
        if (hitResult != null) {
            if (unlockTick) {
                var face = hitResult.face();
                int x = hitResult.x() + face.getOffsetX();
                int y = hitResult.y() + face.getOffsetY();
                int z = hitResult.z() + face.getOffsetZ();
                if (world.isReplaceable(x, y, z)) {
                    world.setBlock(x,
                        y,
                        z,
                        handBlock);
                    lastPlaceTick = gameTicks;
                }
            }
        }
    }

    private void moveCameraToPlayer(double delta) {
        camera.smoothStep = (float) delta;
        lglTranslate(0.0f, 0.0f, -0.3f);
        lglMultMatrix(camera.getMatrix());
    }

    private void setupCamera(double delta) {
        lglMatrixMode(MatrixMode.PROJECTION);
        lglLoadIdentity();
        lglPerspectiveDeg(90.0f,
            (float) window.getWidth() / (float) window.getHeight(),
            0.05f,
            1000.0f);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();
        moveCameraToPlayer(delta);
        Frustum.getFrustum(lglGetMatrix(MatrixMode.PROJECTION), lglGetMatrix(MatrixMode.MODELVIEW));
    }

    @Override
    public void tick() {
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_LEFT)) {
            destroyBlock(gameTicks - lastDestroyTick >= 4);
        }
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            placeBlock(gameTicks - lastPlaceTick >= 4);
        }

        world.tick();

        camera.update();
        var pos = player.position;
        camera.setPosition(pos.x, pos.y + player.getEyeHeight(), pos.z);
        player.tick();
        ++gameTicks;
    }

    @Override
    public void run() {
        setupCamera(timer.deltaTime);
        pick();

        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

        enableCullFace();

        worldRenderer.updateDirtyChunks(player);
        setupFog(0);
        blocksTexture.bind();
        enableTexture2D();
        worldRenderer.render(0);
        setupFog(1);
        worldRenderer.render(1);
        blocksTexture.unbind();

        lglDisableLighting();

        if (hitResult != null) {
            worldRenderer.renderHit(hitResult);

            if (PLACE_PREVIEW && !handBlock.isAir()) {
                var face = hitResult.face();
                int tx = hitResult.x() + face.getOffsetX();
                int ty = hitResult.y() + face.getOffsetY();
                int tz = hitResult.z() + face.getOffsetZ();
                if (world.isReplaceable(tx, ty, tz)) {
                    blocksTexture.bind();
                    enableTexture2D();
                    enableBlend();
                    blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    lglSetTexCoordArrayState(true);
                    lglBegin(GLDrawMode.TRIANGLES);
                    lglColor(1.0f, 1.0f, 1.0f, ((float) Math.sin(Timer.getTime() * 10) + 1.0f) / 4.0f + 0.3f);
                    handBlock.renderAll(tx, ty, tz);
                    lglEnd();
                    lglSetTexCoordArrayState(false);
                    disableBlend();
                    disableTexture2D();
                    blocksTexture.unbind();
                }
            }
        }

        drawGui();

        window.setTitle("SWGL Game 0.1.0 Daytime: " + (world.daytimeTick % 24000) + " FPS: " + frames);
    }

    private void pick() {
        hitResult = worldRenderer.pick(player, lglGetMatrix(MatrixMode.MODELVIEW), camera);
    }

    private void drawGui() {
        clear(DEPTH_BUFFER_BIT);

        enableBlend();
        blendFuncSeparate(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_ONE, GL_ZERO);
        lglMatrixMode(MatrixMode.PROJECTION);
        lglLoadIdentity();
        lglOrthoSymmetric(window.getWidth(), window.getHeight(), -300, 300);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();
        SpriteBatch.draw(crossingHairTexture, -16.0f, -16.0f, 32.0f, 32.0f);
        disableBlend();
    }

    private void setupFog(int layer) {
        if (layer == 0) {
            lglDisableLighting();
        } else if (layer == 1) {
            lglEnableLighting();
            lglEnableColorMaterial();
            float br = 0.6f;
            lglSetLightModelAmbient(br, br, br, 1.0f);
        }
    }

    @Override
    public void close() {
        lglDestroyContext();
    }
}
