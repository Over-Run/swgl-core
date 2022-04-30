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
import org.overrun.swgl.core.asset.tex.ITextureParam;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
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
import org.overrun.swgl.core.util.math.Transformation;

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
public class CameraApp extends GlfwApplication {
    public static void main(String[] args) {
        var app = new CameraApp();
        app.launch();
    }

    public static final float SENSITIVITY = 0.15f;
    public static final String CONTAINER_TEXTURE = "textures/camera/container.png";
    public static final String AWESOME_FACE_TEXTURE = "textures/camera/awesomeface.png";
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private GLProgram program;
    private SimpleModel containerModel;
    private final Transformation transformation = new Transformation();
    private AssetManager assetManager;
    /**
     * The textures.
     */
    private Texture2D container, awesomeFace;
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f modelViewMat = new Matrix4f();
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
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.2f, 0.3f, 0.3f, 1.0f);
        var resManager = new ResManager(this);
        program = resManager.addResource(new GLProgram(
            new VertexLayout(
                VertexFormat.V3F,
                VertexFormat.C4UB,
                VertexFormat.T2F
            )
        ));
        program.create();
        var result = GLShaders.linkSimple(program,
            "shaders/camera/shader.vert",
            "shaders/camera/shader.frag",
            FILE_PROVIDER);
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                program.getInfoLog());
        program.bindAttribLoc(0, "Position");
        program.bindAttribLoc(1, "Color");
        program.bindAttribLoc(2, "UV0");
        program.bind();
        program.getUniformSafe("Sampler0", GLUniformType.I1).set(0);
        program.getUniformSafe("Sampler1", GLUniformType.I1).set(1);
        program.updateUniforms();
        program.unbind();
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

        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

        program.bind();

        program.getUniformSafe("ProjMat", GLUniformType.M4F).set(projMat);
        camera.smoothStep = (float) timer.deltaTime;
        // ModelView = View * Model
        // ViewMat
        modelViewMat.set(camera.getMatrix())
            // ModelMat
            .mul(transformation.getMatrix());
        program.getUniformSafe("ModelViewMat", GLUniformType.M4F).set(modelViewMat);
        program.updateUniforms();

        containerModel.render(program);

        program.unbind();

        window.setTitle(WindowConfig.initialTitle + ", " + frames + " fps");
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            mouse.setGrabbed(!mouse.isGrabbed());
        }
    }
}
