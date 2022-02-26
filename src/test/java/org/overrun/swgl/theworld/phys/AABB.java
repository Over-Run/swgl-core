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

package org.overrun.swgl.theworld.phys;

import org.joml.Vector3f;

/**
 * The axis-aligned box.
 *
 * @author squid233
 * @since 0.1.0
 */
public class AABB {
    public final Vector3f min = new Vector3f();
    public final Vector3f max = new Vector3f();

    public AABB expand(float x, float y, float z) {
        float fx0 = min.x;
        float fy0 = min.y;
        float fz0 = min.z;
        float fx1 = max.x;
        float fy1 = max.y;
        float fz1 = max.z;
        if (x < 0)
            fx0 += x;
        if (x > 0)
            fx1 += x;
        if (y < 0)
            fy0 += y;
        if (y > 0)
            fy1 += y;
        if (z < 0)
            fz0 += z;
        if (z > 0)
            fz1 += z;
        var aabb = new AABB();
        aabb.min.set(fx0, fy0, fz0);
        aabb.max.set(fx1, fy1, fz1);
        return aabb;
    }

    public float clipXCollide(AABB other, float dt) {
        // Pass
        if (other.max.y <= min.y || other.min.y >= max.y || other.max.z <= min.z || other.min.z >= max.z)
            return dt;
        float clip;
        if (dt > 0 && other.max.x <= min.x) {
            clip = min.x - other.max.x;
            if (clip < dt)
                dt = clip;
        }
        if (dt < 0 && other.min.x >= max.x) {
            clip = max.x - other.min.x;
            if (clip > dt)
                dt = clip;
        }
        return dt;
    }

    public float clipYCollide(AABB other, float dt) {
        // Pass
        if (other.max.x <= min.x || other.min.x >= max.x || other.max.z <= min.z || other.min.z >= max.z)
            return dt;
        float clip;
        if (dt > 0 && other.max.y <= min.y) {
            clip = min.y - other.max.y;
            if (clip < dt)
                dt = clip;
        }
        if (dt < 0 && other.min.y >= max.y) {
            clip = max.y - other.min.y;
            if (clip > dt)
                dt = clip;
        }
        return dt;
    }

    public float clipZCollide(AABB other, float dt) {
        // Pass
        if (other.max.x <= min.x || other.min.x >= max.x || other.max.y <= min.y || other.min.y >= max.y)
            return dt;
        float clip;
        if (dt > 0 && other.max.z <= min.z) {
            clip = min.z - other.max.z;
            if (clip < dt)
                dt = clip;
        }
        if (dt < 0 && other.min.z >= max.z) {
            clip = max.z - other.min.z;
            if (clip > dt)
                dt = clip;
        }
        return dt;
    }

    public void move(float x, float y, float z) {
        min.add(x, y, z);
        max.add(x, y, z);
    }
}
