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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The default file systems.
 *
 * @author squid233
 * @since 0.1.0
 */
public class DefaultFileSystems {
    /**
     * The classpath file system.
     */
    public static final IFileSystem CLASSPATH = ClassLoader::getSystemResourceAsStream;
    /**
     * The local file system.
     */
    public static final IFileSystem LOCAL = name -> {
        try {
            return Files.newInputStream(Path.of(name), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * Create a file system from class loader.
     *
     * @param classLoader The class loader.
     * @return The file system
     */
    public static ClassLoaderFS of(ClassLoader classLoader) {
        return new ClassLoaderFS(classLoader);
    }

    /**
     * Create a file system from class.
     *
     * @param cls The class.
     * @return The file system
     */
    public static ClassLoaderFS of(Class<?> cls) {
        return new ClassLoaderFS(cls.getClassLoader());
    }

    /**
     * The file system by class loader.
     *
     * @author squid233
     * @since 0.1.0
     */
    public record ClassLoaderFS(ClassLoader classLoader) implements IFileSystem {
        @Override
        public InputStream getFile(String name) {
            return classLoader.getResourceAsStream(name);
        }
    }
}
