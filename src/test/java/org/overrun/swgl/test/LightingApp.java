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

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.asset.tex.ITextureParam;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.model.VertexFormat;
import org.overrun.swgl.core.model.VertexLayout;
import org.overrun.swgl.core.model.obj.ObjModel;
import org.overrun.swgl.core.model.obj.ObjModels;
import org.overrun.swgl.core.util.timing.Timer;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
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
        app.launch();
    }

    public static final float SENSITIVITY = 0.15f;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static final String WND_TITLE = "Lighting Application";
    private static final Vector3i VERT_ATTRIB_LOC = new Vector3i(0, 1, 2);
    /**
     * So many containers!
     */
    private static final int CONTAINER2_AMOUNT = 1000;
    private static final Vector3f[] POINT_LIGHT_POSITIONS = {
        new Vector3f(0.7f, 0.2f, 2.0f),
        new Vector3f(2.3f, -3.3f, -4.0f),
        new Vector3f(-4.0f, 2.0f, -12.0f),
        new Vector3f(0.0f, 0.0f, -3.0f)
    };
    private static final Vector3f CONTAINER_ROTATE = new Vector3f(1.0f, 0.3f, 0.5f).normalize();
    private GLProgram objectProgram, lightingProgram;
    private ObjModel objectModel;
    private ObjModel lightModel;
    private ObjModel nanoSuitModel;
    private AssetManager assetManager;
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f viewMat = new Matrix4f();
    private final Matrix4f modelMat = new Matrix4f();
    private final Matrix4f normalMat = new Matrix4f();
    private final Matrix4f projViewMat = new Matrix4f();
    private final FpsCamera camera = new FpsCamera();
    private int container2MatVbo;
    private final Vector3f[] cubePositions = new Vector3f[CONTAINER2_AMOUNT];
    private final FloatBuffer modelMatrices = BufferUtils.createFloatBuffer(CONTAINER2_AMOUNT * 16 * 2);
    private final FrustumIntersection frustum = new FrustumIntersection();

    @Override
    public void prepare() {
        GlobalConfig.initialTitle = WND_TITLE;
        GlobalConfig.initialSwapInterval = 0;
        GlobalConfig.requireGlMinorVer = 3;
    }

    @Override
    public void start() {
        enableDebugOutput();
        enableDepthTest();
        enableCullFace();
        setDepthFunc(GL_LEQUAL);
        GLUtil.setupDebugMessageCallback(System.err);
        clearColor(0.1f, 0.1f, 0.1f, 1.0f);
        var resManager = new ResManager(this);

        // Models
        objectModel = ObjModels.loadModel("models/lighting/container2.obj");
        lightModel = ObjModels.loadModel("models/lighting/light.obj");
        try {
            nanoSuitModel = ObjModels.loadModel("models/lighting/nanosuit/nanosuit.obj");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("\nWarning: you should download the package.");
            System.err.println(PlainTextAsset.createStr("models/lighting/nanosuit/README.md", FILE_PROVIDER)
                .replace("[here](https://learnopengl-cn.github.io/data/nanosuit.rar)",
                    "https://learnopengl-cn.github.io/data/nanosuit.rar"));
            System.exit(0);
        }

        var random = new Random();
        for (int i = 0; i < CONTAINER2_AMOUNT; i++) {
            cubePositions[i] = new Vector3f(random.nextFloat(-50.0f, 50.0f),
                random.nextFloat(-1.0f, 1.0f),
                random.nextFloat(-50.0f, 50.0f));
            modelMat.translation(cubePositions[i]);
            float angle = 20.0f * i;
            modelMat.rotate((float) Math.toRadians(angle), CONTAINER_ROTATE);
            modelMat.get(modelMatrices).position(modelMatrices.position() + 16);
            modelMat.invert(normalMat)
                .transpose()
                .set(3, 0, 0)
                .set(3, 1, 0)
                .set(3, 2, 0)
                .get(modelMatrices)
                .position(modelMatrices.position() + 16);
        }
        modelMatrices.flip();
        container2MatVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, container2MatVbo);
        glBufferData(GL_ARRAY_BUFFER, modelMatrices, GL_STATIC_DRAW);
        for (var mesh : objectModel.meshes) {
            mesh.bindVao();

            mesh.setupBuffers(VERT_ATTRIB_LOC);
            glBindBuffer(GL_ARRAY_BUFFER, container2MatVbo);
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3, 4, GL_FLOAT,
                false, 128, 0);
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(4, 4, GL_FLOAT,
                false, 128, 16);
            glEnableVertexAttribArray(5);
            glVertexAttribPointer(5, 4, GL_FLOAT,
                false, 128, 32);
            glEnableVertexAttribArray(6);
            glVertexAttribPointer(6, 4, GL_FLOAT,
                false, 128, 48);

            glEnableVertexAttribArray(7);
            glVertexAttribPointer(7, 4, GL_FLOAT,
                false, 128, 64);
            glEnableVertexAttribArray(8);
            glVertexAttribPointer(8, 4, GL_FLOAT,
                false, 128, 80);
            glEnableVertexAttribArray(9);
            glVertexAttribPointer(9, 4, GL_FLOAT,
                false, 128, 96);
            glEnableVertexAttribArray(10);
            glVertexAttribPointer(10, 4, GL_FLOAT,
                false, 128, 112);

            glVertexAttribDivisor(3, 1);
            glVertexAttribDivisor(4, 1);
            glVertexAttribDivisor(5, 1);
            glVertexAttribDivisor(6, 1);
            glVertexAttribDivisor(7, 1);
            glVertexAttribDivisor(8, 1);
            glVertexAttribDivisor(9, 1);
            glVertexAttribDivisor(10, 1);

            glBindVertexArray(0);
        }

        // GL Programs
        objectProgram = new GLProgram(
            new VertexLayout(
                VertexFormat.V3F,
                VertexFormat.T2F,
                VertexFormat.N3B));
        objectProgram.create();
        var result = Shaders.linkSimple(objectProgram,
            "shaders/lighting/shader.vert",
            "shaders/lighting/object_shader.frag",
            FILE_PROVIDER);
        if (!result)
            throw new RuntimeException("Failed to link the object program. " +
                objectProgram.getInfoLog());
        objectProgram.bind();
        objectProgram.getUniformSafe("material.diffuse", I1).set(0);
        objectProgram.getUniformSafe("material.specular", I1).set(1);

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
            new VertexLayout(VertexFormat.V3F));
        lightingProgram.create();
        result = Shaders.linkSimple(lightingProgram,
            "shaders/lighting/shader.vert",
            "shaders/lighting/light_shader.frag",
            FILE_PROVIDER);
        if (!result)
            throw new RuntimeException("Failed to link the lighting program. " +
                lightingProgram.getInfoLog());

        // Textures
        ITextureParam param = target -> {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        };

        assetManager = new AssetManager();
        for (var model : new ObjModel[]{objectModel, nanoSuitModel})
            for (var mtl : model.materials.values()) {
                for (var map : mtl.ambientMaps) {
                    Texture2D.createAssetParam(assetManager, map, param, FILE_PROVIDER);
                }
                for (var map : mtl.diffuseMaps) {
                    Texture2D.createAssetParam(assetManager, map, param, FILE_PROVIDER);
                }
                for (var map : mtl.specularMaps) {
                    Texture2D.createAssetParam(assetManager, map, param, FILE_PROVIDER);
                }
            }
        assetManager.reloadAssets(true);

        resManager.addResource(objectProgram);
        resManager.addResource(lightingProgram);
        resManager.addResource(objectModel);
        resManager.addResource(lightModel);
        resManager.addResource(nanoSuitModel);
        resManager.addResource(assetManager);

        camera.limitedPitch = true;

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
        frustum.set(projMat.mul(viewMat, projViewMat));

        objectProgram.bind();
        objectProgram.getUniformSafe("ViewPos", F3).set(lPos);
        objectProgram.getUniformSafe("spotLight.position", F3).set(lPos);
        lPos.negate();
        objectProgram.getUniformSafe("spotLight.direction", F3).set(camera.getFrontVec());
        objectProgram.getUniformSafe("HasInstance", I1).set(true);
        setMatrices(objectProgram);
        objectModel.getMaterial(objectModel.meshes.get(0).materialIndex).ifPresent(mtl -> {
            if (!Assimp.AI_DEFAULT_MATERIAL_NAME.equals(mtl.name)) {
                if (mtl.diffuseMaps.length > 0) {
                    activeTexture(0);
                    Texture2D.getAsset(assetManager, mtl.diffuseMaps[0]).ifPresent(Texture2D::bind);
                }
                if (mtl.specularMaps.length > 0) {
                    activeTexture(1);
                    Texture2D.getAsset(assetManager, mtl.specularMaps[0]).ifPresent(Texture2D::bind);
                }
            }
        });
        for (var mesh : objectModel.meshes) {
            mesh.bindVao();
            glDrawElementsInstanced(
                GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0, CONTAINER2_AMOUNT
            );
        }
        modelMat.translation(0.0f, 0.0f, 6.0f)
            .rotateY((float) Math.toRadians(Timer.getTime() * 15))
            .translate(0.0f, 0.0f, -18.0f);
        modelMat.invert(normalMat)
            .transpose()
            .set(3, 0, 0)
            .set(3, 1, 0)
            .set(3, 2, 0);
        objectProgram.getUniformSafe("NormalMat", M4F).set(normalMat);
        objectProgram.getUniformSafe("HasInstance", I1).set(false);
        setMatrices(objectProgram);
        nanoSuitModel.render(VERT_ATTRIB_LOC, mtl -> {
            if (!Assimp.AI_DEFAULT_MATERIAL_NAME.equals(mtl.name)) {
                objectProgram.getUniformSafe("material.shininess", F1).set(mtl.shininess);
                objectProgram.updateUniforms();
                if (mtl.diffuseMaps.length > 0) {
                    activeTexture(0);
                    Texture2D.getAsset(assetManager, mtl.diffuseMaps[0]).ifPresent(Texture2D::bind);
                }
                if (mtl.specularMaps.length > 0) {
                    activeTexture(1);
                    Texture2D.getAsset(assetManager, mtl.specularMaps[0]).ifPresent(Texture2D::bind);
                }
            }
        });
        objectProgram.unbind();

        lightingProgram.bind();
        for (var pos : POINT_LIGHT_POSITIONS) {
            if (!frustum.testAab(pos.x, pos.y, pos.z, pos.x + 1.0f, pos.y + 1.0f, pos.z + 1.0f))
                continue;
            modelMat.translation(pos).scale(0.2f);
            setMatrices(lightingProgram);
            lightModel.render(VERT_ATTRIB_LOC, mtl -> {
            });
        }
        lightingProgram.unbind();
    }

    @Override
    public void settingFrames() {
        window.setTitle(WND_TITLE + " FPS: " + frames);
    }

    @Override
    public void close() {
        glDeleteBuffers(container2MatVbo);
    }
}
