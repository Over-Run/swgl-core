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

package org.overrun.swgl.game.world;

import org.overrun.swgl.core.gl.batch.GLBatch;
import org.overrun.swgl.core.model.VertexLayout;

import static org.overrun.swgl.core.model.VertexFormat.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class Tesselator implements AutoCloseable {
    private static final VertexLayout V3F_C3UB_T2F = new VertexLayout(V3F, C3UB, T2F);
    private GLBatch batch;

    public void init() {
        batch = new GLBatch();
    }

    public void start() {
        batch.begin(V3F_C3UB_T2F);
    }

    public GLBatch batch() {
        return batch;
    }

    public TessBuffer end() {
        batch.end();
        return new TessBuffer(batch.getBuffer(), batch.getIndexBuffer().orElse(null));
    }

    @Override
    public void close() {
        batch.close();
    }
}
