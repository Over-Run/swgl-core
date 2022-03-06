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

import java.util.concurrent.atomic.AtomicReference;

import static org.overrun.swgl.core.util.math.Direction.*;

/**
 * The axis-aligned bounding box.
 *
 * @author squid233
 * @since 0.1.0
 */
public class AABB {
    public final Vector3f min = new Vector3f();
    public final Vector3f max = new Vector3f();

    public AABB() {
    }

    public AABB(Vector3fc min,
                Vector3fc max) {
        this.min.set(min);
        this.max.set(max);
    }

    public AABB(float minX,
                float minY,
                float minZ,
                float maxX,
                float maxY,
                float maxZ) {
        min.set(minX, minY, minZ);
        max.set(maxX, maxY, maxZ);
    }

    /**
     * The box consumer.
     *
     * @author squid233
     * @since 0.1.0
     */
    @FunctionalInterface
    public interface BoxConsumer {
        void accept(Direction dir,
                    float minX,
                    float minY,
                    float minZ,
                    float maxX,
                    float maxY,
                    float maxZ);
    }

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
        var result = new AtomicReference<>(SOUTH);
        var t = new AtomicReference<>(Float.POSITIVE_INFINITY);

        forEachFace((dir1, minX, minY, minZ, maxX, maxY, maxZ) -> {
            final float epsilon = 0.0001f;
            if (Intersectionf.intersectRayAab(origin.x(),
                origin.y(),
                origin.z(),
                dir.x(),
                dir.y(),
                dir.z(),
                minX - epsilon,
                minY - epsilon,
                minZ - epsilon,
                maxX + epsilon,
                maxY + epsilon,
                maxZ + epsilon,
                nf) && nf.x < t.get()) {
                t.set(nf.x);
                result.set(dir1);
            }
        });

        return result.get();
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
        var aabb = new AABB(fx0, fy0, fz0, fx1, fy1, fz1);
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
        var aabb = new AABB(fx0, fy0, fz0, fx1, fy1, fz1);
        aabb.fix();
        return aabb;
    }

    public void forEachEdge(BoxConsumer consumer) {
        // 12 edges

        // -x
        // [-x..-x], [-y..+y], [-z..-z]
        consumer.accept(WEST, min.x, min.y, min.z, min.x, max.y, min.z);
        // [-x..-x], [-y..+y], [+z..+z]
        consumer.accept(WEST, min.x, min.y, max.z, min.x, max.y, max.z);
        // [-x..-x], [-y..-y], [-z..+z]
        consumer.accept(WEST, min.x, min.y, min.z, min.x, min.y, max.z);
        // [-x..-x], [+y..+y], [-z..+z]
        consumer.accept(WEST, min.x, max.y, min.z, min.x, max.y, max.z);

        // +x
        // [+x..+x], [-y..+y], [-z..-z]
        consumer.accept(EAST, max.x, min.y, min.z, max.x, max.y, min.z);
        // [+x..+x], [-y..+y], [+z..+z]
        consumer.accept(EAST, max.x, min.y, max.z, max.x, max.y, max.z);
        // [+x..+x], [-y..-y], [-z..+z]
        consumer.accept(EAST, max.x, min.y, min.z, max.x, min.y, max.z);
        // [+x..+x], [+y..+y], [-z..+z]
        consumer.accept(EAST, max.x, max.y, min.z, max.x, max.y, max.z);

        // [-x..+x], [-y..-y], [-z..-z]
        consumer.accept(DOWN, min.x, min.y, min.z, max.x, min.y, min.z);
        // [-x..+x], [-y..-y], [+z..+z]
        consumer.accept(DOWN, min.x, min.y, max.z, max.x, min.y, max.z);
        // [-x..+x], [+y..+y], [+z..+z]
        consumer.accept(UP, min.x, max.y, max.z, max.x, max.y, max.z);
        // [-x..+x], [+y..+y], [-z..-z]
        consumer.accept(UP, min.x, max.y, min.z, max.x, max.y, min.z);
    }

    public void forEachFace(BoxConsumer consumer) {
        // 6 faces

        for (var dir : Direction.values()) {
            consumer.accept(dir,
                dir == EAST ? max.x : min.x,
                dir == UP ? max.y : min.y,
                dir == SOUTH ? max.z : min.z,
                dir == WEST ? min.x : max.x,
                dir == DOWN ? min.y : max.y,
                dir == NORTH ? min.z : max.z);
        }
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
