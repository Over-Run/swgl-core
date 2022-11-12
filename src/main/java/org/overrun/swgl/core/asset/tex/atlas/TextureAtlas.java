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

package org.overrun.swgl.core.asset.tex.atlas;

import org.overrun.swgl.core.asset.tex.ITextureMipmap;
import org.overrun.swgl.core.asset.tex.Texture;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.asset.tex.TextureParam;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.util.math.Numbers;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL12C.*;

/**
 * The texture atlas.
 *
 * @author squid233
 * @since 0.2.0
 */
public class TextureAtlas extends Texture<TextureAtlas.UserPointer> {
    private int maxMipmapLevel;
    private int mipmapLevel;
    private int minSpriteWidth = Integer.MAX_VALUE, minSpriteHeight = Integer.MAX_VALUE;
    private Texture2D texture;
    private Map<String, AtlasSpriteSlot> slotMap;
    private TextureParam extraParam = null;

    public TextureAtlas(int maxMipmapLevel) {
        this.maxMipmapLevel = maxMipmapLevel;
    }

    public TextureAtlas() {
        this(-1);
    }


    /**
     * The user pointer type.
     *
     * @author squid233
     * @since 0.2.0
     */
    public interface UserPointer extends List<SpriteInfo> {
    }

    /**
     * Load the atlas.
     *
     * @param infoArr the sprite info array
     */
    public void load(SpriteInfo... infoArr) {
        load(List.of(infoArr));
    }

    /**
     * Load the atlas from a collection.
     *
     * @param infoCollection the sprite info collection
     */
    public void load(Collection<SpriteInfo> infoCollection) {
        load(new ArrayList<>(infoCollection));
    }

    /**
     * Load the atlas from a list.
     *
     * @param infoList the sprite info list
     */
    public void load(List<SpriteInfo> infoList) {
        if (infoList.size() == 0)
            return;
        var infoMap = new HashMap<String, SpriteInfo>();
        slotMap = new HashMap<>();
        boolean nonPot = false;
        for (var info : infoList) {
            info.load();
            int w = info.width(), h = info.height();
            if (w < minSpriteWidth) minSpriteWidth = w;
            if (h < minSpriteHeight) minSpriteHeight = h;
            if (!Numbers.isPoT(w) || !Numbers.isPoT(h))
                nonPot = true;
            infoMap.put(info.name(), info);
            slotMap.put(info.name(), new AtlasSpriteSlot(w, h));
        }

        var slots = slotMap.values();
        var packer = new GrowingPacker();
        packer.fit(slots);
        packer.root.w = Numbers.toPoT(packer.root.w);
        packer.root.h = Numbers.toPoT(packer.root.h);

        if (maxMipmapLevel == 0 || nonPot) {
            mipmapLevel = 0;
        } else {
            mipmapLevel = (int) Numbers.log2(Math.min(minSpriteWidth, minSpriteHeight));
            if (maxMipmapLevel > 0 && mipmapLevel > maxMipmapLevel)
                mipmapLevel = maxMipmapLevel;
        }
        texture = new Texture2D();
        texture.setParam(new TextureParam()
            .minLod(0)
            .maxLod(mipmapLevel)
            .baseLevel(0)
            .maxLevel(mipmapLevel)
            .fromOther(extraParam));
        ITextureMipmap mipmap = (target, buffer) -> {
            for (int i = 0; i < mipmapLevel; i++) {
                int lvl = (i + 1);
                glTexImage2D(GL_TEXTURE_2D,
                    lvl,
                    GL_RGBA,
                    packer.root.w >> lvl,
                    packer.root.h >> lvl,
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    (ByteBuffer) null);
            }
            for (var e : slotMap.entrySet()) {
                var slot = e.getValue();
                if (slot.fit != null) {
                    var info = infoMap.get(e.getKey());
                    glTexSubImage2D(target,
                        0,
                        slot.fit.x,
                        slot.fit.y,
                        slot.w,
                        slot.h,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        info.buffer());
                    info.free();
                }
            }
        };
        if (mipmapLevel <= 0) {
            texture.setMipmap(mipmap);
        } else {
            texture.setMipmap((target, buffer) -> {
                mipmap.set(target, buffer);
                ITextureMipmap.DEFAULT.set(target, buffer);
            });
        }
        texture.loadEmpty(packer.root.w, packer.root.h);
    }

    /**
     * Load the atlas from a list.
     *
     * @param name     The asset name. Unused.
     * @param provider The file provider. Unused.
     * @param infoList The sprite info list.
     */
    @Override
    public void reload(String name, IFileProvider provider, UserPointer infoList) {
        load(Objects.requireNonNull(infoList));
    }

    private void checkMap() {
        if (slotMap == null)
            throw new NullPointerException("Atlas not loaded!");
    }

    public TextureParam extraParam() {
        return extraParam;
    }

    public void extraParam(TextureParam extraParam) {
        this.extraParam = extraParam;
    }

    public int getWidth(String spriteName) {
        checkMap();
        return slotMap.get(spriteName).w;
    }

    public int getHeight(String spriteName) {
        checkMap();
        return slotMap.get(spriteName).h;
    }

    public int getU0(String spriteName) {
        checkMap();
        return slotMap.get(spriteName).fit.x;
    }

    public int getV0(String spriteName) {
        checkMap();
        return slotMap.get(spriteName).fit.y;
    }

    public int getU1(String spriteName) {
        checkMap();
        var b = slotMap.get(spriteName);
        return b.fit.x + b.w;
    }

    public int getV1(String spriteName) {
        checkMap();
        var b = slotMap.get(spriteName);
        return b.fit.y + b.h;
    }

    public float getU0n(String spriteName) {
        return getU0(spriteName) / (float) getWidth();
    }

    public float getV0n(String spriteName) {
        return getV0(spriteName) / (float) getHeight();
    }

    public float getU1n(String spriteName) {
        return getU1(spriteName) / (float) getWidth();
    }

    public float getV1n(String spriteName) {
        return getV1(spriteName) / (float) getHeight();
    }

    public double getV1nd(String spriteName) {
        return getV1(spriteName) / (double) getHeight();
    }

    public double getU0nd(String spriteName) {
        return getU0(spriteName) / (double) getWidth();
    }

    public double getV0nd(String spriteName) {
        return getV0(spriteName) / (double) getHeight();
    }

    public double getU1nd(String spriteName) {
        return getU1(spriteName) / (double) getWidth();
    }

    public int maxMipmapLevel() {
        return maxMipmapLevel;
    }

    /**
     * Set the max mipmap level. {@code -1} to enable auto-detecting, {@code 0}
     * to disable mipmap.
     *
     * @param maxMipmapLevel the max mipmap level
     */
    public void maxMipmapLevel(int maxMipmapLevel) {
        this.maxMipmapLevel = maxMipmapLevel;
    }

    public int mipmapLevel() {
        return mipmapLevel;
    }

    @Override
    public void create() {
    }

    @Override
    public void bind() {
        texture.bind();
    }

    @Override
    public void unbind() {
        texture.unbind();
    }

    @Override
    public int getId() {
        return texture.getId();
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public void close() {
        texture.close();
    }
}
