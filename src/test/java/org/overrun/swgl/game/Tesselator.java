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

package org.overrun.swgl.game;

import org.overrun.swgl.core.model.IModel;
import org.overrun.swgl.core.util.ListArrays;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author squid233
 * @since 0.1.0
 * @deprecated Going replace with IMS GL list
 */
@Deprecated
public class Tesselator implements AutoCloseable {
    private static final int MEMORY_ALLOC = 1024 * 8 /* 8 KB */;
    private static final Tesselator INSTANCE = new Tesselator();
    private int vao;
    private int vbo, vcbo, vtbo, vnbo;
    private int ebo;
    private boolean hasColor, hasTexture, hasNormal;
    private FloatBuffer vertexBuffer = memCallocFloat(MEMORY_ALLOC);
    private ByteBuffer colorBuffer = memCalloc(MEMORY_ALLOC);
    private FloatBuffer texBuffer = memCallocFloat(MEMORY_ALLOC);
    private ByteBuffer normalBuffer = memCalloc(MEMORY_ALLOC);
    private final List<Integer> indexBuffer = new ArrayList<>();
    private int vertexCount;
    private float x, y, z;
    private byte r, g, b, a;
    private float u, v;
    private byte nx, ny, nz;
    private boolean dirty = true;
    private int primitive = GL_TRIANGLES;

    public static Tesselator getInstance() {
        return INSTANCE;
    }

    public void enableColor() {
        hasColor = true;
    }

    public void disableColor() {
        hasColor = false;
    }

    public void enableTexture() {
        hasTexture = true;
    }

    public void disableTexture() {
        hasTexture = false;
    }

    public void enableNormal() {
        hasNormal = true;
    }

    public void disableNormal() {
        hasNormal = false;
    }

    public boolean hasColor() {
        return hasColor;
    }

    public boolean hasTexture() {
        return hasTexture;
    }

    public boolean hasNormal() {
        return hasNormal;
    }

    public void begin() {
        begin(GL_TRIANGLES);
    }

    public void begin(int primitive) {
        vertexBuffer.clear();
        colorBuffer.clear();
        texBuffer.clear();
        normalBuffer.clear();
        indexBuffer.clear();
        vertexCount = 0;
        dirty = true;
        this.primitive = primitive;
    }

    public Tesselator vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Tesselator color(float r, float g, float b, float a) {
        this.r = IModel.color2byte(r);
        this.g = IModel.color2byte(g);
        this.b = IModel.color2byte(b);
        this.a = IModel.color2byte(a);
        return this;
    }

    public Tesselator tex(float u, float v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public Tesselator normal(float x, float y, float z) {
        nx = IModel.normal2byte(x);
        ny = IModel.normal2byte(y);
        nz = IModel.normal2byte(z);
        return this;
    }

    public Tesselator quadIndex() {
        indexBuffer.add(vertexCount);
        indexBuffer.add(vertexCount + 1);
        indexBuffer.add(vertexCount + 2);
        indexBuffer.add(vertexCount + 2);
        indexBuffer.add(vertexCount + 3);
        indexBuffer.add(vertexCount);
        return this;
    }

    public Tesselator index(int... indices) {
        indexBuffer.addAll(ListArrays.ofInts(indices));
        return this;
    }

    public void emit() {
        if (vertexBuffer.capacity() <= vertexBuffer.position() + 3)
            vertexBuffer = memRealloc(vertexBuffer, vertexBuffer.capacity() + MEMORY_ALLOC);
        if (colorBuffer.capacity() <= colorBuffer.position() + 4)
            colorBuffer = memRealloc(colorBuffer, colorBuffer.capacity() + MEMORY_ALLOC);
        if (texBuffer.capacity() <= texBuffer.position() + 2)
            texBuffer = memRealloc(texBuffer, texBuffer.capacity() + MEMORY_ALLOC);
        if (normalBuffer.capacity() <= normalBuffer.position() + 3)
            normalBuffer = memRealloc(normalBuffer, normalBuffer.capacity() + MEMORY_ALLOC);
        vertexBuffer.put(x).put(y).put(z);
        colorBuffer.put(r).put(g).put(b).put(a);
        texBuffer.put(u).put(v);
        normalBuffer.put(nx).put(ny).put(nz);
        ++vertexCount;
    }

    public void flush() {
        if (dirty) {
            vertexBuffer.flip();
            colorBuffer.flip();
            texBuffer.flip();
            normalBuffer.flip();
        }

        if (!glIsVertexArray(vao))
            vao = glGenVertexArrays();
        glBindVertexArray(vao);

        if (!glIsBuffer(vbo))
            vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        if (dirty)
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,
            3,
            GL_FLOAT,
            false,
            12,
            0);

        if (hasColor) {
            if (!glIsBuffer(vcbo))
                vcbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vcbo);
            if (dirty)
                glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STREAM_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,
                4,
                GL_UNSIGNED_BYTE,
                true,
                4,
                0);
        } else {
            glDisableVertexAttribArray(1);
        }

        if (hasTexture) {
            if (!glIsBuffer(vtbo))
                vtbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vtbo);
            if (dirty)
                glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STREAM_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2,
                2,
                GL_FLOAT,
                false,
                8,
                0);
        } else {
            glDisableVertexAttribArray(2);
        }

        if (hasNormal) {
            if (!glIsBuffer(vnbo))
                vnbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vnbo);
            if (dirty)
                glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STREAM_DRAW);
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3,
                3,
                GL_BYTE,
                false,
                3,
                0);
        } else {
            glDisableVertexAttribArray(3);
        }

        if (!indexBuffer.isEmpty()) {
            if (!glIsBuffer(ebo))
                ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            if (dirty)
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, ListArrays.toIntArray(indexBuffer), GL_STREAM_DRAW);
            glDrawElements(primitive, indexBuffer.size(), GL_UNSIGNED_INT, 0);
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glDrawArrays(primitive, 0, vertexCount);
        }

        glBindVertexArray(0);
        dirty = false;
    }

    @Override
    public void close() {
        if (glIsVertexArray(vao))
            glDeleteVertexArrays(vao);
        if (glIsBuffer(vbo))
            glDeleteBuffers(vbo);
        if (glIsBuffer(vcbo))
            glDeleteBuffers(vcbo);
        if (glIsBuffer(vtbo))
            glDeleteBuffers(vtbo);
        if (glIsBuffer(ebo))
            glDeleteBuffers(ebo);
        memFree(vertexBuffer);
        memFree(colorBuffer);
        memFree(texBuffer);
        memFree(normalBuffer);
        indexBuffer.clear();
    }
}
