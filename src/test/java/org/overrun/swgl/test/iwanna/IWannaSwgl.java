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

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.BuiltinVertexLayouts;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.blendFunc;
import static org.overrun.swgl.core.gl.GLStateMgr.enableBlend;

/**
 * @author squid233
 * @since 0.2.0
 */
public class IWannaSwgl extends GlfwApplication {
    public static void main(String[] args) {
        var game = new IWannaSwgl();
        game.launch();
    }

    private static final boolean DEBUG = false;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private GLProgram t2c4v3;
    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4fStack model = new Matrix4fStack(8);
    private Level level;
    private Duke duke;
    private int debugBBList = 0;

    @Override
    public void prepare() {
        GlobalConfig.initialWidth = 768;
        GlobalConfig.initialHeight = 600;
        GlobalConfig.initialTitle = "I wanna Swgl";
        GlobalConfig.requiredGlMinorVer = 3;
        GlobalConfig.useLegacyGL = DEBUG;
    }

    @Override
    public void preStart() {
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    }

    private void initDebug() {
        debugBBList = GL11.glGenLists(1);
        GL11.glNewList(debugBBList, GL11.GL_COMPILE);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(1.0f, 1.0f);
        GL11.glVertex2f(0.0f, 1.0f);
        GL11.glVertex2f(0.0f, 0.0f);
        GL11.glVertex2f(1.0f, 0.0f);
        GL11.glEnd();
        GL11.glEndList();
    }

    @Override
    public void start() {
        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        clearColor(0.4f, 0.6f, 0.9f, 1.0f);
        enableBlend();
        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        var rm = new ResManager(this);
        t2c4v3 = rm.addResource(new GLProgram(BuiltinVertexLayouts.T2F_C4UB_V3F));
        t2c4v3.create();
        Shaders.linkSimple(t2c4v3,
            "shaders/iwannaswgl/t2c4v3.vert",
            "shaders/iwannaswgl/t2c4v3.frag",
            FILE_PROVIDER);
        t2c4v3.getUniformSafe("Sampler0", GLUniformType.I1).set(0);

        level = new Level();
        for (int x = 0, c = Level.SCENE_WIDTH; x < c; x++) {
            level.setBlock(x, 0, Blocks.PLACEHOLDER);
        }
        level.setBlock(0, 1, Blocks.PLACEHOLDER);
        level.setBlock(0, 2, Blocks.PLACEHOLDER);
        level.setBlock(0, 3, Blocks.PLACEHOLDER);
        level.setBlock(Level.SCENE_WIDTH - 1, 1, Blocks.PLACEHOLDER);
        level.setBlock(Level.SCENE_WIDTH - 1, 2, Blocks.PLACEHOLDER);
        level.setBlock(Level.SCENE_WIDTH - 1, 3, Blocks.PLACEHOLDER);

        DukeModel.build(t2c4v3.getLayout());
        duke = new Duke();
        duke.spawn();
        duke.level = level;

        if (DEBUG)
            initDebug();
    }

    private void setTexState(boolean enabled) {
        t2c4v3.getUniformSafe("TextureEnabled", GLUniformType.I1).set(enabled);
    }

    @Override
    public void tick() {
        duke.tick(keyboard);
    }

    private void renderDebug(float dt) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadMatrixf(proj.get(new float[16]));
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadMatrixf(view.get(new float[16]));
        GL11.glMultMatrixf(model.get(new float[16]));
        GL11.glPushMatrix();
        GL11.glTranslatef(duke.position.x, duke.position.y, 0.0f);
        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glCallList(debugBBList);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(duke.prevPosition.x, duke.prevPosition.y, 0.0f);
        GL11.glColor3f(0.0f, 1.0f, 0.0f);
        GL11.glCallList(debugBBList);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(
            Math.lerp(duke.prevPosition.x, duke.position.x, dt),
            Math.lerp(duke.prevPosition.y, duke.position.y, dt),
            0.0f);
        GL11.glColor3f(0.0f, 0.0f, 1.0f);
        GL11.glCallList(debugBBList);
        GL11.glPopMatrix();
    }

    @Override
    public void run() {
        proj.setOrtho2D(0, window.getWidth(), 0, window.getHeight());
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        t2c4v3.bind();
        t2c4v3.getUniformSafe("ProjMat", GLUniformType.M4F).set(proj);
        t2c4v3.getUniformSafe("ViewMat", GLUniformType.M4F).set(view);
        t2c4v3.getUniformSafe("ModelMat", GLUniformType.M4F).set(model.scaling(24.0f));
        setTexState(false);
        t2c4v3.updateUniforms();
        level.renderScene(t2c4v3);
        setTexState(true);
        float dt = (float) timer.deltaTime;
        model.pushMatrix().translate(
            Math.lerp(duke.prevPosition.x, duke.position.x, dt),
            Math.lerp(duke.prevPosition.y, duke.position.y, dt),
            0.0f);
        t2c4v3.getUniformSafe("ModelMat", GLUniformType.M4F).set(model);
        model.popMatrix();
        t2c4v3.updateUniforms();
        DukeModel.getTexture().bind();
        DukeModel.render();
        t2c4v3.unbind();

        if (DEBUG)
            renderDebug(dt);
    }

    @Override
    public void close() {
        level.deleteScene();
        DukeModel.destroy();
    }
}
