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

package org.overrun.swgl.test.ims;

import org.joml.Math;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLDrawMode;

import java.nio.ByteBuffer;
import java.time.LocalTime;

import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.stb.STBEasyFont.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class ClockDrawApp extends GlfwApplication {
    public static void main(String[] args) {
        var app = new ClockDrawApp();
        app.launch();
    }

    public static final float PI2 = (float) (Math.PI * 2);
    private ByteBuffer fontBuffer;

    @Override
    public void prepare() {
        GlobalConfig.initialTitle = "OpenGL Clock";
    }

    @Override
    public void preStart() {
        glfwWindowHint(GLFW_SAMPLES, 4);
    }

    @Override
    public void start() {
        lglRequestContext();
        float color = 0x2b / 255.0f;
        clearColor(color, color, color, 1.0f);
        glEnable(GL_MULTISAMPLE);
        fontBuffer = memAlloc(1024);
    }

    public static void drawCircle(float r,
                                  int slices) {
        lglBegin(GLDrawMode.LINES);
        for (int i = 0; i < slices; i++) {
            double d = PI2 * i / slices;
            lglVertex((float) (r * Math.cos(d)), (float) (r * Math.sin(d)));
            lglEmit();
            d = PI2 * (i + 1) / slices;
            lglVertex((float) (r * Math.cos(d)), (float) (r * Math.sin(d)));
            lglEmit();
        }
        lglEnd();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);
        lglMatrixMode(MatrixMode.PROJECTION);
        lglLoadIdentity();
        lglOrthoSymmetric(window.getWidth(), window.getHeight(), -1, 1);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();

        var time = LocalTime.now();
        int hour = time.getHour();
        int min = time.getMinute();
        int sec = time.getSecond();
        // Hours
        lglPushMatrix();
        lglRotateDeg(((hour % 12) * 30.0f) + (min * 0.5f) + (sec * 30.0f / 3600.0f), 0, 0, -1);
        glLineWidth(3.0f);
        lglBegin(GLDrawMode.LINES);
        lglVertex(0, 0);
        lglEmit();
        lglVertex(0, 100);
        lglEmit();
        lglEnd();
        lglPopMatrix();

        // Minutes
        lglPushMatrix();
        lglRotateDeg((min * 6.0f) + (sec * 0.1f), 0, 0, -1);
        glLineWidth(2.0f);
        lglBegin(GLDrawMode.LINES);
        lglVertex(0, 0);
        lglEmit();
        lglVertex(0, 150);
        lglEmit();
        lglEnd();
        lglPopMatrix();

        // Seconds
        lglPushMatrix();
        lglRotateDeg(sec * 6.0f, 0, 0, -1);
        glLineWidth(1.0f);
        lglBegin(GLDrawMode.LINES);
        lglVertex(0, 0);
        lglEmit();
        lglVertex(0, 200);
        lglEmit();
        lglEnd();
        lglPopMatrix();

        for (int i = 0; i < 100; i++) {
            drawCircle(250 + (i / 10.0f), 1000);
        }
        // Draw ticks
        glLineWidth(10.0f);
        lglPushMatrix();
        for (int i = 0; i < 12; i++) {
            lglRotateDeg(30, 0, 0, -1);
            lglBegin(GLDrawMode.LINES);
            lglVertex(0, 250);
            lglEmit();
            lglVertex(0, 230);
            lglEmit();
            lglEnd();
        }
        lglPopMatrix();
        glLineWidth(1.0f);
        lglPushMatrix();
        for (int i = 0; i < 60; i++) {
            lglRotateDeg(6, 0, 0, -1);
            lglBegin(GLDrawMode.LINES);
            lglVertex(0, 250);
            lglEmit();
            lglVertex(0, 235);
            lglEmit();
            lglEnd();
        }
        lglPopMatrix();

        stb_easy_font_spacing(-0.5f);
        for (int i = 1; i < 13; i++) {
            lglPushMatrix();
            var s = String.valueOf(i);
            int numQuads = stb_easy_font_print(0, 0, s, null, fontBuffer);
            float ang = (i % 12) * 30.0f;
            lglRotateDeg(ang, 0, 0, -1);
            lglTranslate(0.0f, 215.0f, 0.0f);
            lglRotateDeg(ang, 0, 0, 1);
            lglTranslate(-stb_easy_font_width(s), stb_easy_font_height(s), 0.0f);
            lglScale(2.0f, -2.0f, 1.0f);
            lglBegin(GLDrawMode.TRIANGLES);
            for (int j = 0, c = numQuads * 64; j < c; j += 64) {
                lglIndices(0, 1, 2, 2, 3, 0);
                lglVertex(fontBuffer.getFloat(j), fontBuffer.getFloat(j + 4));
                lglEmit();
                lglVertex(fontBuffer.getFloat(j + 16), fontBuffer.getFloat(j + 20));
                lglEmit();
                lglVertex(fontBuffer.getFloat(j + 32), fontBuffer.getFloat(j + 36));
                lglEmit();
                lglVertex(fontBuffer.getFloat(j + 48), fontBuffer.getFloat(j + 52));
                lglEmit();
            }
            lglEnd();
            lglPopMatrix();
        }
    }

    @Override
    public void close() {
        lglDestroyContext();
        memFree(fontBuffer);
    }
}
