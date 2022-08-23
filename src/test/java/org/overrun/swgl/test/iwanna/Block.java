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

package org.overrun.swgl.test.iwanna;

import org.overrun.swgl.core.gl.GLBatch;

/**
 * @author squid233
 * @since 0.2.0
 */
public class Block {
    public void generateModel(GLBatch batch, int x, int y) {
        batch.indexBefore(
            0, 1, 8, 8, 7, 0,
            2, 1, 8, 8, 3, 2,
            6, 5, 8, 8, 7, 6,
            4, 5, 8, 8, 3, 4
        );
        float x0 = (float) x;
        float x1 = x0 + 0.5f;
        float x2 = x0 + 1.0f;
        float y0 = (float) y;
        float y1 = y0 + 0.5f;
        float y2 = y0 + 1.0f;
        batch.color(0.0f, 0.0f, 0.0f).vertex(x0, y2, 0.0f).emit(); // 0
        batch.color(1.0f, 0.0f, 0.0f).vertex(x0, y1, 0.0f).emit(); // 1
        batch.color(0.0f, 1.0f, 0.0f).vertex(x0, y0, 0.0f).emit(); // 2
        batch.color(1.0f, 1.0f, 0.0f).vertex(x1, y0, 0.0f).emit(); // 3
        batch.color(0.0f, 0.0f, 1.0f).vertex(x2, y0, 0.0f).emit(); // 4
        batch.color(1.0f, 0.0f, 1.0f).vertex(x2, y1, 0.0f).emit(); // 5
        batch.color(0.0f, 1.0f, 1.0f).vertex(x2, y2, 0.0f).emit(); // 6
        batch.color(1.0f, 1.0f, 1.0f).vertex(x1, y2, 0.0f).emit(); // 7
        batch.color(0.5f, 0.5f, 0.5f).vertex(x1, y1, 0.0f).emit(); // 8
    }
}
