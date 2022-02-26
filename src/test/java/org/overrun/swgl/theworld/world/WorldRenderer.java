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

package org.overrun.swgl.theworld.world;

import org.overrun.swgl.theworld.Frustum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author squid233
 * @since 0.1.0
 */
public class WorldRenderer implements AutoCloseable {
    public static final int MAX_REBUILD_PER_FRAMES = 8;
    public final World world;
    private final Chunk[] chunks;
    public final int xChunks;
    public final int yChunks;
    public final int zChunks;

    public WorldRenderer(World world) {
        this.world = world;
        xChunks = world.width / Chunk.CHUNK_SIZE;
        yChunks = world.height / Chunk.CHUNK_SIZE;
        zChunks = world.depth / Chunk.CHUNK_SIZE;
        chunks = new Chunk[xChunks * yChunks * zChunks];
        for (int x = 0; x < xChunks; x++) {
            for (int y = 0; y < yChunks; y++) {
                for (int z = 0; z < zChunks; z++) {
                    chunks[x + (y * zChunks + z) * xChunks] = new Chunk(world, x, y, z);
                }
            }
        }
    }

    private List<Chunk> getDirtyChunks() {
        List<Chunk> list = null;
        for (var chunk : chunks) {
            if (chunk.isDirty()) {
                if (list == null)
                    list = new ArrayList<>();
                list.add(chunk);
            }
        }
        return list;
    }

    public void updateDirtyChunks() {
        var list = getDirtyChunks();
        if (list != null) {
            list.sort(new DirtyChunkSorter(Frustum.getInstance()));
            for (int i = 0; i < MAX_REBUILD_PER_FRAMES && i < list.size(); i++) {
                list.get(i).rebuild();
            }
        }
    }

    public void render(Frustum frustum) {
        for (var chunk : chunks) {
            if (frustum.testAab(chunk.aabb)) {
                chunk.render();
            }
        }
    }

    @Override
    public void close() {
        for (var chunk : chunks) {
            chunk.close();
        }
    }
}
