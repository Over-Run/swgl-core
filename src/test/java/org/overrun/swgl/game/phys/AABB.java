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

package org.overrun.swgl.game.phys;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.overrun.swgl.core.util.math.Direction;

/**
 * The axis-aligned box.
 *
 * @author squid233
 * @since 0.1.0
 */
public class AABB {
    public final Vector3f min = new Vector3f();
    public final Vector3f max = new Vector3f();

    public boolean isValid() {
        return min.x < max.x && min.y < max.y && min.z < max.z;
    }

    public void fix() {
        if (min.x > max.x) {
            float mx = min.x;
            min.x = max.x;
            max.x = mx;
        }
        if (min.y > max.y) {
            float my = min.y;
            min.y = max.y;
            max.y = my;
        }
        if (min.z > max.z) {
            float mz = min.z;
            min.z = max.z;
            max.z = mz;
        }
    }

    public Vector3f closestX(Vector3f origin) {
        fix();
        var result = new Vector3f(origin);
        if (origin.x > max.x)
            return result.setComponent(0, max.x);
        if (origin.x < min.x)
            return result.setComponent(0, min.x);
        return result.setComponent(0, origin.x);
    }

    public Vector3f closestY(Vector3f origin) {
        fix();
        var result = new Vector3f(origin);
        if (origin.y > max.y)
            return result.setComponent(1, max.y);
        if (origin.y < min.y)
            return result.setComponent(1, min.y);
        return result.setComponent(1, origin.y);
    }

    public Vector3f closestZ(Vector3f origin) {
        fix();
        var result = new Vector3f(origin);
        if (origin.z > max.z)
            return result.setComponent(2, max.z);
        if (origin.z < min.z)
            return result.setComponent(2, min.z);
        return result.setComponent(2, origin.z);
    }

    public Vector3f closest(Vector3f origin) {
        fix();
        var result = new Vector3f(origin);

        if (origin.x > max.x)
            result.x = max.x;
        else result.x = Math.max(origin.x, min.x);
        if (origin.y > max.y)
            result.y = max.y;
        else result.y = Math.max(origin.y, min.y);
        if (origin.z > max.z)
            result.z = max.z;
        else result.z = Math.max(origin.z, min.z);

        return result;
    }

    public Direction rayCastFacing(Vector3fc origin, Vector3fc dir) {
        var nf = new Vector2f();
        var result = Direction.SOUTH;
        float t = Float.POSITIVE_INFINITY;

        for (var face : Direction.values()) {
            final float epsilon = 0.0001f;
            if (Intersectionf.intersectRayAab(origin.x(),
                origin.y(),
                origin.z(),
                dir.x(),
                dir.y(),
                dir.z(),
                /* very ugly code! */
                (face == Direction.WEST ? min.x - epsilon : face == Direction.EAST ? max.x : min.x),
                (face == Direction.DOWN ? min.y - epsilon : face == Direction.UP ? max.y : min.y),
                (face == Direction.NORTH ? min.z - epsilon : face == Direction.SOUTH ? max.z : min.z),
                (face == Direction.WEST ? min.x : face == Direction.EAST ? max.x + epsilon : max.x),
                (face == Direction.DOWN ? min.y : face == Direction.UP ? max.y + epsilon : max.y),
                (face == Direction.NORTH ? min.z : face == Direction.SOUTH ? max.z + epsilon : max.z),
                nf
            ) && nf.x < t) {
                t = nf.x;
                result = face;
            }
        }

        return result;
    }

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
        aabb.fix();
        return aabb;
    }

    public AABB grow(float x, float y, float z) {
        float fx0 = min.x - x;
        float fy0 = min.y - y;
        float fz0 = min.z - z;
        float fx1 = max.x + x;
        float fy1 = max.y + y;
        float fz1 = max.z + z;
        var aabb = new AABB();
        aabb.min.set(fx0, fy0, fz0);
        aabb.max.set(fx1, fy1, fz1);
        aabb.fix();
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
        fix();
    }
}
