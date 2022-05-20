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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.overrun.swgl.core.cfg.GlobalConfig;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Objects;

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
    @Nullable
    default InputStream getFile(@NotNull String name) {
        Objects.requireNonNull(name);
        var url = getUrl(name);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get the url of the file.
     *
     * @param name The resource name.
     * @return The url.
     * @since 0.2.0
     */
    @Nullable
    URL getUrl(String name);

    /**
     * Read all bytes from the file.
     *
     * @param name The resource name.
     * @return The bytes.
     */
    default byte[] getAllBytes(String name) {
        try (var is = getFile(name)) {
            return Objects.requireNonNull(is).readAllBytes();
        } catch (Exception e) {
            GlobalConfig.getDebugLogger().error("Error reading bytes!", e);
            return null;
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        var newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static ByteBuffer urlRes2BB(URL url, String src,
                                        String name, int bufferSize)
        throws IOException {
        if (url == null)
            throw new IOException(src + " resource not found: " + name);
        var file = new File(url.getFile());
        if (file.isFile()) {
            try (var fis = new FileInputStream(file);
                 var fc = fis.getChannel()) {
                return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            }
        }
        var buffer = BufferUtils.createByteBuffer(bufferSize);
        var source = url.openStream();
        if (source == null)
            throw new FileNotFoundException(name);
        try (source) {
            byte[] buf = new byte[8192];
            while (true) {
                int bytes = source.read(buf);
                if (bytes == -1)
                    break;
                if (buffer.remaining() < bytes)
                    buffer = resizeBuffer(buffer, Math.max(buffer.capacity() * 2, buffer.capacity() - buffer.remaining() + bytes));
                buffer.put(buf, 0, bytes);
            }
            buffer.flip();
        }
        return buffer;
    }

    static ByteBuffer ioRes2BB(String name, int bufferSize) throws IOException {
        return urlRes2BB(Thread.currentThread().getContextClassLoader().getResource(name),
            "Classpath", name, bufferSize);
    }

    /**
     * Load classpath resource to byte buffer without checked exceptions.
     *
     * @param name       the resource name
     * @param bufferSize the buffer size to be used if it is not a file
     * @return the direct byte buffer
     * @throws RuntimeException the exception from IOE
     * @since 0.2.0
     */
    static ByteBuffer ioRes2BBWithRE(String name, int bufferSize)
        throws RuntimeException {
        try {
            return ioRes2BB(name, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load FileProvider resource to byte buffer.
     *
     * @param name       the resource name
     * @param bufferSize the buffer size to be used if it is not a file
     * @return the direct byte buffer
     * @throws IOException the exception from IOE
     * @since 0.2.0
     */
    default ByteBuffer res2BB(String name, int bufferSize) throws IOException {
        return urlRes2BB(getUrl(name), "FileProvider", name, bufferSize);
    }

    /**
     * Load FileProvider resource to byte buffer without checked exceptions.
     *
     * @param name       the resource name
     * @param bufferSize the buffer size to be used if it is not a file
     * @return the direct byte buffer
     * @throws RuntimeException the exception from IOE
     * @since 0.2.0
     */
    default ByteBuffer res2BBWithRE(String name, int bufferSize)
        throws RuntimeException {
        try {
            return res2BB(name, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The classpath file provider.
     */
    IFileProvider CLASSPATH = ClassLoader::getSystemResource;
    /**
     * The local file provider.
     */
    IFileProvider LOCAL = name -> {
        try {
            return Path.of(name).toUri().toURL();
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
        return classLoader::getResource;
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
