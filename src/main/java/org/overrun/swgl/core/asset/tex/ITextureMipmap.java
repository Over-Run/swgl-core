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

package org.overrun.swgl.core.asset.tex;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * @author squid233
 * @since 0.1.0
 */
@FunctionalInterface
public interface ITextureMipmap {
    /**
     * The default mipmap setter using {@link GL30C#glGenerateMipmap(int) GenerateMipmap}.
     *
     * @since 0.2.0
     */
    ITextureMipmap DEFAULT = (target, buffer) -> {
        if (GL.getCapabilities().glGenerateMipmap != MemoryUtil.NULL) {
            GL30C.glGenerateMipmap(GL30C.GL_TEXTURE_2D);
        }
    };

    /**
     * Set the texture mipmap loader.
     *
     * @param target The texture target.
     * @param buffer The texture buffer in RGBA.
     */
    void set(int target, ByteBuffer buffer);
}
