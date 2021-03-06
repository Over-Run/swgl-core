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

import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.ITessCallback;
import org.overrun.swgl.core.gui.AWTChain;
import org.overrun.swgl.core.gui.font.AWTFontTexture;
import org.overrun.swgl.core.io.ResManager;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.overrun.swgl.core.gl.GLBlendFunc.ONE_MINUS_SRC_ALPHA;
import static org.overrun.swgl.core.gl.GLBlendFunc.SRC_ALPHA;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public final class AWTTest extends GlfwApplication {
    public static void main(String[] args) {
        new AWTTest().launch();
    }

    private AWTFontTexture fontTexture;

    @Override
    public void prepare() {
        AWTChain.prepare();
        WindowConfig.coreProfile = false;
        WindowConfig.forwardCompatible = false;
    }

    @Override
    public void start() {
        clearColor(0.4f, 0.6f, 0.9f, 1.0f);
        fontTexture = new AWTFontTexture()
            .font(new Font("Unifont", Font.PLAIN, 20))
            .maxSize(getMaxTextureSize())
            .antialias(false);
        // TODO: 2022/7/12
        long start = System.currentTimeMillis();
        fontTexture.buildTexture();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        resManager = new ResManager();
        resManager.addResource(fontTexture);

        enableTexture2D();
        enableBlend();
        blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
    }

    private void drawTexture(Texture2D tex) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 1);
        glVertex2f(0, tex.getHeight());
        glTexCoord2f(1, 1);
        glVertex2f(tex.getWidth(), tex.getHeight());
        glTexCoord2f(1, 0);
        glVertex2f(tex.getWidth(), 0);
        glEnd();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);
        var tex = fontTexture.texture();
        tex.bind();
        if (false) {
            drawTexture(tex);
        } else {
            ITessCallback cb = (x, y, z, w, r, g, b, a, s, t, p, q, nx, ny, nz, i) -> {
                glTexCoord2f(s, t);
                glVertex2f(x, y);
            };
            glBegin(GL_QUADS);
            fontTexture.drawText(FontTestText.EAT_GLASS_TEXT, false, cb);
            glEnd();
        }
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
    }
}
