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
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.overrun.swgl.core.util.math.Direction;

import static org.overrun.swgl.core.util.math.Direction.*;

/**
 * The axis-aligned bounding box.
 *
 * @author squid233
 * @since 0.1.0
 */
public class AABB {
    private static final RayCastResult rayCastResult = new RayCastResult();
    public final Vector3f min = new Vector3f();
    public final Vector3f max = new Vector3f();

    public AABB() {
    }

    public AABB(Vector3fc min,
                Vector3fc max) {
        this.min.set(min);
        this.max.set(max);
        fix();
    }

    public AABB(float minX,
                float minY,
                float minZ,
                float maxX,
                float maxY,
                float maxZ) {
        min.set(minX, minY, minZ);
        max.set(maxX, maxY, maxZ);
        fix();
    }

    /**
     * The box consumer.
     *
     * @author squid233
     * @since 0.1.0
     */
    @FunctionalInterface
    public interface BoxConsumer {
        boolean accept(Direction dir,
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

    public Direction intersect(float originX,
                               float originY,
                               float originZ,
                               float dirX,
                               float dirY,
                               float dirZ) {
        float invDirX = 1.0f / dirX;
        float invDirY = 1.0f / dirY;
        float invDirZ = 1.0f / dirZ;
        float t1 = (min.x() - originX) * invDirX;
        float t2 = (max.x() - originX) * invDirX;
        float t3 = (min.y() - originY) * invDirY;
        float t4 = (max.y() - originY) * invDirY;
        float t5 = (min.z() - originZ) * invDirZ;
        float t6 = (max.z() - originZ) * invDirZ;
        float tminx = Math.min(t1, t2);
        float tminy = Math.min(t3, t4);
        float tminz = Math.min(t5, t6);
        float tmaxx = Math.max(t1, t2);
        float tmaxy = Math.max(t3, t4);
        float tmaxz = Math.max(t5, t6);
        float tmin = Math.max(Math.max(tminx, tminy), tminz);
        float tmax = Math.min(Math.min(tmaxx, tmaxy), tmaxz);
        Direction dir = null;
        if (tmin == tminx) dir = WEST;
        else if (tmin == tminy) dir = DOWN;
        else if (tmin == tminz) dir = NORTH;
        else if (tmax == tmaxx) dir = EAST;
        else if (tmax == tmaxy) dir = UP;
        else if (tmax == tmaxz) dir = SOUTH;
        return dir;
//        // r.dir is unit direction vector of ray
//        float dirfracX = 1.0f / dirX;
//        float dirfracY = 1.0f / dirY;
//        float dirfracZ = 1.0f / dirZ;
//        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
//        // r.org is origin of ray
//        float t1 = (min.x() - originX) * dirfracX;
//        float t2 = (max.x() - originX) * dirfracX;
//        float t3 = (min.y() - originY) * dirfracY;
//        float t4 = (max.y() - originY) * dirfracY;
//        float t5 = (min.z() - originZ) * dirfracZ;
//        float t6 = (max.z() - originZ) * dirfracZ;
//
//        float tminx = Math.min(t1, t2);
//        float tminy = Math.min(t3, t4);
//        float tmin = Math.max(Math.max(tminx, tminy), Math.min(t5, t6));
//        int axis = 2;
//        if (Numbers.isEqual(tmin , tminx)) axis = 0;
//        if (Numbers.isEqual(tmin , tminy)) axis = 1;
//        System.out.println("axis = " + axis);
//        System.out.println("direction = " + ((axis == 0) ? (dirX > 0 ? WEST : EAST) : ((axis == 1) ? (dirY > 0 ? DOWN : UP) : (dirZ > 0 ? NORTH : SOUTH))));
//        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
//        float t;
//
//        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
//        if (tmax < 0) {
//            t = tmax;
//            return -1.0f;
//        }
//
//        // if tmin > tmax, ray doesn't intersect AABB
//        if (tmin > tmax) {
//            t = tmax;
//            return -1.0f;
//        }
//
//        t = tmin;
//        return t;
    }

    public float computePlane(Vector3fc a, Vector3fc b, Vector3fc c,
                              Vector3f normal) {
        float x = b.x() - a.x();
        float y = b.y() - a.y();
        float z = b.z() - a.z();
        float vx = c.x() - a.x();
        float vy = c.y() - a.y();
        float vz = c.z() - a.z();
        float rx = Math.fma(y, vz, -z * vy);
        float ry = Math.fma(z, vx, -x * vz);
        float rz = Math.fma(x, vy, -y * vx);
        float scalar = Math.invsqrt(Math.fma(rx, rx, Math.fma(ry, ry, rz * rz)));
        return computePlane(a, normal.set(rx * scalar, ry * scalar, rz * scalar));
    }

    public float computePlane(Vector3fc a, Vector3fc normal) {
        return Math.fma(normal.x(), a.x(), Math.fma(normal.y(), a.y(), normal.z() * a.z()));
    }

    public Direction rayCastFacing(Vector3fc origin, Vector3fc dir) {
        rayCastResult.reset();
        final float epsilon = 0.0001f;
        forEachFace((dir1, minX, minY, minZ, maxX, maxY, maxZ) -> {
            if (Intersectionf.intersectRayAab(origin.x(), origin.y(), origin.z(),
                dir.x(), dir.y(), dir.z(),
                minX - epsilon, minY - epsilon, minZ - epsilon,
                maxX + epsilon, maxY + epsilon, maxZ + epsilon,
                rayCastResult.nearFar) && rayCastResult.nearFar.x < rayCastResult.distance) {
                rayCastResult.distance = rayCastResult.nearFar.x;
                rayCastResult.direction = dir1;
            }
            return true;
        });
        return rayCastResult.direction;
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
        boolean c;
        // 12 edges

        // -x
        // [-x..-x], [-y..+y], [-z..-z]
        c = consumer.accept(WEST, min.x, min.y, min.z, min.x, max.y, min.z);
        if (!c) return;
        // [-x..-x], [-y..+y], [+z..+z]
        c = consumer.accept(WEST, min.x, min.y, max.z, min.x, max.y, max.z);
        if (!c) return;
        // [-x..-x], [-y..-y], [-z..+z]
        c = consumer.accept(WEST, min.x, min.y, min.z, min.x, min.y, max.z);
        if (!c) return;
        // [-x..-x], [+y..+y], [-z..+z]
        c = consumer.accept(WEST, min.x, max.y, min.z, min.x, max.y, max.z);
        if (!c) return;

        // +x
        // [+x..+x], [-y..+y], [-z..-z]
        c = consumer.accept(EAST, max.x, min.y, min.z, max.x, max.y, min.z);
        if (!c) return;
        // [+x..+x], [-y..+y], [+z..+z]
        c = consumer.accept(EAST, max.x, min.y, max.z, max.x, max.y, max.z);
        if (!c) return;
        // [+x..+x], [-y..-y], [-z..+z]
        c = consumer.accept(EAST, max.x, min.y, min.z, max.x, min.y, max.z);
        if (!c) return;
        // [+x..+x], [+y..+y], [-z..+z]
        c = consumer.accept(EAST, max.x, max.y, min.z, max.x, max.y, max.z);
        if (!c) return;

        // [-x..+x], [-y..-y], [-z..-z]
        c = consumer.accept(DOWN, min.x, min.y, min.z, max.x, min.y, min.z);
        if (!c) return;
        // [-x..+x], [-y..-y], [+z..+z]
        c = consumer.accept(DOWN, min.x, min.y, max.z, max.x, min.y, max.z);
        if (!c) return;
        // [-x..+x], [+y..+y], [+z..+z]
        c = consumer.accept(UP, min.x, max.y, max.z, max.x, max.y, max.z);
        if (!c) return;
        // [-x..+x], [+y..+y], [-z..-z]
        consumer.accept(UP, min.x, max.y, min.z, max.x, max.y, min.z);
    }

    public void forEachFace(BoxConsumer consumer) {
        // 6 faces

        for (var dir : Direction.values()) {
            boolean c = consumer.accept(dir,
                dir == EAST ? max.x : min.x,
                dir == UP ? max.y : min.y,
                dir == SOUTH ? max.z : min.z,
                dir == WEST ? min.x : max.x,
                dir == DOWN ? min.y : max.y,
                dir == NORTH ? min.z : max.z);
            if (!c) break;
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
