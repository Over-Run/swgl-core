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
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.tex.ITextureParam;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.gl.ims.GLLists;
import org.overrun.swgl.core.gui.font.SwglEasyFont;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.util.math.Numbers;
import org.overrun.swgl.core.util.timing.Timer;
import org.overrun.swgl.game.atlas.BlockAtlas;
import org.overrun.swgl.game.gui.TextRenderer;
import org.overrun.swgl.game.gui.hud.InGameHud;
import org.overrun.swgl.game.world.Chunk;
import org.overrun.swgl.game.world.World;
import org.overrun.swgl.game.world.WorldRenderer;
import org.overrun.swgl.game.world.block.Block;
import org.overrun.swgl.game.world.block.Blocks;
import org.overrun.swgl.game.world.entity.HumanEntity;
import org.overrun.swgl.game.world.entity.PlayerEntity;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL12C.*;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * Swgl game. Only for learning.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class SwglGame extends GlfwApplication {
    public static void main(String[] args) {
        SwglGame.instance = new SwglGame();
        SwglGame.instance.boot();
    }

    public static final float SENSITIVITY = 0.15f;
    private static SwglGame instance;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static final boolean PLACE_PREVIEW = true;
    private static final float GAMMA = 1.0f;
    private final FpsCamera camera = new FpsCamera();
    private World world;
    private WorldRenderer worldRenderer;
    private PlayerEntity player;
    public AssetManager assetManager;
    private HitResult hitResult;
    private boolean paused = false;
    private Block handBlock = Blocks.STONE;
    private int gameInfoTextLst;

    public static SwglGame getInstance() {
        return instance;
    }

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
        lglEnableAlphaTest();
        lglAlphaFunc(GL_GREATER, 0.5f);

        window.setIcon(FILE_PROVIDER, "swgl_game/openjdk.png");
        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        var resManager = new ResManager(this);

        assetManager = resManager.addResource(new AssetManager());
        Texture2D.createAsset(assetManager,
            BlockAtlas.TEXTURE,
            tex -> tex.setParam(target -> {
                glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
                glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameterf(target, GL_TEXTURE_MIN_LOD, 0);
                glTexParameterf(target, GL_TEXTURE_MAX_LOD, 4);
                glTexParameteri(target, GL_TEXTURE_MAX_LEVEL, 4);
            }),
            FILE_PROVIDER);
        ITextureParam texParam = target -> {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        };
        Texture2D.createAssetParam(assetManager,
            HumanEntity.HUMAN_TEXTURE,
            texParam,
            FILE_PROVIDER);
        Texture2D.createAssetParam(assetManager,
            InGameHud.CROSSING_HAIR_TEXTURE,
            texParam,
            FILE_PROVIDER);
        assetManager.reloadAssets(true);

        world = new World(Random.newSeed(), 256, 64, 256);
        worldRenderer = new WorldRenderer(world);
        player = new PlayerEntity(world);
        player.keyboard = keyboard;

        schedulePerLoop(4, () -> {
            if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_LEFT)) {
                destroyBlock();
                return true;
            }
            if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_RIGHT)) {
                placeBlock();
                return true;
            }
            return false;
        });

        resManager.addResource(worldRenderer);
        SwglEasyFont.initialize();
        gameInfoTextLst = TextRenderer.createText(0, 0, GlobalConfig.initialTitle);

        camera.limitedPitch = true;

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
                if (paused)
                    world.save();
            }
            case GLFW_KEY_1 -> handBlock = Blocks.STONE;
            case GLFW_KEY_2 -> handBlock = Blocks.GRASS_BLOCK;
            case GLFW_KEY_3 -> handBlock = Blocks.DIRT;
            case GLFW_KEY_4 -> handBlock = Blocks.BEDROCK;
            case GLFW_KEY_5 -> handBlock = Blocks.COBBLESTONE;
            case GLFW_KEY_6 -> handBlock = Blocks.OAK_PLANKS;
            case GLFW_KEY_7 -> handBlock = Blocks.OAK_LOG;
            case GLFW_KEY_8 -> handBlock = Blocks.OAK_SAPLING;
            case GLFW_KEY_9 -> handBlock = Blocks.OAK_LEAVES;
        }
    }

    private void destroyBlock() {
        if (hitResult != null) {
            world.setBlock(hitResult.x(), hitResult.y(), hitResult.z(), Blocks.AIR);
        }
    }

    private void placeBlock() {
        if (hitResult != null) {
            var face = hitResult.face();
            int x = hitResult.x() + face.getOffsetX();
            int y = hitResult.y() + face.getOffsetY();
            int z = hitResult.z() + face.getOffsetZ();
            if (world.canPlaceOn(hitResult.x(),
                hitResult.y(),
                hitResult.z(),
                handBlock,
                face)) {
                world.setBlock(x,
                    y,
                    z,
                    handBlock);
            }
        }
    }

    private void moveCameraToPlayer(double delta) {
        camera.smoothStep = (float) delta;
        lglTranslate(0.0f, 0.0f, -0.3f);
        lglMultMatrix(camera.getMatrix());
    }

    private Frustum setupCamera(double delta) {
        lglMatrixMode(MatrixMode.PROJECTION);
        lglLoadIdentity();
        lglPerspectiveDeg(90.0f,
            (float) window.getWidth() / (float) window.getHeight(),
            0.05f,
            1000.0f);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();
        moveCameraToPlayer(delta);
        return Frustum.getFrustum(lglGetMatrix(MatrixMode.PROJECTION), lglGetMatrix(MatrixMode.MODELVIEW));
    }

    @Override
    public void tick() {
        if (keyboard.isKeyDown(GLFW_KEY_G)) {
            var human = new HumanEntity(world, player.position.x, player.position.y, player.position.z);
            world.entities.put(human.uuid, human);
        }

        world.tick();

        camera.update();
        var pos = player.position;
        camera.setPosition(pos.x, pos.y + player.getEyeHeight(), pos.z);
        player.tick();
    }

    @Override
    public void run() {
        var frustum = setupCamera(timer.deltaTime);
        pick();

        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        enableCullFace();

        worldRenderer.updateDirtyChunks(player);

        setupFog(0);
        worldRenderer.render(0);
        for (var entity : world.entities.values()) {
            if (entity instanceof HumanEntity human && entity.isLit() && frustum.testAab(entity.aabb)) {
                human.render(timer.deltaTime);
            }
        }

        setupFog(1);
        worldRenderer.render(1);
        for (var entity : world.entities.values()) {
            if (entity instanceof HumanEntity human && !entity.isLit() && frustum.testAab(entity.aabb)) {
                human.render(timer.deltaTime);
            }
        }

        lglDisableLighting();

        if (hitResult != null) {
            lglDisableAlphaTest();
            worldRenderer.renderHit(hitResult);

            if (PLACE_PREVIEW && !handBlock.isAir()) {
                var face = hitResult.face();
                if (world.canPlaceOn(hitResult.x(),
                    hitResult.y(),
                    hitResult.z(),
                    handBlock,
                    face)) {
                    int tx = hitResult.x() + face.getOffsetX();
                    int ty = hitResult.y() + face.getOffsetY();
                    int tz = hitResult.z() + face.getOffsetZ();
                    if (world.isInsideWorld(tx, ty, tz)) {
                        Texture2D.getAsset(assetManager, BlockAtlas.TEXTURE).ifPresent(Texture2D::bind);
                        enableTexture2D();
                        enableBlend();
                        lglEnableLighting();
                        lglDisableColorMaterial();
                        lglSetLightModelAmbient(1.0f, 1.0f, 1.0f, ((float) Math.sin(Timer.getTime() * 10) + 1.0f) / 4.0f + 0.3f);
                        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        lglSetTexCoordArrayState(true);
                        lglPushMatrix();
                        lglTranslate(tx + 1, ty + 2, tz + 1);
                        lglBegin(GLDrawMode.TRIANGLES);
                        handBlock.render(world, 0, -1, -2, -1);
                        lglEnd();
                        lglSetTexCoordArrayState(false);
                        disableBlend();
                        disableTexture2D();
                        bindTexture2D(0);
                        lglPopMatrix();
                        lglDisableLighting();
                    }
                }
            }
            lglEnableAlphaTest();
        }

        drawGui(window.getWidth() * 0.5f, window.getHeight() * 0.5f);
    }

    @Override
    public void settingFrames() {
        Chunk.updates = 0;
    }

    private void pick() {
        hitResult = worldRenderer.pick(player, lglGetMatrix(MatrixMode.MODELVIEW), camera);
    }

    private void drawGui(float width, float height) {
        clear(DEPTH_BUFFER_BIT);

        lglGetMatrix(MatrixMode.PROJECTION)
            .setOrtho2D(0,
                width,
                height,
                0);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();

        enableBlend();
        blendFuncSeparate(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_ONE, GL_ZERO);
        lglPushMatrix();
        lglTranslate(width * 0.5f, height * 0.5f, 0.0f);
        SpriteBatch.draw(InGameHud.CROSSING_HAIR_TEXTURE, -8.0f, -8.0f, 16.0f, 16.0f);
        lglPopMatrix();

        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        lglPushMatrix();
        lglTranslate(2, 2, 0);
        TextRenderer.drawText(gameInfoTextLst);
        TextRenderer.drawText(0, 10, frames + " fps, " + Chunk.updates + " chunk updates");
        TextRenderer.drawText(0, 20, "Daytime: " + (world.daytimeTick % 24000));
        lglPopMatrix();

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
        world.save();
        GLLists.lglDeleteLists(gameInfoTextLst, 1);
        lglDestroyContext();
        SwglEasyFont.destroy();
    }
}
