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
import org.joml.*;
import org.joml.Math;
import org.overrun.swgl.core.gl.*;
import org.overrun.swgl.core.model.IModel;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.GLUniformType.*;

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
    static IntBuffer indicesBuffer; /* Package private, for GLLists */
    private static boolean indexBufferExtended = true;
    private static float
        x = 0.0f, y = 0.0f, z = 0.0f, w = 1.0f,
        s = 0.0f, t = 0.0f, p = 0.0f, q = 1.0f;
    private static byte
        r = IModel.color2byte(1.0f), g = IModel.color2byte(1.0f), b = IModel.color2byte(1.0f), a = IModel.color2byte(1.0f),
        nx = 0, ny = 0, nz = IModel.normal2byte(1.0f);
    private static int vao = 0, vbo = 0, ebo = 0;
    private static int vertexCount = 0;

    ///////////////////////////////////////////////////////////////////////////
    // Matrix state
    ///////////////////////////////////////////////////////////////////////////

    private static final Matrix4fStack projectionMat = new Matrix4fStack(4);
    private static final Matrix4fStack modelviewMat = new Matrix4fStack(48);
    private static final Matrix4fStack textureMat = new Matrix4fStack(4);
    private static final Matrix4fStack colorMat = new Matrix4fStack(4);
    private static final Matrix4f normalMat = new Matrix4f();
    private static Matrix4fStack currentMat = modelviewMat;

    private static boolean
        vertexArrayState = true,
        normalArrayState = false,
        colorArrayState = true,
        texCoordArrayState = false;

    ///////////////////////////////////////////////////////////////////////////
    // Lighting
    ///////////////////////////////////////////////////////////////////////////

    private static boolean lighting = false;
    private static boolean colorMaterial = false;
    private static final Vector4f lightModelAmbient = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
    private static final Light[] lights = {
        new Light()
    };
    private static final Material material = new Material();

    private static boolean rendering = true;
    static GLList currentList;

    /**
     * The matrix modes.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum MatrixMode {
        MODELVIEW,
        PROJECTION,
        TEXTURE,
        COLOR
    }

    /**
     * The light source.
     *
     * @author squid233
     * @since 0.1.0
     */
    public static final class Light {
        public boolean enabled = false;
        public final Vector4f position = new Vector4f(0.0f, 0.0f, 1.0f, 0.0f);
        public final Vector4f ambient = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        public final Vector4f diffuse = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        public final Vector4f specular = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * The material.
     *
     * @author squid233
     * @since 0.1.0
     */
    public static final class Material {
        public final Vector4f ambient = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
        public final Vector4f diffuse = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
        public final Vector4f specular = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public static int lglGetByteStride() {
        return (4 * 4 + 4 + 4 * 4 + 3);
    }

    public static boolean lglIsRendering() {
        return rendering;
    }

    public static int lglGetVertexCount() {
        return vertexCount;
    }

    public static int lglGetIndexCount() {
        return indicesBuffer.limit();
    }

    public static GLDrawMode lglGetDrawMode() {
        return drawMode;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Start
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Request an immediate mode simulation (IMS) context.
     *
     * @see #lglDestroyContext()
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
                varying vec3 out_frag_pos;

                uniform mat4 projectionMat;
                uniform mat4 modelviewMat;
                uniform mat4 normalMat;

                void main() {
                    out_frag_pos = vec3(modelviewMat * in_vertex);
                    gl_Position = projectionMat * vec4(out_frag_pos, 1.0);
                    out_color = in_color;
                    out_tex_coord = in_tex_coord;
                    out_normal = vec3(normalMat * vec4(in_normal, 0.0));
                }""";
        // Fragment shader BEGIN
        var fragSrc =
            new StringBuilder("""
                #version 110

                struct Light {
                    int enabled;
                    vec4 position;
                    vec4 ambient;
                    vec4 diffuse;
                    vec4 specular;
                };

                struct Material {
                    vec4 ambient;
                    vec4 diffuse;
                    vec4 specular;
                };

                varying vec4 out_color;
                varying vec4 out_tex_coord;
                varying vec3 out_normal;
                varying vec3 out_frag_pos;

                uniform int HasLighting, HasColorMaterial;
                uniform vec4 lightModelAmbient;
                uniform Material material;
                """)
                .append("#define MAX_LIGHT_SOURCES (").append(lights.length).append(")\n")
                .append("uniform Light lights[MAX_LIGHT_SOURCES];\n");
        final int maxTexUnits = getMaxTexImgUnits();
        for (int i = 0; i < maxTexUnits; i++) {
            fragSrc.append("uniform sampler2D sampler2D_").append(i).append(";\n");
            fragSrc.append("uniform int sampler2D_").append(i).append("_enabled;\n");
        }
        fragSrc.append("""
            bool _isTrue(int b) { return b != 0; }

            vec4 calcDiffuseLight(Light light) {
                vec3 norm = normalize(out_normal);
                vec3 lightPos = light.position.xyz;
                vec3 lightDir;
                if (light.position.w == 0.0) {
                    lightDir = normalize(-lightPos);
                } else if (light.position.w == 1.0) {
                    lightDir = normalize(lightPos - out_frag_pos);
                }
                float diff = max(dot(norm, lightDir), 0.0);
                vec4 diffuse = diff * light.diffuse;
                return diffuse;
            }

            vec4 calcSpecularLight(Light light) {
                return light.specular;
            }

            void main() {
                vec4 fragColor = vec4(1.0);
                if (_isTrue(HasLighting)) {
                    fragColor *= lightModelAmbient.rgb;
                    for (int i = 0; i < MAX_LIGHT_SOURCES; ++i) {
                        Light light = lights[i];
                        if (_isTrue(light.enabled)) {
                            vec3 ambient = (light.position.w == 1.0 ? light.ambient.rgb : vec3(1.0)) * material.ambient.rgb;
                            if (_isTrue(HasColorMaterial)) {
                                ambient *= out_color.rgb;
                            }
                            vec4 diffuse = calcDiffuseLight(light) * material.diffuse;
                            fragColor *= ambient + diffuse;
                        }
                    }
                } else {
                    fragColor *= out_color;
                }
                """);
        for (int i = 0; i < maxTexUnits; i++) {
            fragSrc.append("    if (_isTrue(sampler2D_").append(i).append("_enabled)) {\n");
            fragSrc.append("        fragColor *= texture2D(sampler2D_").append(i).append(", out_tex_coord.st);\n");
            fragSrc.append("    }\n");
        }
        fragSrc.append("    gl_FragColor = fragColor;\n");
        fragSrc.append("}");
        // Fragment shader END
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
            pipeline.getUniformSafe("sampler2D_" + i, I1).set(i);
            pipeline.getUniformSafe("sampler2D_" + i + "_enabled", I1).set(false);
        }
        pipeline.getUniformSafe("projectionMat", M4F).set(projectionMat);
        pipeline.getUniformSafe("modelviewMat", M4F).set(modelviewMat);
        pipeline.getUniformSafe("normalMat", M4F).set(normalMat);
        setLightUniform();
        pipeline.updateUniforms();
        pipeline.unbind();
    }

    private static void setLightUniform() {
        pipeline.getUniformSafe("HasLighting", I1).set(lighting);
        pipeline.getUniformSafe("HasColorMaterial", I1).set(colorMaterial);
        pipeline.getUniformSafe("lightModelAmbient", F4).set(lightModelAmbient);
        {
            var prefix = "material.";
            pipeline.getUniformSafe(prefix + "ambient", F4).set(material.ambient);
            pipeline.getUniformSafe(prefix + "diffuse", F4).set(material.diffuse);
            pipeline.getUniformSafe(prefix + "specular", F4).set(material.specular);
        }
        for (int i = 0; i < lights.length; i++) {
            var prefix = "lights[" + i + "].";
            pipeline.getUniformSafe(prefix + "enabled", I1).set(lights[i].enabled);
            pipeline.getUniformSafe(prefix + "position", F4).set(lights[i].position);
            pipeline.getUniformSafe(prefix + "ambient", F4).set(lights[i].ambient);
            pipeline.getUniformSafe(prefix + "diffuse", F4).set(lights[i].diffuse);
            pipeline.getUniformSafe(prefix + "specular", F4).set(lights[i].specular);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Client arrays
    ///////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////
    // State management
    ///////////////////////////////////////////////////////////////////////////

    public static void lglEnableLighting() {
        lighting = true;
    }

    public static void lglDisableLighting() {
        lighting = false;
    }

    public static void lglEnableColorMaterial() {
        colorMaterial = true;
    }

    public static void lglDisableColorMaterial() {
        colorMaterial = false;
    }

    public static void lglSetLightModelAmbient(Vector4fc value) {
        lightModelAmbient.set(value);
    }

    public static void lglSetLightModelAmbient(float[] value) {
        lightModelAmbient.set(value);
    }

    public static void lglSetLightModelAmbient(float r, float g, float b, float a) {
        lightModelAmbient.set(r, g, b, a);
    }

    public static Light lglGetLight(int light) {
        return lights[light];
    }

    ///////////////////////////////////////////////////////////////////////////
    // Immediate mode
    ///////////////////////////////////////////////////////////////////////////

    public static void lglBegin(GLDrawMode mode) {
        drawMode = mode;
        buffer.clear();
        indicesBuffer.clear();
        vertexCount = 0;
    }

    public static void lglBuffer(ByteBuffer buf) {
        buffer.put(buf);
    }

    public static void lglIndexBuffer(IntBuffer buf) {
        indicesBuffer.put(buf);
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

    public static void lglVertex(Vector4fc pos) {
        lglVertex(pos.x(), pos.y(), pos.z(), pos.w());
    }

    public static void lglVertex(Vector3fc pos) {
        lglVertex(pos.x(), pos.y(), pos.z());
    }

    public static void lglVertex(Vector2fc pos) {
        lglVertex(pos.x(), pos.y());
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
        if (indicesBuffer.capacity() - indicesBuffer.position() < 128) {
            indicesBuffer = memRealloc(indicesBuffer, indicesBuffer.capacity() + 256);
            indexBufferExtended = true;
        }
    }

    public static void lglIndices(int... indices) {
        growIB();
        for (int i : indices) {
            indicesBuffer.put(vertexCount + i);
        }
    }

    public static void lglEmit() {
        buffer.putFloat(x).putFloat(y).putFloat(z).putFloat(w);
        buffer.put(r).put(g).put(b).put(a);
        buffer.putFloat(s).putFloat(t).putFloat(p).putFloat(q);
        buffer.put(nx).put(ny).put(nz);
        ++vertexCount;
    }

    public static void lglEnd() {
        buffer.flip();
        indicesBuffer.flip();

        if (rendering)
            lglEnd0();
    }

    private static void prepareDraw() {
        pipeline.bind();
        pipeline.getUniformSafe("projectionMat", M4F).set(projectionMat);
        pipeline.getUniformSafe("modelviewMat", M4F).set(modelviewMat);
        pipeline.getUniformSafe("normalMat", M4F).set(normalMat.set(modelviewMat).invert().transpose().m30(0.0f).m31(0.0f).m32(0.0f));
        setLightUniform();
        for (int i = 0, c = getMaxTexImgUnits(); i < c; i++) {
            pipeline.getUniformSafe("sampler2D_" + i + "_enabled", I1).set(isTexture2dEnabled(i));
        }
        pipeline.updateUniforms();

        if (ENABLE_CORE_PROFILE) {
            if (vao == 0)
                vao = glGenVertexArrays();
            glBindVertexArray(vao);
        }
    }

    private static void postDraw() {
        if (ENABLE_CORE_PROFILE)
            glBindVertexArray(0);

        pipeline.unbind();
    }

    private static void prepareVA() {
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
    }

    private static void lglEnd0() {
        prepareDraw();

        final boolean notVbo = vbo == 0;
        if (notVbo)
            vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        if (notVbo)
            nglBufferData(GL_ARRAY_BUFFER, Integer.toUnsignedLong(buffer.capacity()), memAddress(buffer), GL_DYNAMIC_DRAW);
        else
            glBufferSubData(GL_ARRAY_BUFFER, 0L, buffer);

        prepareVA();

        if (lglGetIndexCount() > 0) {
            if (ebo == 0)
                ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            if (indexBufferExtended) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_DYNAMIC_DRAW);
            } else {
                nglBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0L, Integer.toUnsignedLong(indicesBuffer.limit()) << 2L, memAddress(indicesBuffer));
            }
            indexBufferExtended = false;
            glDrawElements(drawMode.getGlType(), lglGetIndexCount(), GL_UNSIGNED_INT, 0L);
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glDrawArrays(drawMode.getGlType(), 0, lglGetVertexCount());
        }

        postDraw();

        drawMode = null;
    }

    public static void lglDrawBuffers(GLDrawMode mode,
                                      int vertexCount, int indexCount,
                                      int vbo, int ebo) {
        prepareDraw();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        prepareVA();

        if (indexCount > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glDrawElements(mode.getGlType(), indexCount, GL_UNSIGNED_INT, 0L);
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glDrawArrays(mode.getGlType(), 0, vertexCount);
        }

        postDraw();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Matrix state
    ///////////////////////////////////////////////////////////////////////////

    public static Matrix4fStack lglGetMatrix(MatrixMode mode) {
        return switch (mode) {
            case MODELVIEW -> modelviewMat;
            case PROJECTION -> projectionMat;
            case TEXTURE -> textureMat;
            case COLOR -> colorMat;
        };
    }

    public static Matrix4fStack lglGetMatrixMode() {
        return currentMat;
    }

    public static void lglMatrixMode(MatrixMode mode) {
        currentMat = lglGetMatrix(mode);
    }

    public static void lglPerspective(float fovy,
                                      float aspect,
                                      float zNear,
                                      float zFar) {
        currentMat.perspective(fovy, aspect, zNear, zFar);
    }

    public static void lglPerspectiveDeg(float fovy,
                                         float aspect,
                                         float zNear,
                                         float zFar) {
        lglPerspective(Math.toRadians(fovy), aspect, zNear, zFar);
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
        lglRotate(Math.toRadians(ang), x, y, z);
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
        lglRotateLocal(Math.toRadians(ang), x, y, z);
    }

    public static void lglRotateXYZ(float angleX,
                                    float angleY,
                                    float angleZ) {
        currentMat.rotateXYZ(angleX, angleY, angleZ);
    }

    public static void lglRotateXYZDeg(float angleX,
                                       float angleY,
                                       float angleZ) {
        lglRotateXYZ(Math.toRadians(angleX), Math.toRadians(angleY), Math.toRadians(angleZ));
    }

    public static void lglScale(float xyz) {
        currentMat.scale(xyz);
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

    public static void lglTranslate(Vector3fc offset) {
        currentMat.translate(offset);
    }

    ///////////////////////////////////////////////////////////////////////////
    // End
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Destroy an IMS context.
     *
     * @see #lglRequestContext()
     */
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
