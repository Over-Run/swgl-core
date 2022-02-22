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
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.io.IFileProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The asset manager.
 *
 * @author squid233
 * @since 0.1.0
 */
public class AssetManager implements AutoCloseable {
    private final Map<String, Map<Class<?>, AssetWrapper>> assets = new HashMap<>();
    private final Map<String, String[]> aliases = new HashMap<>();
    private final Map<String, String> biAliases = new HashMap<>();
    private boolean frozen;

    private static final class AssetWrapper {
        private final String name;
        private final Class<?> type;
        private Asset asset;
        private final Consumer<Asset> consumer;
        private final IFileProvider provider;
        private boolean reloaded;

        private AssetWrapper(String name,
                             Class<?> type,
                             Consumer<Asset> consumer,
                             IFileProvider provider) {
            this.name = name;
            this.type = type;
            this.consumer = consumer;
            this.provider = provider;
        }

        public Asset asset() {
            return asset;
        }

        public boolean createAsset(String name,
                                   boolean forceReload) {
            if (!reloaded)
                // Construct from constructor without any parameters
                try {
                    asset = (Asset) type.getDeclaredConstructor().newInstance();
                } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                    return false;
                }
            // If not reloaded or reloaded but force reloads
            if (!reloaded || forceReload) {
                if (consumer != null)
                    consumer.accept(asset);
                asset.reload(name, provider);
                reloaded = true;
            }
            return true;
        }
    }

    /**
     * Create an asset. Not loaded.
     *
     * @param name     The asset original name.
     * @param type     The asset type class.
     * @param consumer The consumer to be accepted before loading asset.
     * @param provider The file provider.
     * @param <T>      The asset type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Asset>
    void createAsset(String name,
                     Class<T> type,
                     @Nullable Consumer<T> consumer,
                     IFileProvider provider) {
        if (isFrozen())
            throw new IllegalStateException("Couldn't create asset in frozen state!");
        var map = new HashMap<Class<?>, AssetWrapper>();
        map.put(type, new AssetWrapper(name, type, (Consumer<Asset>) consumer, provider));
        assets.put(name, map);
    }

    /**
     * Create an asset. Not loaded.
     *
     * @param name     The asset original name.
     * @param type     The asset type class.
     * @param provider The file provider.
     * @param <T>      The asset type.
     */
    public <T extends Asset>
    void createAsset(String name,
                     Class<T> type,
                     IFileProvider provider) {
        createAsset(name, type, null, provider);
    }

    /**
     * Add aliases to the specified name.
     *
     * @param name    The original name.
     * @param aliases The aliases.
     */
    public void addAliases(String name, String... aliases) {
        this.aliases.put(name, aliases);
        for (var alias : aliases) {
            biAliases.put(alias, name);
        }
    }

    /**
     * Reload all created assets.
     *
     * @param force         Force reloads in frozen state if true.
     * @param forcePerAsset Force reloads per assets if true.
     * @see #reloadAssets(boolean)
     */
    public void reloadAssets(boolean force, boolean forcePerAsset) {
        if (isFrozen() && !force)
            throw new IllegalStateException("Couldn't reload asset in frozen state!");
        for (var map : assets.entrySet()) {
            for (var e : map.getValue().entrySet()) {
                var wrapper = e.getValue();
                var nm = map.getKey();
                var key = nm;
                var aliasArr = aliases.get(nm);
                int i = 0;
                boolean ok;
                // If load failed, load from aliases.
                while (!(ok = wrapper.createAsset(key, forcePerAsset)) && i < aliasArr.length) {
                    key = aliasArr[i];
                    ++i;
                }
                if (!ok) {
                    GlobalConfig.getDebugStream().println("Failed to load asset '" + nm + "' from any aliases.");
                }
            }
        }
    }

    /**
     * Force reloads all created assets.
     *
     * @param force Force reloads in frozen state if true.
     * @see #reloadAssets(boolean, boolean)
     */
    public void reloadAssets(boolean force) {
        reloadAssets(force, true);
    }

    /**
     * Get the asset from created assets.
     *
     * @param name The asset name or alias.
     * @param type The asset type class.
     * @param <T>  The asset type.
     * @return The asset.
     * @throws RuntimeException If asset not found.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Asset> T getAsset(String name,
                                        Class<T> type)
        throws RuntimeException {
        var map = assets.get(name);
        // Check if asset present, else it is an alias
        if (map == null) {
            var nm = biAliases.get(name);
            if (nm == null)
                throw new RuntimeException("Couldn't get original name from '" + name + "'!");
            if ((map = assets.get(name)) == null)
                throw new RuntimeException("Couldn't get asset map from alias name '" + name
                    + "' and original name '" + nm + "'!");
        }
        return (T) map.get(type).asset();
    }

    /**
     * Freeze this manager.
     *
     * @see #thaw()
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Thaw this manager.
     *
     * @see #freeze()
     */
    public void thaw() {
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
            for (var a : v.values()) {
                a.asset().close();
            }
        }
    }
}