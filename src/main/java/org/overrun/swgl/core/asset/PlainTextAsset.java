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

import org.jetbrains.annotations.Nullable;
import org.overrun.swgl.core.io.IFileProvider;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Plain text asset for general resources.
 *
 * @author squid233
 * @since 0.1.0
 */
public class PlainTextAsset extends Asset {
    @Nullable
    private String content;

    /**
     * Construct without content.
     */
    public PlainTextAsset() {
    }

    /**
     * Construct with content.
     *
     * @param content The content string.
     */
    public PlainTextAsset(@Nullable String content) {
        this.content = content;
    }

    /**
     * Construct with content in file.
     *
     * @param name     The resource name.
     * @param provider The file provider.
     */
    public PlainTextAsset(String name, IFileProvider provider) {
        content = createStr(name, provider);
    }

    /**
     * Get the content in file.
     *
     * @param name     The resource name.
     * @param provider The file provider.
     */
    public static String createStr(String name, IFileProvider provider)
        throws RuntimeException {
        var sb = new StringBuilder();
        try (var is = provider.getFile(name);
             var isr = new InputStreamReader(Objects.requireNonNull(is,
                 "Can't get file \"" + name + "\"!"));
             var br = new BufferedReader(isr)) {
            var ln = br.readLine();
            if (ln != null) {
                sb.append(ln);
                while ((ln = br.readLine()) != null)
                    sb.append("\n").append(ln);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    @Override
    public void reload(String name, IFileProvider provider) {
        content = createStr(name, provider);
    }

    /**
     * Get the content.
     *
     * @return the content
     */
    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }
}
