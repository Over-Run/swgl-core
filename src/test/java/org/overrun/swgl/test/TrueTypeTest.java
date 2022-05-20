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
import org.lwjgl.opengl.GLUtil;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLBlendFunc;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.gui.font.STBFontInfoBuffer;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.BuiltinVertexLayouts;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class TrueTypeTest extends GlfwApplication {
    public static void main(String[] args) {
        new TrueTypeTest().launch();
    }

    private static final int BITMAP_W = 1024;
    private static final int BITMAP_H = 256;
    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f model = new Matrix4f();
    private GLProgram program;
    private int vao, vbo, ebo;
    private int ftex;

    private void ttInitFont() {
        var fontInfoBuffer =
            resManager.addResource(new STBFontInfoBuffer())
                .init("C:/Windows/Fonts/times.ttf", IFileProvider.LOCAL, () -> {
                    throw new RuntimeException("Failed to init TimesNewRoman");
                })
                .addBackendFont(new STBFontInfoBuffer().init("unifont.ttf", IFileProvider.LOCAL, () -> {
                    throw new RuntimeException("Failed to init Unifont");
                }))
                .computeVMetrics(32.0f);

        var tempBitmap = fontInfoBuffer.writeBuffer(BITMAP_W, BITMAP_H, FontTestText.EAT_GLASS_TEXT);

        ftex = glGenTextures();
        bindTexture2D(ftex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int align = glGetInteger(GL_UNPACK_ALIGNMENT);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_RED,
            BITMAP_W,
            BITMAP_H,
            0,
            GL_RED,
            GL_UNSIGNED_BYTE,
            tempBitmap);
        glPixelStorei(GL_UNPACK_ALIGNMENT, align);
        memFree(tempBitmap);
    }

    @Override
    public void prepare() {
        WindowConfig.initialWidth = 960;
    }

    @Override
    public void start() {
        GLUtil.setupDebugMessageCallback();
        clearColor(0.4f, 0.6f, 0.9f, 1.0f);
        resManager = new ResManager();

        program = resManager.addResource(new GLProgram(BuiltinVertexLayouts.T2F_V3F));
        program.create();
        GLShaders.linkSimple(program,
            """
                #version 330 core
                layout (location = 0) in vec2 UV;
                layout (location = 1) in vec3 Position;
                out vec2 TexCoord;
                uniform mat4 proj, model;
                void main() {
                    gl_Position = proj * model * vec4(Position, 1.0);
                    TexCoord = UV;
                }""",
            """
                #version 330 core
                in vec2 TexCoord;
                out vec4 FragColor;
                uniform sampler2D Sampler;
                void main() {
                    FragColor = vec4(1.0, 1.0, 1.0, texture(Sampler, TexCoord).r);
                }
                """);
        program.createUniform("proj", GLUniformType.M4F);
        program.createUniform("model", GLUniformType.M4F);
        program.createUniform("Sampler", GLUniformType.I1).set(0);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, new float[]{
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, BITMAP_H, 0.0f,
            1.0f, 1.0f, BITMAP_W, BITMAP_H, 0.0f,
            1.0f, 0.0f, BITMAP_W, 0.0f, 0.0f,
        }, GL_STATIC_DRAW);
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, new int[]{
            0, 1, 2, 2, 3, 0
        }, GL_STATIC_DRAW);
        program.layoutBeginDraw();
        glBindVertexArray(0);

        ttInitFont();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);
        program.bind();
        program.getUniform("proj").set(proj.setOrtho2D(0, window.getWidth(), window.getHeight(), 0));
        program.getUniform("model").set(model.translation(0.5f, 0.5f, 0.0f).translate(50, 100, 0));
        program.updateUniforms();
        enableBlend();
        blendFunc(GLBlendFunc.SRC_ALPHA, GLBlendFunc.ONE_MINUS_SRC_ALPHA);
        bindTexture2D(ftex);
        enableTexture2D();
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);
        disableBlend();
        program.unbind();
    }

    @Override
    public void close() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }
}
