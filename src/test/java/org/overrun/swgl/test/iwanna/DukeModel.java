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

import org.lwjgl.opengl.GL15C;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.gl.GLVao;
import org.overrun.swgl.core.gl.IGLBuffer;
import org.overrun.swgl.core.gl.batch.GLBatch;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.level.SpriteDrawer;
import org.overrun.swgl.core.model.VertexLayout;

import static org.lwjgl.opengl.GL30C.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class DukeModel {
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static Texture2D texture2D;
    private static GLVao vao;
    private static IGLBuffer.Single vbo, ebo;
    private static int indexCount;

    public static void build(VertexLayout layout) {
        texture2D = new Texture2D("textures/iws/duke.png", FILE_PROVIDER);
        var batch = new GLBatch();
        batch.begin(layout);
        batch.indexBefore(0, 1, 2, 2, 3, 0);
        SpriteDrawer.draw(texture2D, 0, 0, 1, 1, 48, 48, 48, 48, true, batch);
        batch.end();
        indexCount = batch.getIndexCount();
        vao = new GLVao();
        vao.bind();
        vbo = new IGLBuffer.Single()
            .layout(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
            .bind()
            .data(batch.getBuffer(), GL15C::glBufferData);
        ebo = new IGLBuffer.Single()
            .layout(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)
            .bind()
            .data(batch.getIndexBuffer().orElseThrow(), GL15C::glBufferData);
        layout.beginDraw();
        vbo.unbind();
        vao.unbind();
        batch.close();
    }

    public static void render() {
        vao.bind();
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L);
        vao.unbind();
    }

    public static Texture2D getTexture() {
        return texture2D;
    }

    public static void destroy() {
        texture2D.close();
        texture2D = null;
        vao.delete();
        vbo.delete();
        ebo.delete();
    }
}
