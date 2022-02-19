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

package org.overrun.swgl.core.io;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author squid233
 * @since 0.1.0
 */
@FunctionalInterface
public interface ICleaner {
    /**
     * Empty cleaner to do nothing.
     */
    ICleaner EMPTY = buffer -> {
    };
    /**
     * Uses {@link MemoryUtil#memFree(Buffer)} to free memory.
     */
    ICleaner MEM_UTIL = MemoryUtil::memFree;
    /**
     * Uses {@link STBImage#stbi_image_free(ByteBuffer)} to free memory.
     */
    ICleaner STBI = STBImage::stbi_image_free;

    /**
     * Frees memory.
     *
     * @param buffer The buffer.
     */
    void free(ByteBuffer buffer);
}
