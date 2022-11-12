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

import org.overrun.swgl.core.io.IFileProvider;

import java.util.HashMap;
import java.util.Map;

import static org.overrun.swgl.core.cfg.GlobalConfig.getDebugLogger;

/**
 * The asset manager.
 *
 * @author squid233
 * @since 0.1.0
 */
public class AssetManager implements AutoCloseable {
    private final Map<String, Asset<?>> assets = new HashMap<>();
    private boolean frozen;

    public Asset<?> addAsset(String name,
                             Asset<?> asset) {
        if (isFrozen())
            throw new IllegalStateException("Couldn't add asset in frozen state!");
        return assets.put(name, asset);
    }

    public Asset<?> disposeAsset(String name) throws Exception {
        var asset = assets.remove(name);
        if (asset != null) asset.close();
        return asset;
    }

    public boolean hasAsset(String name) {
        return assets.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <UserPointer, T extends Asset<UserPointer>>
    T getAsset(String name) {
        return (T) assets.get(name);
    }

    public <UserPointer, T extends Asset<UserPointer>>
    T loadAsset(String name,
                IFileProvider fileProvider,
                IAssetTypeProvider<UserPointer, T> typeProvider,
                UserPointer pointer) {
        if (isFrozen())
            throw new IllegalStateException("Couldn't load asset in frozen state!");
        T asset;
        try {
            asset = typeProvider.createInstance(name, fileProvider, pointer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create the asset instance", e);
        }
        assets.put(name, asset);
        return asset;
    }

    public <UserPointer, T extends Asset<UserPointer>>
    T loadAsset(String name,
                IFileProvider fileProvider,
                IAssetTypeProvider<UserPointer, T> typeProvider) {
        return loadAsset(name, fileProvider, typeProvider, null);
    }

    /**
     * Freeze this manager.
     *
     * @see #unfreeze()
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Unfreeze this manager.
     *
     * @see #freeze()
     */
    public void unfreeze() {
        frozen = false;
    }

    /**
     * Get the frozen state.
     *
     * @return The frozen state.
     */
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public void close() throws Exception {
        for (var v : assets.values()) {
            try {
                v.close();
            } catch (Exception e) {
                getDebugLogger().error("Error disposing assets", e);
            }
        }
    }
}
