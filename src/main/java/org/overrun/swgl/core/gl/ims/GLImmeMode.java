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

package org.overrun.swgl.core.gl.ims;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.overrun.swgl.core.gl.*;
import org.overrun.swgl.core.model.IModel;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * The OpenGL immediate mode simulator.
 * <p>
 * The functions start with 'lgl' that means 'legacy GL'.
 * </p>
 *
 * @author squid233
 * @since 0.1.0
 */
@ApiStatus.Experimental
public class GLImmeMode {
    public static int imsVertexCount = 50000;
    private static final GLProgram pipeline = new GLProgram();
    private static GLDrawMode drawMode;
    static ByteBuffer buffer; /* Package private, for GLLists */
    private static IntBuffer indicesBuffer;
    private static float
        x = 0.0f, y = 0.0f, z = 0.0f, w = 1.0f,
        s = 0.0f, t = 0.0f, p = 0.0f, q = 1.0f;
    private static byte
        r = IModel.color2byte(1.0f), g = IModel.color2byte(1.0f), b = IModel.color2byte(1.0f), a = IModel.color2byte(1.0f),
        nx = 0, ny = 0, nz = IModel.normal2byte(1.0f);
    private static int vao = 0, vbo = 0, ebo = 0;
    private static int vertexCount = 0;
    private static final Matrix4fStack projectionMat = new Matrix4fStack(4);
    private static final Matrix4fStack modelviewMat = new Matrix4fStack(48);
    private static final Matrix4fStack textureMat = new Matrix4fStack(4);
    private static final Matrix4fStack colorMat = new Matrix4fStack(4);
    private static Matrix4fStack currentMat = modelviewMat;
    private static boolean
        vertexArrayState = true,
        normalArrayState = false,
        colorArrayState = true,
        texCoordArrayState = false;
    private static boolean rendering = true;
    static GLList currentList;

    public enum MatrixMode {
        MODELVIEW,
        PROJECTION,
        TEXTURE,
        COLOR
    }

    public static int lglGetByteStride() {
        return (4 * 4 + 4 + 4 * 4 + 3);
    }

    /**
     * Request an immediate mode simulation context.
     */
    public static void lglRequestContext() {
        buffer = memCalloc(imsVertexCount * lglGetByteStride());
        indicesBuffer = memCallocInt(imsVertexCount);
        pipeline.create();
        // Vertex shader
        var vertSrc =
            """
                #version 110
                attribute vec4 in_vertex;
                attribute vec4 in_color;
                attribute vec4 in_tex_coord;
                attribute vec3 in_normal;

                varying vec4 out_color;
                varying vec4 out_tex_coord;
                varying vec3 out_normal;

                uniform mat4 projectionMat;
                uniform mat4 modelviewMat;

                void main() {
                    gl_Position = projectionMat * modelviewMat * in_vertex;
                    out_color = in_color;
                    out_tex_coord = in_tex_coord;
                    out_normal = in_normal;
                }""";
        // Fragment shader BEGIN
        var fragSrc =
            new StringBuilder("""
                #version 110

                varying vec4 out_color;
                varying vec4 out_tex_coord;
                varying vec3 out_normal;
                """);
        final int maxTexUnits = getMaxTexImgUnits();
        for (int i = 0; i < maxTexUnits; i++) {
            fragSrc.append("uniform sampler2D sampler2D_").append(i).append(";\n");
            fragSrc.append("uniform int sampler2D_").append(i).append("_enabled;\n");
        }
        fragSrc.append("""
            void main() {
                vec4 fragColor = out_color;
                """);
        for (int i = 0; i < maxTexUnits; i++) {
            fragSrc.append("    if (sampler2D_").append(i).append("_enabled != 0) {\n");
            fragSrc.append("        fragColor *= texture2D(sampler2D_").append(i).append(", out_tex_coord.st);\n");
            fragSrc.append("    }\n");
        }
        fragSrc.append("    gl_FragColor = fragColor;\n");
        fragSrc.append("}");
        // Fragment shader END
        //todo System.out.println(fragSrc);
        Shaders.linkSimple(pipeline,
            vertSrc,
            fragSrc.toString());
        glBindAttribLocation(pipeline.getId(),
            0,
            "in_vertex");
        glBindAttribLocation(pipeline.getId(),
            1,
            "in_color");
        glBindAttribLocation(pipeline.getId(),
            2,
            "in_tex_coord");
        glBindAttribLocation(pipeline.getId(),
            3,
            "in_normal");
        pipeline.bind();
        for (int i = 0; i < maxTexUnits; i++) {
            pipeline.getUniformSafe("sampler2D_" + i, GLUniformType.I1).set(i);
            pipeline.getUniformSafe("sampler2D_" + i + "_enabled", GLUniformType.I1).set(false);
        }
        pipeline.getUniformSafe("projectionMat", GLUniformType.M4F).set(projectionMat);
        pipeline.getUniformSafe("modelviewMat", GLUniformType.M4F).set(modelviewMat);
        pipeline.updateUniforms();
        pipeline.unbind();
    }

    public static void lglSetVertexArrayState(boolean state) {
        vertexArrayState = state;
    }

    public static void lglSetNormalArrayState(boolean state) {
        normalArrayState = state;
    }

    public static void lglSetColorArrayState(boolean state) {
        colorArrayState = state;
    }

    public static void lglSetTexCoordArrayState(boolean state) {
        texCoordArrayState = state;
    }

    public static void lglSetRendering(boolean rendering) {
        GLImmeMode.rendering = rendering;
    }

    public static boolean lglIsRendering() {
        return rendering;
    }

    public static int lglGetVertexCount() {
        return vertexCount;
    }

    public static GLDrawMode lglGetDrawMode() {
        return drawMode;
    }

    public static void lglBegin(GLDrawMode mode) {
        drawMode = mode;
        buffer.clear();
        indicesBuffer.clear();
        vertexCount = 0;
    }

    public static void lglBuffer(ByteBuffer buf) {
        buffer.put(buf);
    }

    public static void lglColor(byte r, byte g, byte b, byte a) {
        GLImmeMode.r = r;
        GLImmeMode.g = g;
        GLImmeMode.b = b;
        GLImmeMode.a = a;
    }

    public static void lglColor(byte r, byte g, byte b) {
        lglColor(r, g, b, (byte) (255.0f));
    }

    public static void lglColor(float r, float g, float b, float a) {
        GLImmeMode.r = IModel.color2byte(r);
        GLImmeMode.g = IModel.color2byte(g);
        GLImmeMode.b = IModel.color2byte(b);
        GLImmeMode.a = IModel.color2byte(a);
    }

    public static void lglColor(float r, float g, float b) {
        lglColor(r, g, b, 1.0f);
    }

    public static void lglVertex(float x, float y, float z, float w) {
        GLImmeMode.x = x;
        GLImmeMode.y = y;
        GLImmeMode.z = z;
        GLImmeMode.w = w;
    }

    public static void lglVertex(float x, float y, float z) {
        lglVertex(x, y, z, 1.0f);
    }

    public static void lglVertex(float x, float y) {
        lglVertex(x, y, 0.0f);
    }

    public static void lglTexCoord(float s, float t, float r, float q) {
        GLImmeMode.s = s;
        GLImmeMode.t = t;
        GLImmeMode.p = r;
        GLImmeMode.q = q;
    }

    public static void lglTexCoord(float s, float t, float r) {
        lglTexCoord(s, t, r, 1.0f);
    }

    public static void lglTexCoord(float s, float t) {
        lglTexCoord(s, t, 0.0f);
    }

    public static void lglTexCoord(float s) {
        lglTexCoord(s, 0.0f);
    }

    public static void lglNormal(float nx, float ny, float nz) {
        GLImmeMode.nx = IModel.normal2byte(nx);
        GLImmeMode.ny = IModel.normal2byte(ny);
        GLImmeMode.nz = IModel.normal2byte(nz);
    }

    private static void growIB() {
        if (indicesBuffer.capacity() - indicesBuffer.position() < 128)
            indicesBuffer = memRealloc(indicesBuffer, indicesBuffer.capacity() + 256);
    }

    public static void lglIndices(int... indices) {
        growIB();
        for (int i : indices) {
            indicesBuffer.put(vertexCount + i);
        }
    }

    public static void lglEmit() {
        if (rendering) {
            buffer.putFloat(x).putFloat(y).putFloat(z).putFloat(w);
            buffer.put(r).put(g).put(b).put(a);
            buffer.putFloat(s).putFloat(t).putFloat(p).putFloat(q);
            buffer.put(nx).put(ny).put(nz);
        }
        ++vertexCount;
    }

    public static void lglEnd() {
        if (rendering)
            lglEnd0();
    }

    private static void lglEnd0() {
        buffer.flip();
        indicesBuffer.flip();

        pipeline.bind();
        pipeline.getUniformSafe("projectionMat", GLUniformType.M4F).set(projectionMat);
        pipeline.getUniformSafe("modelviewMat", GLUniformType.M4F).set(modelviewMat);
        for (int i = 0, c = getMaxTexImgUnits(); i < c; i++) {
            pipeline.getUniformSafe("sampler2D_" + i + "_enabled", GLUniformType.I1).set(isTexture2dEnabled(i));
        }
        pipeline.updateUniforms();

        if (ENABLE_CORE_PROFILE) {
            if (vao == 0)
                vao = glGenVertexArrays();
            glBindVertexArray(vao);
        }

        final boolean notVbo = vbo == 0;
        if (notVbo)
            vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        if (notVbo)
            nglBufferData(GL_ARRAY_BUFFER, Integer.toUnsignedLong(buffer.capacity()), memAddress(buffer), GL_DYNAMIC_DRAW);
        else
            glBufferSubData(GL_ARRAY_BUFFER, 0L, buffer);

        if (vertexArrayState) glEnableVertexAttribArray(0);
        else glDisableVertexAttribArray(0);
        if (colorArrayState) glEnableVertexAttribArray(1);
        else glDisableVertexAttribArray(1);
        if (texCoordArrayState) glEnableVertexAttribArray(2);
        else glDisableVertexAttribArray(2);
        if (normalArrayState) glEnableVertexAttribArray(3);
        else glDisableVertexAttribArray(3);

        final int stride = lglGetByteStride();
        if (vertexArrayState)
            glVertexAttribPointer(0,
                4,
                GL_FLOAT,
                false,
                stride,
                0L);
        if (colorArrayState)
            glVertexAttribPointer(1,
                4,
                GL_UNSIGNED_BYTE,
                true,
                stride,
                16L);
        if (texCoordArrayState)
            glVertexAttribPointer(2,
                4,
                GL_FLOAT,
                false,
                stride,
                20L);
        if (normalArrayState)
            glVertexAttribPointer(3,
                3,
                GL_BYTE,
                true,
                stride,
                36L);

        if (indicesBuffer.limit() > 0) {
            if (ebo == 0)
                ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_DYNAMIC_DRAW);
            glDrawElements(drawMode.getGlType(), indicesBuffer.limit(), GL_UNSIGNED_INT, 0L);
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glDrawArrays(drawMode.getGlType(), 0, vertexCount);
        }

        if (ENABLE_CORE_PROFILE)
            glBindVertexArray(0);

        pipeline.unbind();

        drawMode = null;
    }

    public static Matrix4fStack lglGetMatrixMode() {
        return currentMat;
    }

    public static void lglMatrixMode(MatrixMode mode) {
        switch (mode) {
            case MODELVIEW -> currentMat = modelviewMat;
            case PROJECTION -> currentMat = projectionMat;
            case TEXTURE -> currentMat = textureMat;
            case COLOR -> currentMat = colorMat;
        }
    }

    public static void lglFrustum(float left,
                                  float right,
                                  float bottom,
                                  float top,
                                  float zNear,
                                  float zFar) {
        currentMat.frustum(left, right, bottom, top, zNear, zFar);
    }

    public static void lglLoadIdentity() {
        currentMat.identity();
    }

    public static void lglLoadMatrix(Matrix4fc m) {
        currentMat.set(m);
    }

    public static void lglLoadMatrix(float[] m) {
        currentMat.set(m);
    }

    public static void lglLoadTransposeMatrix(Matrix4fc m) {
        currentMat.setTransposed(m);
    }

    public static void lglLoadTransposeMatrix(float[] m) {
        currentMat.setTransposed(m);
    }

    public static void lglMultMatrix(Matrix4fc m) {
        currentMat.mul(m);
    }

    public static void lglMultMatrix(float[] m) {
        currentMat.mul(new Matrix4f().set(m));
    }

    public static void lglMultTransposeMatrix(Matrix4fc m) {
        currentMat.mul(m.transpose(new Matrix4f()));
    }

    public static void lglMultTransposeMatrix(float[] m) {
        currentMat.mul(new Matrix4f().set(m).transpose());
    }

    public static void lglOrtho(float left,
                                float right,
                                float bottom,
                                float top,
                                float zNear,
                                float zFar) {
        currentMat.ortho(left, right, bottom, top, zNear, zFar);
    }

    public static void lglOrtho2D(float left,
                                  float right,
                                  float bottom,
                                  float top) {
        currentMat.ortho2D(left, right, bottom, top);
    }

    public static void lglOrthoSymmetric(float width,
                                         float height,
                                         float zNear,
                                         float zFar) {
        currentMat.orthoSymmetric(width, height, zNear, zFar);
    }

    public static void lglPushMatrix() {
        currentMat.pushMatrix();
    }

    public static void lglPopMatrix() {
        currentMat.popMatrix();
    }

    public static void lglRotate(float ang,
                                 float x,
                                 float y,
                                 float z) {
        currentMat.rotate(ang, x, y, z);
    }

    public static void lglRotateDeg(float ang,
                                    float x,
                                    float y,
                                    float z) {
        currentMat.rotate(Math.toRadians(ang), x, y, z);
    }

    public static void lglRotateLocal(float ang,
                                      float x,
                                      float y,
                                      float z) {
        currentMat.rotateLocal(ang, x, y, z);
    }

    public static void lglRotateLocalDeg(float ang,
                                         float x,
                                         float y,
                                         float z) {
        currentMat.rotateLocal(Math.toRadians(ang), x, y, z);
    }

    public static void lglScale(float x,
                                float y,
                                float z) {
        currentMat.scale(x, y, z);
    }

    public static void lglTranslate(float x,
                                    float y,
                                    float z) {
        currentMat.translate(x, y, z);
    }

    public static void lglDestroyContext() {
        pipeline.close();
        if (ENABLE_CORE_PROFILE && glIsVertexArray(vao))
            glDeleteVertexArrays(vao);
        if (glIsBuffer(vbo))
            glDeleteBuffers(vbo);
        if (glIsBuffer(ebo))
            glDeleteBuffers(ebo);
        memFree(buffer);
        memFree(indicesBuffer);
    }
}
