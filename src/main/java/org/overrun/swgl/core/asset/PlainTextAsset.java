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

import org.overrun.swgl.core.io.IFileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Plain text asset for general resources.
 *
 * @author squid233
 * @since 0.1.0
 */
public class PlainTextAsset extends Asset {
    private final String content;

    public PlainTextAsset(String content) {
        this.content = content;
    }

    public PlainTextAsset(IFileSystem fs, String name) {
        content = createStr(fs, name);
    }

    public static String createStr(IFileSystem fs, String name)
        throws RuntimeException {
        var sb = new StringBuilder();
        try (var is = fs.getFile(name);
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

    public String getContent() {
        return content;
    }
}
