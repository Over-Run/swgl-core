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

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

package org.overrun.swgl.core.io;

import org.lwjgl.BufferUtils;
import org.overrun.swgl.core.cfg.GlobalConfig;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The file provider interface for operating files.
 *
 * @author squid233
 * @since 0.1.0
 */
@FunctionalInterface
public interface IFileProvider {
    /**
     * Get the file from the filename.
     *
     * @param name The resource name.
     * @return The InputStream
     */
    InputStream getFile(String name);

    /**
     * Read all bytes from the file.
     *
     * @param name The resource name.
     * @return The bytes.
     */
    default byte[] getAllBytes(String name) {
        try (var is = getFile(name)) {
            return is.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace(GlobalConfig.getDebugStream());
            return null;
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        var newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    static ByteBuffer ioRes2BB(String name, int bufferSize)
        throws IOException {
        ByteBuffer buffer;
        var url = Thread.currentThread().getContextClassLoader().getResource(name);
        if (url == null)
            throw new IOException("Classpath resource not found: " + name);
        var file = new File(url.getFile());
        if (file.isFile()) {
            try (var fis = new FileInputStream(file);
                 var fc = fis.getChannel()) {
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            }
        } else {
            buffer = BufferUtils.createByteBuffer(bufferSize);
            var source = url.openStream();
            if (source == null)
                throw new FileNotFoundException(name);
            try (source) {
                byte[] buf = new byte[8192];
                while (true) {
                    int bytes = source.read(buf, 0, buf.length);
                    if (bytes == -1)
                        break;
                    if (buffer.remaining() < bytes)
                        buffer = resizeBuffer(buffer, Math.max(buffer.capacity() * 2, buffer.capacity() - buffer.remaining() + bytes));
                    buffer.put(buf, 0, bytes);
                }
                buffer.flip();
            }
        }
        return buffer;
    }

    /**
     * The classpath file provider.
     */
    IFileProvider CLASSPATH = ClassLoader::getSystemResourceAsStream;
    /**
     * The local file provider.
     */
    IFileProvider LOCAL = name -> {
        try {
            return Files.newInputStream(Path.of(name), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * Create a file provider from class loader.
     *
     * @param classLoader The class loader.
     * @return The file provider
     */
    static IFileProvider of(ClassLoader classLoader) {
        return classLoader::getResourceAsStream;
    }

    /**
     * Create a file provider from class.
     *
     * @param cls The class.
     * @return The file provider
     */
    static IFileProvider of(Class<?> cls) {
        return of(cls.getClassLoader());
    }

    /**
     * Create a file provider from caller class.
     * <p>
     * This is a convenience method, using {@link StackWalker}.
     * </p>
     *
     * @return The file provider
     */
    static IFileProvider ofCaller() {
        return of(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }
}
