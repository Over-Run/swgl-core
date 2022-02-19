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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.mesh.*;
import org.overrun.swgl.core.util.math.Transformation;

import java.lang.Math;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        app.boot();
    }

    public static final float SENSITIVITY = 0.15f;
    public static final String CONTAINER_TEXTURE = "textures/camera/container.png";
    public static final String AWESOME_FACE_TEXTURE = "textures/camera/awesomeface.png";
    private static final IFileProvider FILE_PROVIDER = IFileProvider.of(CameraApp.class);
    private GLProgram program;
    private Mesh mesh;
    private final Transformation transformation = new Transformation();
    private AssetManager assetManager;
    /**
     * The Supplier to get the texture lazily.
     */
    private Supplier<Texture2D> container, awesomeFace;
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f modelViewMat = new Matrix4f();
    private final FpsCamera camera = new FpsCamera(-0.5f, 1.5f, 1.5f,
        (float) Math.toRadians(-45.0f), (float) Math.toRadians(-40.0f));
    private final Vector3f prevCameraPos = new Vector3f(camera.getPosition());

    private void createTextureAsset(String name,
                                    Consumer<Texture2D> consumer) {
        assetManager.createAsset(name, Texture2D.class, consumer, FILE_PROVIDER);
    }

    @Override
    public void prepare() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialTitle = "Camera Application";
        GlobalConfig.initialSwapInterval = 0;
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
        program = new GLProgram(
            new MappedVertexLayout(
                "Position", VertexFormat.POSITION_FMT,
                "Color", VertexFormat.COLOR_FMT,
                "UV0", VertexFormat.TEXTURE_FMT
            ).hasPosition(true)
                .hasColor(true)
                .hasTexture(true)
        );
        program.create();
        var result = Shaders.linkSimple(program,
            PlainTextAsset.createStr("shaders/camera/shader.vert", FILE_PROVIDER),
            PlainTextAsset.createStr("shaders/camera/shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the OpenGL program. " +
                program.getInfoLog());
        program.createUniform("ProjMat", GLUniformType.M4F);
        program.createUniform("ModelViewMat", GLUniformType.M4F);
        program.createUniform("Sampler0", GLUniformType.I1);
        program.createUniform("Sampler1", GLUniformType.I1);
        program.bind();
        program.getUniform("Sampler0").set(0);
        program.getUniform("Sampler1").set(1);
        program.updateUniforms();
        program.unbind();
        mesh = Geometry.generateQuads(24,
            program.getLayout(),
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
        mesh.setMaterial(new Material(ITextureProvider.of(
            unit -> switch (unit) {
                case 0 -> container.get();
                case 1 -> awesomeFace.get();
                default -> null;
            },
            0,
            1
        )));
        Consumer<Texture2D> recorder = tex ->
            tex.recordTexParam(() -> {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            });
        assetManager = new AssetManager();
        createTextureAsset(CONTAINER_TEXTURE, recorder);
        createTextureAsset(AWESOME_FACE_TEXTURE, recorder);
        assetManager.reloadAssets(true);
        assetManager.freeze();
        container = () -> assetManager.getAsset(CONTAINER_TEXTURE, Texture2D.class);
        awesomeFace = () -> assetManager.getAsset(AWESOME_FACE_TEXTURE, Texture2D.class);

        camera.restrictPitch = true;

        mouse.setGrabbed(true);
    }

    @Override
    public void onResize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onCursorPos(double x, double y,
                            double xd, double yd) {
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_RIGHT) || mouse.isGrabbed()) {
            camera.rotate((float) -Math.toRadians(xd) * SENSITIVITY,
                (float) -Math.toRadians(yd) * SENSITIVITY);
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
        prevCameraPos.set(camera.getPosition());
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

        program.getUniform("ProjMat").set(projMat);
        var rot = camera.getRotationXY();
        var lPos = camera.getLerpPosition(prevCameraPos, (float) timer.deltaTime);
        // ModelView = View * Model
        // ViewMat
        modelViewMat.rotationX(-rot.x)
            .rotateY(-rot.y)
            .translate(lPos.negate())
            // ModelMat
            .mul(transformation.getMatrix());
        lPos.negate();
        program.getUniform("ModelViewMat").set(modelViewMat);
        program.updateUniforms();

        mesh.render(program);

        program.unbind();
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            mouse.setGrabbed(!mouse.isGrabbed());
        }
    }

    @Override
    public void close() {
        if (assetManager != null) {
            try {
                assetManager.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mesh != null) {
            mesh.close();
        }
        if (program != null) {
            program.close();
        }
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }
}
