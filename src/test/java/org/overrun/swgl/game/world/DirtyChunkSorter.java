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

package org.overrun.swgl.game.world;

import org.overrun.swgl.core.util.timing.Timer;
import org.overrun.swgl.game.Frustum;
import org.overrun.swgl.game.world.entity.PlayerEntity;

import java.util.Comparator;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class DirtyChunkSorter implements Comparator<Chunk> {
    private final PlayerEntity player;
    private final Frustum frustum;
    private final boolean transparency;
    private final double now = Timer.getTime();

    public DirtyChunkSorter(PlayerEntity player,
                            Frustum frustum,
                            boolean transparency) {
        this.player = player;
        this.frustum = frustum;
        this.transparency = transparency;
    }

    @Override
    public int compare(Chunk o1, Chunk o2) {
        boolean visible1 = frustum.testAab(o1.aabb);
        boolean visible2 = frustum.testAab(o2.aabb);
        if (visible1 && !visible2) return transparency ? 1 : -1;
        if (!visible1 && visible2) return transparency ? -1 : 1;
        double t1 = (now - (transparency ? o1.trspDirtiedTime : o1.dirtiedTime)) * 0.5;
        double t2 = (now - (transparency ? o2.trspDirtiedTime : o2.dirtiedTime)) * 0.5;
        if (t1 < t2) return transparency ? 1 : -1;
        if (t1 > t2) return transparency ? -1 : 1;
        float d1 = o1.distanceSqr(player);
        float d2 = o2.distanceSqr(player);
        return transparency ? Float.compare(d2, d1) : Float.compare(d1, d2);
    }
}
