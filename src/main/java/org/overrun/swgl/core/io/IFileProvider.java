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

import org.overrun.swgl.core.cfg.GlobalConfig;

import java.io.IOException;
import java.io.InputStream;
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
        } catch (IOException e) {
            e.printStackTrace(GlobalConfig.getDebugStream());
            return null;
        }
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
}
