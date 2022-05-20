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

import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.gl.GLDrawMode;

import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GLImmeModeTest extends GlfwApplication {
    public static void main(String[] args) {
        new GLImmeModeTest().launch();
    }

    @Override
    public void start() {
        lglRequestContext();
        clearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT);
        lglMatrixMode(MatrixMode.PROJECTION);
        lglLoadIdentity();
        lglOrtho2D(0, window.getWidth(), window.getHeight(), 0);
        lglMatrixMode(MatrixMode.MODELVIEW);
        lglLoadIdentity();

        lglBegin(GLDrawMode.TRIANGLES);
        lglVertex(0, 0);
        lglEmit();
        lglVertex(0, 100);
        lglEmit();
        lglVertex(200, 100);
        lglEmit();
        lglEnd();
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
    }

    @Override
    public void close() {
        lglDestroyContext();
    }
}
