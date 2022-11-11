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

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.AssetTypes;
import org.overrun.swgl.core.io.IFileProvider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.swgl.core.cfg.GlobalConfig.getDebugLogger;
import static org.overrun.swgl.core.gl.GLStateMgr.*;

/**
 * A swgl 2D texture.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Texture2D extends Texture {
    private int id;
    private boolean failed;
    private int width, height;
    @Nullable
    private TextureParam param;
    @Nullable
    private ITextureMipmap mipmap = ITextureMipmap.DEFAULT;
    public int defaultWidth = 16, defaultHeight = 16;

    /**
     * Create an empty 2D texture.
     */
    public Texture2D() {
    }

    /**
     * Create a 2D texture load from the file.
     *
     * @param name     The resource name.
     * @param provider The file provider.
     */
    public Texture2D(String name, IFileProvider provider) {
        reload(name, provider);
    }

    public static void createAsset(
        AssetManager mgr,
        String name,
        @Nullable Consumer<Texture2D> consumer,
        IFileProvider provider
    ) {
        mgr.createAsset(name, AssetTypes.TEXTURE2D, consumer, provider);
    }

    public static void createAssetParam(
        AssetManager mgr,
        String name,
        @Nullable ITextureParam param,
        IFileProvider provider
    ) {
        createAsset(mgr, name, (Texture2D tex) -> tex.setParam(param), provider);
    }

    public static Optional<Texture2D> createGetAssetParam(
        AssetManager mgr,
        String name,
        @Nullable ITextureParam param,
        IFileProvider provider
    ) {
        createAssetParam(mgr, name, param, provider);
        mgr.reloadAssets();
        return mgr.getAsset(name);
    }

    public static Optional<Texture2D> getAsset(
        AssetManager mgr,
        String name
    ) {
        return mgr.getAsset(name);
    }

    public static Supplier<Optional<Texture2D>> getAssetLazy(
        AssetManager mgr,
        String name
    ) {
        return mgr.getAssetLazy(name);
    }

    /**
     * {@inheritDoc}
     * <p>
     * You shouldn't call {@link #create()} before {@code reload()}.
     * </p>
     * <p>
     * If you do that, and if there are 1000 textures,
     * that means you created 2000 texture ids!
     * </p>
     *
     * @param name     The asset name or alias.
     * @param provider The file provider.
     */
    @Override
    public void reload(String name, IFileProvider provider) {
        ByteBuffer buffer = null;
        try {
            buffer = provider.res2BB(name, 8192);
        } catch (IOException e) {
            getDebugLogger().error("Error reading resource to buffer!", e);
        }
        buffer = asBuffer(buffer, name);
        try {
            build(buffer);
        } finally {
            memFree(buffer);
        }
    }

    /**
     * Create to an empty texture.
     *
     * @param width  the texture width
     * @param height the texture height
     * @since 0.2.0
     */
    public void loadEmpty(int width, int height) {
        this.width = width;
        this.height = height;
        build(null);
    }

    /**
     * Load texture from byte buffer.
     *
     * @param bytes     the byte buffer
     * @param name      the resource debugging name
     * @param freeBytes free bytes after loading buffer
     */
    public void load(ByteBuffer bytes,
                     @Nullable String name,
                     boolean freeBytes) {
        var buffer = asBuffer(bytes,
            Objects.requireNonNullElse(name,
                "byte array"));
        if (freeBytes) {
            memFree(bytes);
        }
        try {
            build(buffer);
        } finally {
            memFree(buffer);
        }
    }

    /**
     * Load texture from byte buffer.
     *
     * @param bytes the byte buffer
     * @param name  the resource debugging name
     */
    public void load(ByteBuffer bytes,
                     @Nullable String name) {
        load(bytes, name, false);
    }

    /**
     * Load texture from byte array.
     *
     * @param bytes the byte array
     * @param name  the resource debugging name
     */
    public void load(byte[] bytes, @Nullable String name) {
        var bb = memAlloc(bytes.length).put(bytes).flip();
        load(bb, name, true);
    }

    public void setParam(@Nullable TextureParam param) {
        this.param = param;
    }

    public Optional<TextureParam> getParam() {
        return Optional.ofNullable(param);
    }

    public void setMipmap(@Nullable ITextureMipmap mipmap) {
        this.mipmap = mipmap;
    }

    public Optional<ITextureMipmap> getMipmap() {
        return Optional.ofNullable(mipmap);
    }

    private ByteBuffer fail() {
        failed = true;
        width = (defaultWidth == 0 ? 16 : defaultWidth);
        height = (defaultHeight == 0 ? 16 : defaultHeight);
        int[] missingNo = new int[width * height];
        final int hx = width >> 1;
        final int hy = height >> 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = x + y * width;
                if (y < hy)
                    missingNo[index] = x < hx ? 0xfff800f8 : 0xff000000;
                else
                    missingNo[index] = x < hx ? 0xff000000 : 0xfff800f8;
            }
        }
        var buffer = memAlloc(missingNo.length * 4);
        buffer.asIntBuffer().put(missingNo).flip();
        return buffer;
    }

    private ByteBuffer asBuffer(ByteBuffer buffer,
                                String name) {
        if (buffer == null)
            return fail();
        int[] xp = {0}, yp = {0}, cp = {0};
        var ret = stbi_load_from_memory(
            buffer,
            xp,
            yp,
            cp,
            STBI_rgb_alpha);
        if (ret == null) {
            getDebugLogger().error("Failed to load image '{}'! Reason: {}",
                name,
                stbi_failure_reason());
            ret = fail();
        } else {
            width = xp[0];
            height = yp[0];
        }
        return ret;
    }

    private void build(ByteBuffer buffer) {
        int lastUnit = getActiveTexture();
        int lastId = get2DTextureId();
        if (!glIsTexture(id))
            create();
        bindTexture2D(0, id);
        if (mipmap != null) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
            if (!ITextureMipmap.hasARB() && !GL.getCapabilities().forwardCompatible) {
                glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);
            }
        }
        if (param != null)
            param.set(GL_TEXTURE_2D);
        glTexImage2D(GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width,
            height,
            0,
            failed ? GL_RGB : GL_RGBA,
            GL_UNSIGNED_BYTE,
            buffer);
        if (mipmap != null) {
            mipmap.set(GL_TEXTURE_2D, buffer);
        }
        bindTexture2D(lastUnit, lastId);
    }

    @Override
    public void create() {
        id = glGenTextures();
    }

    @Override
    public void bind() {
        bindTexture2D(id);
    }

    @Override
    public void unbind() {
        bindTexture2D(0);
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * If {@code true}, the texture uses the missing texture.
     *
     * @return If the texture loading failed.
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Get the texture width.
     *
     * @return the texture width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Get the texture height.
     *
     * @return the texture height
     */
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void close() {
        glDeleteTextures(id);
    }
}
