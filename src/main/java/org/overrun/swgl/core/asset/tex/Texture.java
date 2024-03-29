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

import org.overrun.swgl.core.asset.Asset;

/**
 * A swgl texture.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class Texture<UserPointer>
    extends Asset<UserPointer>
    implements AutoCloseable {
    /**
     * Generates an id for this texture.
     */
    public abstract void create();

    /**
     * Binds this texture.
     */
    public abstract void bind();

    /**
     * Unbinds this texture.
     */
    public abstract void unbind();

    /**
     * Get the id of this texture.
     */
    public abstract int getId();

    /**
     * Get the texture width.
     *
     * @return the texture width
     */
    public abstract int getWidth();

    /**
     * Get the texture height.
     *
     * @return the texture height
     */
    public abstract int getHeight();
}
