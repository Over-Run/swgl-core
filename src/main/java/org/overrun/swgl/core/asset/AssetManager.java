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

        public boolean createAsset(String name) {
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
            if (consumer != null)
                consumer.accept(asset);
            asset.reload(name, provider);
            return true;
        }
    }

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

    public <T extends Asset>
    void createAsset(String name,
                     Class<T> type,
                     IFileProvider provider) {
        createAsset(name, type, null, provider);
    }

    public void addAliases(String name, String... aliases) {
        this.aliases.put(name, aliases);
        for (var alias : aliases) {
            biAliases.put(alias, name);
        }
    }

    public void reloadAssets(boolean force) {
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
                while (!(ok = wrapper.createAsset(key)) && i < aliasArr.length) {
                    key = aliasArr[i];
                    ++i;
                }
                if (!ok) {
                    GlobalConfig.getDebugStream().println("Failed to load asset '" + nm + "' from any aliases.");
                }
            }
        }
    }

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

    public void freeze() {
        frozen = true;
    }

    public void thaw() {
        frozen = false;
    }

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
