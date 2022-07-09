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

import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL15C;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.*;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.VertexFormat;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.stb.STBEasyFont.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class ClockDrawApp extends GlfwApplication {
    public static void main(String[] args) {
        new ClockDrawApp().launch();
    }

    private static final float[] TICKS_BUFFER = {
        125.0f, 216.50635f, 115.0f, 199.18584f, // 11
        216.50635f, 125.0f, 199.18584f, 115.0f, // 10
        250.0f, 0.0f, 230.0f, 0.0f, // 9
        216.50635f, -125.0f, 199.18584f, -115.0f, // 8
        125.0f, -216.50635f, 115.0f, -199.18584f, // 7
        0.0f, -250.0f, 0.0f, -230.0f, // 6
        -125.0f, -216.50635f, -115.0f, -199.18584f, // 5
        -216.50635f, -125.0f, -199.18584f, -115.0f, // 4
        -250.0f, 0.0f, -230.0f, 0.0f, // 3
        -216.50635f, 125.0f, -199.18584f, 115.0f, // 2
        -125.0f, 216.50635f, -115.0f, 199.18584f, // 1
        0.0f, 250.0f, 0.0f, 230.0f, // 12
        26.132116f, 248.63048f, 24.041546f, 228.74004f,
        51.977924f, 244.53691f, 47.81969f, 224.97395f,
        77.25425f, 237.76413f, 71.07391f, 218.74301f,
        101.68416f, 228.38637f, 93.54943f, 210.11546f,
        146.9463f, 202.25427f, 135.19061f, 186.07393f,
        167.28264f, 185.78624f, 153.90002f, 170.92334f,
        185.78621f, 167.28268f, 170.92331f, 153.90007f,
        202.25426f, 146.94633f, 186.07391f, 135.19063f,
        228.38637f, 101.68419f, 210.11546f, 93.54945f,
        237.76414f, 77.25428f, 218.74301f, 71.07394f,
        244.53693f, 51.97795f, 224.97397f, 47.819714f,
        248.6305f, 26.132145f, 228.74005f, 24.041573f,
        248.63051f, -26.132093f, 228.74007f, -24.041527f,
        244.53694f, -51.9779f, 224.97398f, -47.81967f,
        237.76416f, -77.254234f, 218.74303f, -71.0739f,
        228.3864f, -101.68415f, 210.1155f, -93.54942f,
        202.2543f, -146.94632f, 186.07394f, -135.19061f,
        185.78625f, -167.28267f, 170.92336f, -153.90005f,
        167.28271f, -185.78624f, 153.9001f, -170.92334f,
        146.94637f, -202.2543f, 135.19066f, -186.07394f,
        101.6842f, -228.38643f, 93.54946f, -210.11552f,
        77.25428f, -237.7642f, 71.07394f, -218.74307f,
        51.977947f, -244.53699f, 47.81971f, -224.97403f,
        26.132133f, -248.63055f, 24.041563f, -228.74011f,
        -26.132116f, -248.63057f, -24.041546f, -228.74013f,
        -51.977932f, -244.537f, -47.8197f, -224.97403f,
        -77.25427f, -237.76422f, -71.07393f, -218.74309f,
        -101.6842f, -228.38646f, -93.54946f, -210.11554f,
        -146.94637f, -202.25433f, -135.19066f, -186.07399f,
        -167.28271f, -185.7863f, -153.9001f, -170.9234f,
        -185.78627f, -167.28276f, -170.92337f, -153.90015f,
        -202.25433f, -146.94641f, -186.07399f, -135.1907f,
        -228.38649f, -101.68424f, -210.11557f, -93.5495f,
        -237.76427f, -77.25432f, -218.74313f, -71.073975f,
        -244.53705f, -51.977978f, -224.97409f, -47.81974f,
        -248.63063f, -26.132154f, -228.74017f, -24.041582f,
        -248.63063f, 26.132107f, -228.74017f, 24.041538f,
        -244.53705f, 51.977932f, -224.97409f, 47.8197f,
        -237.76428f, 77.25428f, -218.74315f, 71.07394f,
        -228.38652f, 101.68421f, -210.1156f, 93.54948f,
        -202.2544f, 146.9464f, -186.07405f, 135.19069f,
        -185.78635f, 167.28275f, -170.92345f, 153.90013f,
        -167.28279f, 185.78632f, -153.90016f, 170.92342f,
        -146.94644f, 202.25438f, -135.19073f, 186.07404f,
        -101.684265f, 228.38654f, -93.54952f, 210.11562f,
        -77.25434f, 237.76431f, -71.07399f, 218.74316f,
        -51.977993f, 244.5371f, -47.81975f, 224.97414f,
        -26.132166f, 248.63069f, -24.041594f, 228.74023f
    };
    public static final float PI2 = (float) (Math.PI * 2);
    private static final int slices = 80,
        circleNum = 100,
        vertexCount = (slices + 1) * circleNum,
        bufSz = vertexCount * 2;
    private int textVertCount = 0;
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4fStack model = new Matrix4fStack(2);
    private GLProgram program;
    private ByteBuffer fontBuffer;
    private VAObj ticks, clockHand, circle, text;

    /**
     * @author squid233
     * @since 0.2.0
     */
    private static final class VAObj implements AutoCloseable {
        public GLVao vao;
        private IGLBuffer.Array buffers;

        public <T, U> VAObj(T data, @Nullable U indices,
                            IGLBuffer.DataFunc<T> dataFunc,
                            @Nullable IGLBuffer.DataFunc<U> indicesDataFunc) {
            preCreate(indices != null ? 2 : 1);
            buffers.layout(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
                .bind()
                .data(data, dataFunc);
            if (indices != null) {
                buffers.layout(1, GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)
                    .bind(1)
                    .data(1, indices, indicesDataFunc);
            }
            postCreate();
        }

        private void preCreate(int count) {
            vao = new GLVao();
            bind();
            buffers = new IGLBuffer.Array(count);
        }

        private void postCreate() {
            VertexFormat.V2F.beginDraw(0, 0, 0);
            buffers.unbind();
            unbind();
        }

        public void bind() {
            vao.bind();
        }

        public void unbind() {
            vao.unbind();
        }

        @Override
        public void close() {
            vao.delete();
            buffers.deleteAll();
        }
    }

    @Override
    public void prepare() {
        WindowConfig.initialTitle = "OpenGL Clock";
        WindowConfig.setRequiredGlVer(3, 3);
    }

    @Override
    public void start() {
        final float color = 0x2b / 255.0f;
        clearColor(color, color, color, 1.0f);
        fontBuffer = memCalloc(1024);
        resManager = new ResManager();

        program = new GLProgram();
        if (!GLShaders.linkSimple(program,
            """
                #version 330 core
                layout (location = 0) in vec2 Position;
                uniform mat4 Projection, Model;
                void main() {
                  gl_Position = Projection * Model * vec4(Position, 0.0, 1.0);
                }""",
            """
                #version 330 core
                out vec4 FragColor;
                void main() {
                  FragColor = vec4(1.0);
                }""")) {
            throw new RuntimeException("Failed to link program! Log: " + program.getInfoLog());
        }
        program.createUniform("Projection", GLUniformType.M4F).set(projection);
        program.createUniform("Model", GLUniformType.M4F).set(model);

        ticks = new VAObj(TICKS_BUFFER, null, GL15C::glBufferData, null);
        clockHand = new VAObj(new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f
        }, null, GL15C::glBufferData, null);

        var buf = memAllocFloat(bufSz);
        for (int i = 0; i < circleNum; i++) {
            drawCircle(buf, 250 + (i * 0.1f), slices);
        }
        circle = new VAObj(buf.flip(), null, GL15C::glBufferData, null);
        memFree(buf);

        var vert = new ArrayList<Float>();
        stb_easy_font_spacing(-0.5f);
        for (int i = 1; i < 13; i++) {
            model.pushMatrix();
            var s = String.valueOf(i);
            int numQuads = stb_easy_font_print(0, 0, s, null, fontBuffer);
            float ang = Math.toRadians((i % 12) * 30.0f);
            model.rotateZ(-ang)
                .translate(0.0f, 215.0f, 0.0f)
                .rotateZ(ang)
                .translate(-stb_easy_font_width(s), stb_easy_font_height(s), 0.0f)
                .scale(2.0f, -2.0f, 1.0f);
            float x, y;
            for (int j = 0, c = numQuads * 64; j < c; j += 64) {
                x = fontBuffer.getFloat(j);
                y = fontBuffer.getFloat(j + 4);
                vert.add(Math.fma(model.m00(), x, Math.fma(model.m10(), y, model.m30())));
                vert.add(Math.fma(model.m01(), x, Math.fma(model.m11(), y, model.m31())));
                x = fontBuffer.getFloat(j + 16);
                y = fontBuffer.getFloat(j + 20);
                vert.add(Math.fma(model.m00(), x, Math.fma(model.m10(), y, model.m30())));
                vert.add(Math.fma(model.m01(), x, Math.fma(model.m11(), y, model.m31())));
                x = fontBuffer.getFloat(j + 32);
                y = fontBuffer.getFloat(j + 36);
                vert.add(Math.fma(model.m00(), x, Math.fma(model.m10(), y, model.m30())));
                vert.add(Math.fma(model.m01(), x, Math.fma(model.m11(), y, model.m31())));
                x = fontBuffer.getFloat(j + 48);
                y = fontBuffer.getFloat(j + 52);
                vert.add(Math.fma(model.m00(), x, Math.fma(model.m10(), y, model.m30())));
                vert.add(Math.fma(model.m01(), x, Math.fma(model.m11(), y, model.m31())));
            }
            model.popMatrix();
        }
        textVertCount = vert.size();
        buf = memAllocFloat(textVertCount);
        int[] idxBuf = new int[3 * textVertCount / 2];
        int curr = 0;
        int idx = 0;
        // stride in 8 floats
        for (int i = 0; i < textVertCount; i += 8) {
            // stride in 2 floats
            for (int j = 0; j < 8; j += 2) {
                buf.put(vert.get(i + j)).put(vert.get(i + j + 1));
            }
            // 0 1 2 2 3 0
            idxBuf[curr++] = idx++;
            idxBuf[curr++] = idx++;
            idxBuf[curr++] = idx;
            idxBuf[curr++] = idx++;
            idxBuf[curr++] = idx++;
            idxBuf[curr++] = idx - 4;
        }
        text = new VAObj(buf.flip(), idxBuf, GL15C::glBufferData, GL15C::glBufferData);
        memFree(buf);

        resManager.addResources(ticks, clockHand, circle, text, program);
    }

    public static void drawCircle(FloatBuffer buf,
                                  float r,
                                  int slices) {
        for (int i = 0; i <= slices; i++) {
            double d = PI2 * i / slices;
            buf.put((float) (r * Math.cos(d))).put((float) (r * Math.sin(d)));
        }
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);

        program.bind();
        program.getUniform("Projection").set(projection);
        program.getUniform("Model").set(model);
        program.updateUniforms();

        // Clock frame
        circle.bind();
        glDrawArrays(GL_LINE_STRIP, 0, vertexCount);
//        circle.unbind();

        var time = LocalTime.now();
        int hour = time.getHour();
        int min = time.getMinute();
        int sec = time.getSecond();

        clockHand.bind();
        // Hours
        program.getUniform("Model").set(
            model.pushMatrix()
                .rotateZ(-Math.toRadians(((hour % 12) * 30.0f) + (min * 0.5f) + (sec * 30.0f / 3600.0f)))
                .scale(1, 100, 1)
        );
        program.updateUniforms();
        glLineWidth(3.0f);
        glDrawArrays(GL_LINES, 0, 2);
        model.popMatrix();

        // Minutes
        program.getUniform("Model").set(
            model.pushMatrix()
                .rotateZ(-Math.toRadians((min * 6.0f) + (sec * 0.1f)))
                .scale(1, 150, 1)
        );
        program.updateUniforms();
        glLineWidth(2.0f);
        glDrawArrays(GL_LINES, 0, 2);
        model.popMatrix();

        // Seconds
        program.getUniform("Model").set(
            model.pushMatrix()
                .rotateZ(-Math.toRadians(sec * 6.0f))
                .scale(1, 200, 1)
        );
        program.updateUniforms();
        glLineWidth(1.0f);
        glDrawArrays(GL_LINES, 0, 2);
        model.popMatrix();
//        clockHand.unbind();

        // Draw ticks
        program.getUniform("Model").set(model);
        program.updateUniforms();
        ticks.bind();
        final int c10 = 12 * 2;
        final int c1 = (60 - 12) * 2;
        glLineWidth(10.0f);
        glDrawArrays(GL_LINES, 0, c10);
        glLineWidth(1.0f);
        glDrawArrays(GL_LINES, c10, c1);
//        ticks.unbind();

        text.bind();
        glDrawElements(GL_TRIANGLES, textVertCount, GL_UNSIGNED_INT, 0);
        text.unbind();

        GLStateMgr.useProgram(0);
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        projection.setOrthoSymmetric(width, height, -1, 1);
        model.identity();
    }

    @Override
    public void settingFrames(int prevFrames,
                              int currFrames) {
        window.setTitle(WindowConfig.initialTitle + " FPS: " + currFrames);
    }

    @Override
    public void close() {
        memFree(fontBuffer);
    }
}
