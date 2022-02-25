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
import org.joml.Vector3f;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.model.MappedVertexLayout;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.model.obj.ObjModel;
import org.overrun.swgl.core.model.obj.ObjModels;
import org.overrun.swgl.core.util.IntTri;
import org.overrun.swgl.core.util.Pair;

import java.util.Objects;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.GLUniformType.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class LightingApp extends GlfwApplication {
    public static void main(String[] args) {
        var app = new LightingApp();
        app.boot();
    }

    public static final float SENSITIVITY = 0.15f;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.of(LightingApp.class);
    private static final Vector3f[] CUBE_POSITIONS = {
        new Vector3f(0.0f, 0.0f, 0.0f),
        new Vector3f(2.0f, 5.0f, -15.0f),
        new Vector3f(-1.5f, -2.2f, -2.5f),
        new Vector3f(-3.8f, -2.0f, -12.3f),
        new Vector3f(2.4f, -0.4f, -3.5f),
        new Vector3f(-1.7f, 3.0f, -7.5f),
        new Vector3f(1.3f, -2.0f, -2.5f),
        new Vector3f(1.5f, 2.0f, -2.5f),
        new Vector3f(1.5f, 0.2f, -1.5f),
        new Vector3f(-1.3f, 1.0f, -1.5f)
    };
    private static final Vector3f[] POINT_LIGHT_POSITIONS = {
        new Vector3f(0.7f, 0.2f, 2.0f),
        new Vector3f(2.3f, -3.3f, -4.0f),
        new Vector3f(-4.0f, 2.0f, -12.0f),
        new Vector3f(0.0f, 0.0f, -3.0f)
    };
    private static final Vector3f CONTAINER_ROTATE = new Vector3f(1.0f, 0.3f, 0.5f).normalize();
    private final ResManager resManager = new ResManager();
    private GLProgram objectProgram, lightingProgram;
    private ObjModel objectModel;
    private ObjModel lightModel;
    private ObjModel nanoSuitModel;
    private AssetManager assetManager;
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f viewMat = new Matrix4f();
    private final Matrix4f modelMat = new Matrix4f();
    private final Matrix4f normalMat = new Matrix4f();
    private final FpsCamera camera = new FpsCamera();

    @Override
    public void prepare() {
        GLFWErrorCallback.createPrint(System.err).set();
        GlobalConfig.initialTitle = "Lighting Application";
        GlobalConfig.initialSwapInterval = 0;
    }

    @Override
    public void start() {
        enableDebugOutput();
        enableDepthTest();
        enableCullFace();
        setDepthFunc(GL_LEQUAL);
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.1f, 0.1f, 0.1f, 1.0f);
        addResManager(resManager);

        // Models
        objectModel = ObjModels.loadModel("models/lighting/container2.obj");
        lightModel = ObjModels.loadModel("models/lighting/light.obj");
        nanoSuitModel = ObjModels.loadModel("models/lighting/nanosuit/nanosuit.obj");

        // GL Programs
        objectProgram = new GLProgram(
            new MappedVertexLayout(
                Pair.of("Position", VertexFormat.POSITION_FMT),
                Pair.of("UV2", VertexFormat.TEXTURE_FMT),
                Pair.of("Normal", VertexFormat.NORMAL_FMT))
                .hasPosition(true)
                .hasTexture(true)
                .hasNormal(true));
        objectProgram.create();
        var result = Shaders.linkSimple(objectProgram,
            PlainTextAsset.createStr("shaders/lighting/shader.vert", FILE_PROVIDER),
            PlainTextAsset.createStr("shaders/lighting/object_shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the object program. " +
                objectProgram.getInfoLog());
        objectProgram.bind();
        objectProgram.getUniformSafe("material.diffuse", I1).set(0);
        objectProgram.getUniformSafe("material.specular", I1).set(1);
        objectProgram.getUniformSafe("material.shininess", F1).set(objectModel.getMaterial("container2").orElseThrow().shininess);

        objectProgram.getUniformSafe("dirLight.direction", F3).set(-0.2f, -1.0f, -0.3f);
        objectProgram.getUniformSafe("dirLight.ambient", F3).set(0.2f, 0.2f, 0.2f);
        objectProgram.getUniformSafe("dirLight.diffuse", F3).set(0.5f, 0.5f, 0.5f);
        objectProgram.getUniformSafe("dirLight.specular", F3).set(1.0f, 1.0f, 1.0f);

        for (int i = 0; i < POINT_LIGHT_POSITIONS.length; i++) {
            var prefix = "pointLights[" + i + "].";
            objectProgram.getUniformSafe(prefix + "position", F3).set(POINT_LIGHT_POSITIONS[i]);
            // MaxDistance = 50
            objectProgram.getUniformSafe(prefix + "constant", F1).set(1.0f);
            objectProgram.getUniformSafe(prefix + "linear", F1).set(0.09f);
            objectProgram.getUniformSafe(prefix + "quadratic", F1).set(0.032f);
            objectProgram.getUniformSafe(prefix + "ambient", F3).set(0.2f, 0.2f, 0.2f);
            objectProgram.getUniformSafe(prefix + "diffuse", F3).set(0.5f, 0.5f, 0.5f);
            objectProgram.getUniformSafe(prefix + "specular", F3).set(1.0f, 1.0f, 1.0f);
        }

        objectProgram.getUniformSafe("spotLight.cutOff", F1).set((float) Math.cos(Math.toRadians(12.5f)));
        objectProgram.getUniformSafe("spotLight.outerCutOff", F1).set((float) Math.cos(Math.toRadians(17.5f)));
        objectProgram.getUniformSafe("spotLight.ambient", F3).set(0.2f, 0.2f, 0.2f);
        objectProgram.getUniformSafe("spotLight.diffuse", F3).set(0.5f, 0.5f, 0.5f);
        objectProgram.getUniformSafe("spotLight.specular", F3).set(1.0f, 1.0f, 1.0f);
        // MaxDistance = 50
        objectProgram.getUniformSafe("spotLight.constant", F1).set(1.0f);
        objectProgram.getUniformSafe("spotLight.linear", F1).set(0.09f);
        objectProgram.getUniformSafe("spotLight.quadratic", F1).set(0.032f);

        objectProgram.updateUniforms();
        objectProgram.unbind();

        lightingProgram = new GLProgram(
            new MappedVertexLayout(Pair.of("Position", VertexFormat.POSITION_FMT))
                .hasPosition(true));
        lightingProgram.create();
        result = Shaders.linkSimple(lightingProgram,
            PlainTextAsset.createStr("shaders/lighting/shader.vert", FILE_PROVIDER),
            PlainTextAsset.createStr("shaders/lighting/light_shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the lighting program. " +
                lightingProgram.getInfoLog());

        // Textures
        Consumer<Texture2D> consumer = tex -> tex.recordTexParam(() -> {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        });

        assetManager = new AssetManager();
        for (var model : new ObjModel[]{objectModel, nanoSuitModel})
            for (var mtl : model.materials.values()) {
                for (var map : mtl.ambientMaps) {
                    assetManager.createAsset(map, Texture2D.class, consumer, FILE_PROVIDER);
                }
                for (var map : mtl.diffuseMaps) {
                    assetManager.createAsset(map, Texture2D.class, consumer, FILE_PROVIDER);
                }
                for (var map : mtl.specularMaps) {
                    assetManager.createAsset(map, Texture2D.class, consumer, FILE_PROVIDER);
                }
            }
        assetManager.reloadAssets(true);

        resManager.addResource(objectProgram);
        resManager.addResource(lightingProgram);
        resManager.addResource(objectModel);
        resManager.addResource(lightModel);
        resManager.addResource(nanoSuitModel);
        resManager.addResource(assetManager);

        camera.restrictPitch = true;

        mouse.setGrabbed(true);
    }

    @Override
    public void onResize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onCursorPos(double x, double y, double xd, double yd) {
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_RIGHT) || mouse.isGrabbed()) {
            camera.rotate((float) Math.toRadians(xd * SENSITIVITY),
                (float) -Math.toRadians(yd * SENSITIVITY));
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
        if (keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL))
            speed += 0.2f;
        camera.moveRelative(xa, ya, za, speed);
    }

    private void setMatrices(GLProgram program) {
        program.getUniformSafe("ProjMat", M4F).set(projMat);
        program.getUniformSafe("ViewMat", M4F).set(viewMat);
        program.getUniformSafe("ModelMat", M4F).set(modelMat);
        program.updateUniforms();
    }

    @Override
    public void run() {
        final var locations = new IntTri(0, 1, 2);
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);

        // fovy = toRadians(90)
        projMat.setPerspective(1.5707963267948966f,
            (float) window.getWidth() / (float) window.getHeight(),
            0.01f,
            100.0f);
        var lPos = camera.getLerpPosition().negate();
        camera.smoothStep = (float) timer.deltaTime;
        viewMat.set(camera.getMatrix());
        modelMat.identity();

        objectProgram.bind();
        objectProgram.getUniformSafe("ViewPos", F3).set(lPos);
        objectProgram.getUniformSafe("spotLight.position", F3).set(lPos);
        lPos.negate();
        objectProgram.getUniformSafe("spotLight.direction", F3).set(camera.getFrontVec());
        for (int i = 0; i < CUBE_POSITIONS.length; i++) {
            modelMat.translation(CUBE_POSITIONS[i]);
            float angle = 20.0f * i;
            modelMat.rotate((float) Math.toRadians(angle), CONTAINER_ROTATE);
            modelMat.invert(normalMat)
                .transpose()
                .set(3, 0, 0)
                .set(3, 1, 0)
                .set(3, 2, 0);
            objectProgram.getUniformSafe("NormalMat", M4F).set(normalMat);
            setMatrices(objectProgram);
            objectModel.render(locations, mtl -> {
                if (!Assimp.AI_DEFAULT_MATERIAL_NAME.equals(mtl.name)) {
                    if (mtl.diffuseMaps.length > 0) {
                        activeTexture(0);
                        assetManager.getAsset(mtl.diffuseMaps[0], Texture2D.class).bind();
                    }
                    if (mtl.specularMaps.length > 0) {
                        activeTexture(1);
                        assetManager.getAsset(mtl.specularMaps[0], Texture2D.class).bind();
                    }
                }
            });
        }
        modelMat.identity();
        normalMat.identity();
        objectProgram.getUniformSafe("NormalMat", M4F).set(normalMat);
        setMatrices(objectProgram);
        nanoSuitModel.render(locations, mtl -> {
            if (!Assimp.AI_DEFAULT_MATERIAL_NAME.equals(mtl.name)) {
                if (mtl.diffuseMaps.length > 0) {
                    activeTexture(0);
                    assetManager.getAsset(mtl.diffuseMaps[0], Texture2D.class).bind();
                }
                if (mtl.specularMaps.length > 0) {
                    activeTexture(1);
                    assetManager.getAsset(mtl.specularMaps[0], Texture2D.class).bind();
                }
            }
        });
        objectProgram.unbind();

        lightingProgram.bind();
        for (var pos : POINT_LIGHT_POSITIONS) {
            modelMat.translation(pos).scale(0.2f);
            setMatrices(lightingProgram);
            lightModel.render(locations, mtl -> {
            });
        }
        lightingProgram.unbind();
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
