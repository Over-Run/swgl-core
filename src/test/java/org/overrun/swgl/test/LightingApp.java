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
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.Shaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.level.FpsCamera;
import org.overrun.swgl.core.mesh.Geometry;
import org.overrun.swgl.core.mesh.MappedVertexLayout;
import org.overrun.swgl.core.mesh.Mesh;
import org.overrun.swgl.core.mesh.VertexFormat;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_LEQUAL;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.GLStateMgr.setDepthFunc;

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
    private static final Vector3f LIGHT_POS = new Vector3f(1.2f, 1.0f, 2.0f);
    private GLProgram objectProgram, lightingProgram;
    private Mesh meshObject, meshLight;
    private final Matrix4f projMat = new Matrix4f();
    private final Matrix4f viewMat = new Matrix4f();
    private final Matrix4f modelMat = new Matrix4f();
    private final FpsCamera camera = new FpsCamera();
    private final Vector3f prevCameraPos = new Vector3f(camera.getPosition());

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

        var layout =
            new MappedVertexLayout("Position", VertexFormat.POSITION_FMT)
                .hasPosition(true);
        objectProgram = new GLProgram(layout);
        objectProgram.create();
        var result = Shaders.linkSimple(objectProgram,
            PlainTextAsset.createStr("shaders/lighting/shader.vert", FILE_PROVIDER),
            PlainTextAsset.createStr("shaders/lighting/object_shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the object program. " +
                objectProgram.getInfoLog());
        objectProgram.createUniform("ProjMat", GLUniformType.M4F);
        objectProgram.createUniform("ViewMat", GLUniformType.M4F);
        objectProgram.createUniform("ModelMat", GLUniformType.M4F);
        objectProgram.createUniform("ObjectColor", GLUniformType.F3);
        objectProgram.createUniform("LightColor", GLUniformType.F3);
        objectProgram.bind();
        objectProgram.getUniform("ObjectColor").set(1.0f, 0.5f, 0.31f);
        objectProgram.getUniform("LightColor").set(1.0f, 1.0f, 1.0f);
        objectProgram.updateUniforms();
        objectProgram.unbind();

        lightingProgram = new GLProgram(layout);
        lightingProgram.create();
        result = Shaders.linkSimple(lightingProgram,
            PlainTextAsset.createStr("shaders/lighting/shader.vert", FILE_PROVIDER),
            PlainTextAsset.createStr("shaders/lighting/light_shader.frag", FILE_PROVIDER));
        if (!result)
            throw new RuntimeException("Failed to link the lighting program. " +
                lightingProgram.getInfoLog());
        lightingProgram.createUniform("ProjMat", GLUniformType.M4F);
        lightingProgram.createUniform("ViewMat", GLUniformType.M4F);
        lightingProgram.createUniform("ModelMat", GLUniformType.M4F);

        Vector3f[] positions = {
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
        };
        meshObject = Geometry.generateQuads(24,
            layout,
            positions,
            null,
            null,
            null);
        meshLight = Geometry.generateQuads(24,
            layout,
            positions,
            null,
            null,
            null);

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
            camera.rotate((float) -Math.toRadians(xd) * SENSITIVITY,
                (float) -Math.toRadians(yd) * SENSITIVITY);
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
        prevCameraPos.set(camera.getPosition());
        camera.moveRelative(xa * speed, ya * speed, za * speed);
    }

    private void setMatrices(GLProgram program) {
        program.getUniform("ProjMat").set(projMat);
        program.getUniform("ViewMat").set(viewMat);
        program.getUniform("ModelMat").set(modelMat);
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
        var rot = camera.getRotationXY();
        var lPos = camera.getLerpPosition(prevCameraPos, (float) timer.deltaTime);
        viewMat.rotationX(-rot.x)
            .rotateY(-rot.y)
            .translate(lPos.negate());
        modelMat.identity();

        objectProgram.bind();
        setMatrices(objectProgram);
        meshObject.render(objectProgram);
        objectProgram.unbind();

        modelMat.translation(LIGHT_POS).scale(0.2f);
        lightingProgram.bind();
        setMatrices(lightingProgram);
        meshLight.render(lightingProgram);
        lightingProgram.unbind();
    }

    @Override
    public void close() {
        if (meshObject != null) {
            meshObject.close();
        }
        if (objectProgram != null) {
            objectProgram.close();
        }
        if (lightingProgram != null) {
            lightingProgram.close();
        }
    }

    @Override
    public void postClose() {
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
