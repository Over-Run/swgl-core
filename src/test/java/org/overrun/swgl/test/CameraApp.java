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

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.asset.tex.TextureParam;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.GLVertex;
import org.overrun.swgl.core.gl.shader.GLShaderCreator;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.model.VertexLayout;
import org.overrun.swgl.core.model.simple.SimpleMaterial;
import org.overrun.swgl.core.model.simple.SimpleMesh;
import org.overrun.swgl.core.model.simple.SimpleMeshes;
import org.overrun.swgl.core.util.Tri;
import org.overrun.swgl.core.util.math.Numbers;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * Tests the GLProgram, Mesh, AssetManager, Camera, Timer and Application, etc.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class CameraApp extends GlfwApplication {
    public static void main(String[] args) {
        new CameraApp().launch();
    }

    public static final float SENSITIVITY = 0.15f;
    public static final String CONTAINER_TEXTURE = "textures/camera/container.png";
    public static final String AWESOME_FACE_TEXTURE = "textures/camera/awesomeface.png";
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private GLProgram program;
    private GLProgram shaderSingleColor;
    private SimpleMesh containerModel;
    private AssetManager assetManager;
    /**
     * The textures.
     */
    private Texture2D container, awesomeFace;
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4fStack modelMat = new Matrix4fStack(2);
    private final Matrix4f viewMat = new Matrix4f();
    private final FpsCamera camera = new FpsCamera(-0.5f, 1.5f, 1.5f,
        (float) Math.toRadians(-45.0f), (float) Math.toRadians(-40.0f));

    private Texture2D createTexture(String name,
                                    TextureParam param) {
        return Texture2D.loadAsset(assetManager, name, FILE_PROVIDER, param);
    }

    @Override
    public void prepare() {
        WindowConfig.initialTitle = "Camera Application";
        WindowConfig.initialSwapInterval = 0;
    }

    @Override
    public void preStart() {
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
    }

    @Override
    public void start() {
        enableDebugOutput();
        enableDepthTest();
        enableCullFace();
        setDepthFunc(GL_LEQUAL);
        enableStencilTest();
        stencilFunc(GL_NOTEQUAL, 0, 0xff);
        stencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.2f, 0.3f, 0.3f, 1.0f);
        resManager = new ResManager();
        program = resManager.addResource(new GLProgram(
            new VertexLayout(
                VertexFormat.V3F,
                VertexFormat.C4UB,
                VertexFormat.T2F
            )
        ));
        program.bindAttribLoc(0, "Position");
        program.bindAttribLoc(1, "Color");
        program.bindAttribLoc(2, "UV0");
        var vertSrc = PlainTextAsset.createStr("shaders/camera/shader.vert", FILE_PROVIDER);
        boolean result = GLShaders.linkSimple(program,
            vertSrc,
            PlainTextAsset.createStr("shaders/camera/shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                                       program.getInfoLog());
        program.createUniform("ProjMat", GLUniformType.M4F).set(projMat);
        program.createUniform("ViewMat", GLUniformType.M4F).set(viewMat);
        program.createUniform("ModelMat", GLUniformType.M4F).set(modelMat);
        program.createUniform("Sampler0", GLUniformType.I1).set(0);
        program.createUniform("Sampler1", GLUniformType.I1).set(1);

        shaderSingleColor = resManager.addResource(new GLProgram(program.getLayout()));
        shaderSingleColor.bindAttribLoc(0, "Position");
        shaderSingleColor.bindAttribLoc(1, "Color");
        shaderSingleColor.bindAttribLoc(2, "UV0");
        result = GLShaders.linkSimple(shaderSingleColor,
            vertSrc,
            GLShaderCreator.createFragSingleColor("110", null, null, "0.04, 0.28, 0.26, 1.0"));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                                       shaderSingleColor.getInfoLog());
        shaderSingleColor.createUniform("ProjMat", GLUniformType.M4F).set(projMat);
        shaderSingleColor.createUniform("ViewMat", GLUniformType.M4F).set(viewMat);
        shaderSingleColor.createUniform("ModelMat", GLUniformType.M4F).set(modelMat);

        var v0 = new GLVertex().color(1.0f, 0.0f, 0.0f).texCoords(0.0f, 0.0f);
        var v1 = new GLVertex().color(0.0f, 1.0f, 0.0f).texCoords(0.0f, 1.0f);
        var v2 = new GLVertex().color(0.0f, 0.0f, 1.0f).texCoords(1.0f, 1.0f);
        var v3 = new GLVertex().color(1.0f, 1.0f, 0.0f).texCoords(1.0f, 0.0f);
        containerModel = SimpleMeshes.genQuads(program.getLayout(),
            // West -x
            v0.copy().position(0.0f, 1.0f, 0.0f),
            v1.copy().position(0.0f, 0.0f, 0.0f),
            v2.copy().position(0.0f, 0.0f, 1.0f),
            v3.copy().position(0.0f, 1.0f, 1.0f),
            // East +x
            v0.copy().position(1.0f, 1.0f, 1.0f),
            v1.copy().position(1.0f, 0.0f, 1.0f),
            v2.copy().position(1.0f, 0.0f, 0.0f),
            v3.copy().position(1.0f, 1.0f, 0.0f),
            // Down -y
            v0.copy().position(0.0f, 0.0f, 1.0f),
            v1.copy().position(0.0f, 0.0f, 0.0f),
            v2.copy().position(1.0f, 0.0f, 0.0f),
            v3.copy().position(1.0f, 0.0f, 1.0f),
            // Up +y
            v0.copy().position(0.0f, 1.0f, 0.0f),
            v1.copy().position(0.0f, 1.0f, 1.0f),
            v2.copy().position(1.0f, 1.0f, 1.0f),
            v3.copy().position(1.0f, 1.0f, 0.0f),
            // North -z
            v0.copy().position(1.0f, 1.0f, 0.0f),
            v1.copy().position(1.0f, 0.0f, 0.0f),
            v2.copy().position(0.0f, 0.0f, 0.0f),
            v3.copy().position(0.0f, 1.0f, 0.0f),
            // South +z
            v0.copy().position(0.0f, 1.0f, 1.0f),
            v1.copy().position(0.0f, 0.0f, 1.0f),
            v2.copy().position(1.0f, 0.0f, 1.0f),
            v3.copy().position(1.0f, 1.0f, 1.0f));
        containerModel.setMaterial(new SimpleMaterial(
            unit -> switch (unit) {
                case 0 -> Tri.of(0, 1, container);
                case 1 -> Tri.of(0, 1, awesomeFace);
                default -> Tri.of(0, 1, null);
            }
        ));
        resManager.addResource(containerModel);
        var param = new TextureParam().minFilter(GL_LINEAR).magFilter(GL_LINEAR);
        assetManager = resManager.addResource(new AssetManager());
        container = createTexture(CONTAINER_TEXTURE, param);
        awesomeFace = createTexture(AWESOME_FACE_TEXTURE, param);
        assetManager.freeze();

        camera.limitedPitch = true;

        mouse.setGrabbed(true);
    }

    @Override
    public void onCursorPos(double x, double y,
                            double xd, double yd) {
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_RIGHT) || mouse.isGrabbed()) {
            camera.rotate((float) Math.toRadians(xd * SENSITIVITY),
                (float) -Math.toRadians(yd * SENSITIVITY));
        }
    }

    @Override
    public void tick() {
        float speed = 0.05f;
        float xa = 0, ya = 0, za = 0;
        if (keyboard.isKeyDown(GLFW_KEY_A)) {
            --xa;
        }
        if (keyboard.isKeyDown(GLFW_KEY_D)) {
            ++xa;
        }
        if (keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            --ya;
        }
        if (keyboard.isKeyDown(GLFW_KEY_SPACE)) {
            ++ya;
        }
        if (keyboard.isKeyDown(GLFW_KEY_W)) {
            --za;
        }
        if (keyboard.isKeyDown(GLFW_KEY_S)) {
            ++za;
        }
        camera.update();
        camera.moveRelative(xa, ya, za, speed);
    }

    @Override
    public void run() {
        // fovy = toRadians(90)
        projMat.setPerspective(Numbers.RAD90F,
            (float) window.getWidth() / (float) window.getHeight(),
            0.01f,
            100.0f);

        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT | STENCIL_BUFFER_BIT);

        shaderSingleColor.bind();
        shaderSingleColor.getUniform("ProjMat").set(projMat);
        camera.smoothStep = (float) timer.partialTick;
        viewMat.set(camera.getMatrix());
        modelMat.identity();
        shaderSingleColor.getUniform("ViewMat").set(viewMat);
        shaderSingleColor.getUniform("ModelMat").set(modelMat);
        shaderSingleColor.updateUniforms();

        program.bind();

        program.getUniform("ProjMat").set(projMat);
        program.getUniform("ViewMat").set(viewMat);
        program.getUniform("ModelMat").set(modelMat);
        program.updateUniforms();

        //stencilMask(0x00);
        //draw other objects

        // draw objects as normal
        stencilFunc(GL_ALWAYS, 1, 0xff);
        stencilMask(0xff);
        containerModel.render(GLDrawMode.TRIANGLES);

        // draw scaled objects
        stencilFunc(GL_NOTEQUAL, 1, 0xff);
        stencilMask(0x00);
        disableDepthTest();
        shaderSingleColor.bind();
        final float scale = 1.1f;
        shaderSingleColor.getUniform("ModelMat")
            .set(modelMat.pushMatrix()
                .translate(0.5f, 0.5f, 0.5f)
                .scale(scale)
                .translate(-0.5f, -0.5f, -0.5f));
        modelMat.popMatrix();
        shaderSingleColor.updateUniforms();
        containerModel.render(GLDrawMode.TRIANGLES);
        stencilMask(0xff);
        stencilFunc(GL_ALWAYS, 0, 0xff);
        enableDepthTest();

        useProgram(0);

        window.setTitle(WindowConfig.initialTitle + ", " + frames + " fps");
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            mouse.setGrabbed(!mouse.isGrabbed());
        }
    }
}
