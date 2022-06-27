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
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.tex.ITextureParam;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.shader.GLShaderCreator;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.model.VertexLayout;
import org.overrun.swgl.core.model.simple.SimpleMaterial;
import org.overrun.swgl.core.model.simple.SimpleModel;
import org.overrun.swgl.core.model.simple.SimpleModels;
import org.overrun.swgl.core.util.Tri;

import java.lang.Math;

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
    private SimpleModel containerModel;
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

    private void createTextureAsset(String name,
                                    ITextureParam param) {
        Texture2D.createAssetParam(assetManager, name, param, FILE_PROVIDER);
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
        program.create();
        var vertSrc = PlainTextAsset.createStr("shaders/camera/shader.vert", FILE_PROVIDER);
        boolean result = GLShaders.linkSimple(program,
            vertSrc,
            PlainTextAsset.createStr("shaders/camera/shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                                       program.getInfoLog());
        program.bindAttribLoc(0, "Position");
        program.bindAttribLoc(1, "Color");
        program.bindAttribLoc(2, "UV0");
        program.createUniform("ProjMat", GLUniformType.M4F).set(projMat);
        program.createUniform("ViewMat", GLUniformType.M4F).set(viewMat);
        program.createUniform("ModelMat", GLUniformType.M4F).set(modelMat);
        program.createUniform("Sampler0", GLUniformType.I1).set(0);
        program.createUniform("Sampler1", GLUniformType.I1).set(1);

        shaderSingleColor = resManager.addResource(new GLProgram(program.getLayout()));
        shaderSingleColor.create();
        result = GLShaders.linkSimple(shaderSingleColor,
            vertSrc,
            GLShaderCreator.createFragSingleColor("110", null, null, "0.04, 0.28, 0.26, 1.0"));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                                       shaderSingleColor.getInfoLog());
        shaderSingleColor.bindAttribLoc(0, "Position");
        shaderSingleColor.bindAttribLoc(1, "Color");
        shaderSingleColor.bindAttribLoc(2, "UV0");
        shaderSingleColor.createUniform("ProjMat", GLUniformType.M4F).set(projMat);
        shaderSingleColor.createUniform("ViewMat", GLUniformType.M4F).set(viewMat);
        shaderSingleColor.createUniform("ModelMat", GLUniformType.M4F).set(modelMat);

        containerModel = SimpleModels.genQuads(24,
            new Vector3fc[]{
                // West -x
                new Vector3f(0.0f, 1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 1.0f, 1.0f),
                // East +x
                new Vector3f(1.0f, 1.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(1.0f, 1.0f, 0.0f),
                // Down -y
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(1.0f, 0.0f, 1.0f),
                // Up +y
                new Vector3f(0.0f, 1.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 1.0f),
                new Vector3f(1.0f, 1.0f, 1.0f),
                new Vector3f(1.0f, 1.0f, 0.0f),
                // North -z
                new Vector3f(1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f),
                // South +z
                new Vector3f(0.0f, 1.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 1.0f, 1.0f)
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
        containerModel.getMesh(0)
            .setMaterial(new SimpleMaterial(
                unit -> switch (unit) {
                    case 0 -> Tri.of(0, 1, container);
                    case 1 -> Tri.of(0, 1, awesomeFace);
                    default -> Tri.of(0, 1, null);
                }
            ));
        resManager.addResource(containerModel);
        ITextureParam param = target -> {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        };
        assetManager = resManager.addResource(new AssetManager());
        createTextureAsset(CONTAINER_TEXTURE, param);
        createTextureAsset(AWESOME_FACE_TEXTURE, param);
        assetManager.reloadAssets(true);
        assetManager.freeze();
        container = Texture2D.getAsset(assetManager, CONTAINER_TEXTURE).orElseThrow();
        awesomeFace = Texture2D.getAsset(assetManager, AWESOME_FACE_TEXTURE).orElseThrow();

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
        camera.moveRelative(xa * speed, ya * speed, za * speed);
    }

    @Override
    public void run() {
        // fovy = toRadians(90)
        projMat.setPerspective(1.5707963267948966f,
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
        containerModel.render(program);

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
        containerModel.render(program);
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
