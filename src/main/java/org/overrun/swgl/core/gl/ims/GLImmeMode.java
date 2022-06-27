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

import org.joml.*;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.batch.GLBatch;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.model.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;
import static org.overrun.swgl.core.gl.GLStateMgr.isTexture2dEnabled;
import static org.overrun.swgl.core.gl.GLUniformType.*;
import static org.overrun.swgl.core.model.VertexFormat.*;

/**
 * The OpenGL immediate mode simulator.
 * <p>
 * The functions start with 'lgl' that means 'legacy GL'.
 * </p>
 *
 * @author squid233
 * @since 0.1.0
 * @deprecated the IMS is deprecated to use; you should use moderner methods.
 */
@Deprecated(since = "0.2.0", forRemoval = true)
public class GLImmeMode {
    public static int imsVertexCount = 50000;
    private static GLProgram pipeline;
    private static GLDrawMode drawMode;
    static GLBatch batch; /* Package private, for GLLists */
    private static int prevVtc = 0, prevIxc = 0;
    private static int vao = 0, vbo = 0, ebo = 0;
    private static VertexLayout layout;

    ///////////////////////////////////////////////////////////////////////////
    // Matrix state
    ///////////////////////////////////////////////////////////////////////////

    public static Matrix4f projectionMat;
    public static Matrix4f viewMat;
    public static Matrix4f modelMat;

    private static boolean
        vertexArrayState = true,
        normalArrayState = false,
        colorArrayState = true,
        texCoordArrayState = false;

    ///////////////////////////////////////////////////////////////////////////
    // Lighting
    ///////////////////////////////////////////////////////////////////////////

    private static boolean lighting = false;
    private static final Vector4f lightModelAmbient = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);

    ///////////////////////////////////////////////////////////////////////////
    // Alpha test
    ///////////////////////////////////////////////////////////////////////////
    private static boolean alphaTest = false;

    private static boolean rendering = true;
    static GLList currentList;

    /**
     * The matrix modes.
     *
     * @author squid233
     * @since 0.1.0
     */
    public enum MatrixMode {
        VIEW,
        MODEL,
        PROJECTION
    }

    public static VertexLayout lglGetLayout() {
        return layout;
    }

    public static int lglGetByteStride() {
        return layout.getStride();
    }

    public static boolean lglIsRendering() {
        return rendering;
    }

    public static int lglGetVertexCount() {
        return batch.getVertexCount();
    }

    public static int lglGetIndexCount() {
        return batch.getIndexCount();
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
        layout = new VertexLayout(
            V3F,
            C4UB,
            T2F,
            N3B
        );
        batch = new GLBatch();
        pipeline = new GLProgram(layout);
        pipeline.create();
        // Vertex shader
        var vertSrc =
            """
                #version 330 core
                layout (location = 0) in vec3 in_vertex;
                layout (location = 1) in vec4 in_color;
                layout (location = 2) in vec2 in_tex_coord;
                layout (location = 3) in vec3 in_normal;

                out vec4 out_color;
                out vec2 out_tex_coord;

                uniform mat4 projectionMat;
                uniform mat4 viewMat;
                uniform mat4 modelMat;

                void main() {
                    gl_Position = projectionMat * viewMat * modelMat * vec4(in_vertex, 1.0);
                    out_color = in_color;
                    out_tex_coord = in_tex_coord;
                }""";
        // Fragment shader BEGIN
        var fragSrc = """
            #version 330 core

            in vec4 out_color;
            in vec2 out_tex_coord;

            out vec4 FragColor;

            uniform int HasAlphaTest;
            uniform bool HasLighting;
            uniform vec4 lightModelAmbient;
            uniform sampler2D textureSampler;
            uniform int sampler2D_enabled;
            void main() {
                vec4 fragColor = (texture2D(textureSampler, out_tex_coord) - 1) * sampler2D_enabled + 1;
                if (HasLighting) {
                    fragColor *= lightModelAmbient.rgb;
                } else {
                    fragColor *= out_color;
                }
                if (fragColor.a < (0.1 * HasAlphaTest))
                    discard;
                FragColor = fragColor;
            }""";
        // Fragment shader END
        GLShaders.linkSimple(pipeline,
            vertSrc,
            fragSrc);
        pipeline.bind();
        pipeline.createUniform("textureSampler", I1).set(0);
        pipeline.createUniform("sampler2D_enabled", I1).set(false);
        pipeline.createUniform("projectionMat", M4F);
        pipeline.createUniform("viewMat", M4F);
        pipeline.createUniform("modelMat", M4F);
        setAlphaTestUniform(true);
        setLightUniform(true);
        pipeline.updateUniforms();
        pipeline.unbind();
    }

    private static void setAlphaTestUniform(boolean prep) {
        if (prep) {
            pipeline.createUniform("HasAlphaTest", I1).set(alphaTest);
            return;
        }
        pipeline.getUniform("HasAlphaTest").set(alphaTest);
    }

    private static void setLightUniform(boolean prep) {
        if (prep) {
            pipeline.createUniform("HasLighting", I1).set(lighting);
            pipeline.createUniform("lightModelAmbient", F4).set(lightModelAmbient);
            return;
        }
        pipeline.getUniform("HasLighting").set(lighting);
        if (!lighting)
            return;
        pipeline.getUniform("lightModelAmbient").set(lightModelAmbient);
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

    public static void lglEnableAlphaTest() {
        alphaTest = true;
    }

    public static void lglDisableAlphaTest() {
        alphaTest = false;
    }

    public static void lglEnableLighting() {
        lighting = true;
    }

    public static void lglDisableLighting() {
        lighting = false;
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

    ///////////////////////////////////////////////////////////////////////////
    // Immediate mode
    ///////////////////////////////////////////////////////////////////////////

    public static void lglBegin(GLDrawMode mode) {
        drawMode = mode;
        batch.begin(layout, imsVertexCount);
    }

    public static void lglBuffer(ByteBuffer buf) {
        batch.buffer(buf);
    }

    public static void lglIndexBuffer(IntBuffer buf) {
        batch.indexBuffer(buf);
    }

    public static void lglColor(byte r, byte g, byte b, byte a) {
        batch.color(r, g, b, a);
    }

    public static void lglColor(byte r, byte g, byte b) {
        lglColor(r, g, b, (byte) (-1));
    }

    public static void lglColor(float r, float g, float b, float a) {
        batch.color(r, g, b, a);
    }

    public static void lglColor(float r, float g, float b) {
        lglColor(r, g, b, 1.0f);
    }

    public static void lglVertex(float x, float y, float z) {
        batch.vertex(x, y, z);
    }

    public static void lglVertex(float x, float y) {
        lglVertex(x, y, 0.0f);
    }

    public static void lglVertex(Vector3fc pos) {
        lglVertex(pos.x(), pos.y(), pos.z());
    }

    public static void lglVertex(Vector2fc pos) {
        lglVertex(pos.x(), pos.y());
    }

    public static void lglTexCoord(float s, float t) {
        batch.texCoord(s, t);
    }

    public static void lglTexCoord(float s) {
        lglTexCoord(s, 0.0f);
    }

    public static void lglNormal(float nx, float ny, float nz) {
        batch.normal(nx, ny, nz);
    }

    private static void lglIndices0(boolean before, int... indices) {
        if (before) batch.indexBefore(indices);
        else batch.indexAfter(indices);
    }

    public static void lglIndices(int... indices)
        throws UnsupportedOperationException {
        if (drawMode == GLDrawMode.QUADS)
            throw new UnsupportedOperationException("Unsupported to use lglIndices in QUADS drawing!");
        lglIndices0(true, indices);
    }

    public static void lglEmit() {
        batch.emit();
        if (drawMode == GLDrawMode.QUADS
            && (batch.getVertexCount() & 3) == 0) {
            lglIndices0(false, 0, 1, 2, 2, 3, 0);
        }
    }

    public static void lglEnd() {
        batch.end();

        if (rendering)
            lglEnd0();
    }

    private static void prepareDraw() {
        pipeline.bind();
        pipeline.getUniform("projectionMat").set(projectionMat);
        pipeline.getUniform("viewMat").set(viewMat);
        pipeline.getUniform("modelMat").set(modelMat);
        setAlphaTestUniform(false);
        setLightUniform(false);
        pipeline.getUniform("sampler2D_enabled").set(texCoordArrayState && isTexture2dEnabled(0));
        pipeline.updateUniforms();

        if (vao == 0)
            vao = glGenVertexArrays();
        glBindVertexArray(vao);
    }

    private static void postDraw() {
        glBindVertexArray(0);

        pipeline.unbind();
    }

    private static void prepareVA(final VertexLayout layout,
                                  final int stride) {
        if (vertexArrayState) {
            if (layout.hasPosition())
                glVertexAttribPointer(0,
                    3,
                    GL_FLOAT,
                    false,
                    stride,
                    layout.getOffset(V3F));
            glEnableVertexAttribArray(0);
        } else glDisableVertexAttribArray(0);
        if (colorArrayState) {
            if (layout.hasColor())
                glVertexAttribPointer(1,
                    4,
                    GL_UNSIGNED_BYTE,
                    true,
                    stride,
                    layout.getOffset(C4UB));
            glEnableVertexAttribArray(1);
        } else glDisableVertexAttribArray(1);
        if (texCoordArrayState) {
            if (layout.hasTexture())
                glVertexAttribPointer(2,
                    2,
                    GL_FLOAT,
                    false,
                    stride,
                    layout.getOffset(T2F));
            glEnableVertexAttribArray(2);
        } else glDisableVertexAttribArray(2);
        if (normalArrayState) {
            if (layout.hasNormal())
                glVertexAttribPointer(3,
                    3,
                    GL_BYTE,
                    true,
                    stride,
                    layout.getOffset(N3B));
            glEnableVertexAttribArray(3);
        } else glDisableVertexAttribArray(3);

    }

    private static void prepareVA(final int stride) {
        prepareVA(lglGetLayout(), stride);
    }

    private static void prepareVA() {
        prepareVA(lglGetByteStride());
    }

    private static void lglEnd0() {
        prepareDraw();

        int vtc = batch.getVertexCount();
        final int ic = lglGetIndexCount();

        if (vbo == 0)
            vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        var buffer = batch.getBuffer();
        if (vtc > prevVtc)
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
        else
            glBufferSubData(GL_ARRAY_BUFFER, 0L, buffer);

        prepareVA();

        if (ic > 0) {
            if (ebo == 0)
                ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            var ib = batch.getIndexBuffer().orElseThrow();
            if (ic > prevIxc) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_DYNAMIC_DRAW);
            } else {
                glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0L, ib);
            }
            glDrawElements(drawMode.getGlType(), ic, GL_UNSIGNED_INT, 0L);
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glDrawArrays(drawMode.getGlType(), 0, lglGetVertexCount());
        }

        prevVtc = vtc;
        prevIxc = ic;

        postDraw();

        drawMode = null;
    }

    public static void lglDrawBuffers(GLDrawMode mode,
                                      int vertexCount, int indexCount,
                                      int vbo, int ebo) {
        lglDrawBuffers(mode, vertexCount, indexCount, vbo, ebo, 0);
    }

    public static void lglDrawBuffers(GLDrawMode mode,
                                      int vertexCount, int indexCount,
                                      int vbo, int ebo,
                                      int stride) {
        lglDrawBuffers(mode, vertexCount, indexCount, vbo, ebo, null, stride);
    }

    public static void lglDrawBuffers(GLDrawMode mode,
                                      int vertexCount, int indexCount,
                                      int vbo, int ebo,
                                      VertexLayout layout,
                                      int stride) {
        prepareDraw();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        if (layout != null) prepareVA(layout, stride < 0 ? lglGetByteStride() : stride);
        else if (stride > 0) prepareVA(stride);
        else prepareVA();

        if (indexCount > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glDrawElements(mode.getGlType(), indexCount, GL_UNSIGNED_INT, 0L);
        } else if (vertexCount > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glDrawArrays(mode.getGlType(), 0, vertexCount);
        }

        postDraw();
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
        if (glIsVertexArray(vao))
            glDeleteVertexArrays(vao);
        if (glIsBuffer(vbo))
            glDeleteBuffers(vbo);
        if (glIsBuffer(ebo))
            glDeleteBuffers(ebo);
        batch.close();
        batch = null;
    }
}
