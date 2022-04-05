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

package org.overrun.swgl.game.atlas;

import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.io.IFileProvider;

import static org.lwjgl.opengl.GL12C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class BlockAtlas {
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    public static final boolean HAS_MIPMAP = true;
    public static final int MIPMAP_LEVEL = 4;
    public static final String TEXTURE = "swgl_game/blocks.png";
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    public static void create(AssetManager mgr) {
        Texture2D.createAsset(mgr,
            BlockAtlas.TEXTURE,
            tex -> {
                tex.setParam(target -> {
                    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, HAS_MIPMAP ? GL_NEAREST_MIPMAP_LINEAR : GL_NEAREST);
                    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    if (HAS_MIPMAP) {
                        glTexParameterf(target, GL_TEXTURE_MIN_LOD, 0);
                        glTexParameterf(target, GL_TEXTURE_MAX_LOD, MIPMAP_LEVEL);
                        glTexParameteri(target, GL_TEXTURE_MAX_LEVEL, MIPMAP_LEVEL);
                    }
                });
                if (!HAS_MIPMAP) {
                    tex.setMipmap(null);
                }
            },
            FILE_PROVIDER);
    }
}
