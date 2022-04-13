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

import org.joml.SimplexNoise;

/**
 * @author squid233
 * @since 0.1.0
 */
public class SimpxNoiseTerrain {
    public static float fbm(
        int octaves,
        float x,
        float y,
        float z,
        float persistence,
        float scale,
        float low,
        float high
    ) {
        float maxAmp = 0;
        float amp = 1;
        float freq = scale;
        float noise = 0;

        // add successively smaller, higher-frequency terms
        for (int i = 0; i < octaves; i++) {
            noise += SimplexNoise.noise(x * freq, y, z * freq) * amp;
            maxAmp += amp;
            amp *= persistence;
            freq *= 2;
        }

        // take the average value of the iterations
        noise /= maxAmp;

        // normalize the result
        noise = (noise * (high - low) + (high + low)) * 0.5f;

        return noise;
    }

    public static int[] generateTerrain(
        float t,
        int w,
        int h,
        int d,
        int[] outMaxResult
    ) {
        final int[] map = new int[w * d];
        final float scale = 2.0f / (w + d);
        int maxResult = 0;
        for (int x = 0; x < w; x++) {
            for (int z = 0; z < d; z++) {
                int i = (int) fbm(16, x, t, z, 0.5f, scale, 0, h);
                map[x + z * w] = i;
                if (i > maxResult)
                    maxResult = i;
            }
        }
        if (outMaxResult != null)
            outMaxResult[0] = maxResult;
        return map;
    }
}
