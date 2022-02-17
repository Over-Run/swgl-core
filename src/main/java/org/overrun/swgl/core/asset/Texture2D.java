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

package org.overrun.swgl.core.asset;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.io.IFileProvider;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * A swgl 2D texture.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Texture2D extends Texture {
    private static final int[] MISSING_NO = {
        0xf800f8, 0x000000,
        0x000000, 0xf800f8
    };
    private int id;
    private boolean failed;
    private int width, height;
    private final Map<Integer, Runnable> texParamRecorder = new LinkedHashMap<>();
    private final List<Integer> recorderIds = new ArrayList<>();
    private int nextRecorderId;

    /**
     * Create an empty 2D texture.
     */
    public Texture2D() {
    }

    public Texture2D(String name, IFileProvider provider) {
        create();
        reload(name, provider);
    }

    public void reload(String name, IFileProvider provider) {
        if (!glIsTexture(id))
            create();
        ByteBuffer buffer;
        var bytes = provider.getAllBytes(name);
        if (bytes == null) {
            buffer = fail();
        } else {
            var bb = memAlloc(bytes.length);
            try (var stack = MemoryStack.stackPush()) {
                bb.put(bytes).flip();
                var xp = stack.mallocInt(1);
                var yp = stack.mallocInt(1);
                var cp = stack.mallocInt(1);
                buffer = stbi_load_from_memory(bb,
                    xp,
                    yp,
                    cp,
                    STBI_rgb_alpha);
                if (buffer == null) {
                    GlobalConfig.getDebugStream().println("Failed to load image '"
                        + name
                        + "'! Reason: "
                        + stbi_failure_reason());
                    buffer = fail();
                } else {
                    width = xp.get(0);
                    height = yp.get(0);
                }
            } finally {
                memFree(bb);
            }
        }
        build(buffer);
        memFree(buffer);
    }

    public int recordTexParam(Runnable recorder) {
        recorderIds.add(nextRecorderId++);
        texParamRecorder.put(nextRecorderId, recorder);
        return nextRecorderId;
    }

    public void rerecordTexParam(int id, Runnable recorder) {
        if (!hasRecorder(id)) {
            throw new IllegalArgumentException("Please use recordTexParam first!");
        }
        texParamRecorder.put(id, recorder);
    }

    public void destroyRecorder(int id) {
        recorderIds.remove((Object) id);
        texParamRecorder.remove(id);
    }

    public boolean hasRecorder(int id) {
        return recorderIds.contains(id);
    }

    private ByteBuffer fail() {
        failed = true;
        width = 2;
        height = 2;
        var buffer = memAlloc(MISSING_NO.length * 4);
        buffer.asIntBuffer().put(MISSING_NO).flip();
        return buffer;
    }

    private void build(ByteBuffer buffer) {
        int lastUnit = getActive2DTexture();
        int lastId = get2DTextureId();
        bindTexture2D(0, id);
        for (var recorder : texParamRecorder.values()) {
            recorder.run();
        }
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width,
            height,
            0,
            failed ? GL_RGB : GL_RGBA,
            GL_UNSIGNED_BYTE,
            buffer);
        if (GL.getCapabilities().OpenGL30)
            glGenerateMipmap(GL_TEXTURE_2D);
        bindTexture2D(lastUnit, lastId);
    }

    @Override
    public void create() {
        id = glGenTextures();
    }

    @Override
    public void bind() {
        bindTexture2D(id);
    }

    @Override
    public void unbind() {
        bindTexture2D(0);
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isFailed() {
        return failed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void close() {
        glDeleteTextures(id);
    }
}
