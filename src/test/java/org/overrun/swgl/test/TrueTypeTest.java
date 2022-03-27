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

import org.lwjgl.opengl.GLUtil;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.io.IFileProvider;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class TrueTypeTest extends GlfwApplication {
    public static void main(String[] args) {
        var t3 = new TrueTypeTest();
        t3.launch();
    }

    private final STBTTBakedChar.Buffer cdata = STBTTBakedChar.calloc(96);
    private int ftex;

    private void ttInitFont() {
        byte[] bytes = IFileProvider.LOCAL.getAllBytes("C:/Windows/Fonts/times.ttf");
        var ttfBuffer = memAlloc(bytes.length).put(bytes).flip();
        var tempBitmap = memAlloc(512 * 512);
        stbtt_BakeFontBitmap(ttfBuffer,
            32.0f,
            tempBitmap,
            512,
            512,
            32,
            cdata);
        memFree(ttfBuffer);
        var rgbaBitmap = memAlloc(512 * 512 * 4);
        for (int i = 0; i < rgbaBitmap.limit(); i += 4) {
            rgbaBitmap.put(i, (byte) -1);
            rgbaBitmap.put(i + 1, (byte) -1);
            rgbaBitmap.put(i + 2, (byte) -1);
            rgbaBitmap.put(i + 3, tempBitmap.get(i >> 2));
        }
        memFree(tempBitmap);
        ftex = glGenTextures();
        bindTexture2D(ftex);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_RGBA,
            512,
            512,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            rgbaBitmap);
        memFree(rgbaBitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    }

    private void ttPrint(float x, float y, String text) {
        bindTexture2D(ftex);
        enableTexture2D();
        lglSetTexCoordArrayState(true);
        lglBegin(GLDrawMode.QUADS);
        lglColor(1, 1, 1, 1);
        var q = STBTTAlignedQuad.calloc();
        float[] px = {x};
        float[] py = {y};
        for (char c : text.toCharArray()) {
            if (c >= 32 && c < 128) {
                stbtt_GetBakedQuad(cdata,
                    512,
                    512,
                    c - 32,
                    px,
                    py,
                    q,
                    true);
                lglTexCoord(q.s0(), q.t0());
                lglVertex(q.x0(), q.y0());
                lglEmit();
                lglTexCoord(q.s0(), q.t1());
                lglVertex(q.x0(), q.y1());
                lglEmit();
                lglTexCoord(q.s1(), q.t1());
                lglVertex(q.x1(), q.y1());
                lglEmit();
                lglTexCoord(q.s1(), q.t0());
                lglVertex(q.x1(), q.y0());
                lglEmit();
            }
        }
        q.close();
        lglEnd();
        lglSetTexCoordArrayState(false);
    }

    @Override
    public void start() {
        GLUtil.setupDebugMessageCallback(GlobalConfig.getDebugStream());
        clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        lglRequestContext();
        ttInitFont();
    }

    @Override
    public void run() {
        lglGetMatrix(MatrixMode.PROJECTION).setOrtho2D(0, window.getWidth(), window.getHeight(), 0).translate(0.5f, 0.5f, 0.0f);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();
        clear(COLOR_BUFFER_BIT);
        enableBlend();
        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        ttPrint(100, 100, "Hello World! Using Times New Roman font");
        disableBlend();
    }

    @Override
    public void onResize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void close() {
        cdata.close();
        lglDestroyContext();
    }
}
