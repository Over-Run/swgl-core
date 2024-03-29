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

/**
 * A swgl asset.
 *
 * @param <UserPointer> The user pointer type.
 * @author squid233
 * @since 0.1.0
 */
public abstract class Asset<UserPointer> {
    public static final String BUILTIN_RES_BASE_DIR = "__$$swgl_core$$__unique_res$$__";

    /**
     * Reloads or loads the asset by the specified name and {@link IFileProvider FileProvider}.
     *
     * @param name     The asset name.
     * @param provider The file provider.
     * @param pointer  The user pointer.
     */
    public abstract void reload(
        String name,
        IFileProvider provider,
        @Nullable UserPointer pointer
    );

    /**
     * Disposes this asset.
     *
     * @throws Exception If the asset can't be disposed
     */
    public void close() throws Exception {
    }
}
