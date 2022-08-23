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

package org.overrun.swgl.core.phys.p3d;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * The 3d axis-aligned bounding box.
 *
 * @author squid233
 * @since 0.2.0
 */
public class AABBox3f {
    private float minX, minY, minZ, maxX, maxY, maxZ;
    private boolean fixed = false;

    public AABBox3f() {
    }

    public AABBox3f(float minX, float minY, float minZ,
                    float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public AABBox3f(Vector3fc min, Vector3fc max) {
        this(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public AABBox3f(AABBox3f other) {
        minX = other.minX;
        minY = other.minY;
        minZ = other.minZ;
        maxX = other.maxX;
        maxY = other.maxY;
        maxZ = other.maxZ;
        fixed = other.fixed;
    }

    /**
     * Create a new box with the size and position.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param w the width
     * @param h the height
     * @param d the depth
     * @return the new box
     */
    public static AABBox3f ofSize(float x, float y, float z,
                                  float w, float h, float d) {
        return new AABBox3f(x, y, z, w, h, d);
    }

    /**
     * Create a new box with the size and position.
     *
     * @param position the position
     * @param size     the size
     * @return the new box
     */
    public static AABBox3f ofSize(Vector3fc position,
                                  Vector3fc size) {
        return ofSize(position.x(), position.y(), position.z(), size.x(), size.y(), size.z());
    }

    /**
     * Create a new box with the extents and position.<br>
     * The position is the center of the box.<br>
     * The extents are half the size of the box.<br>
     * The extents are not guaranteed to be positive.
     *
     * @param x       the x position
     * @param y       the y position
     * @param z       the z position
     * @param extentX the x extent
     * @param extentY the y extent
     * @param extentZ the z extent
     * @return the new rect
     */
    public static AABBox3f ofExtent(float x, float y, float z,
                                    float extentX, float extentY, float extentZ) {
        return new AABBox3f(x - extentX, y - extentY, z - extentZ,
            x + extentX, y + extentY, z + extentZ);
    }

    /**
     * Create a new box with the extents and position.<br>
     * The position is the center of the box.<br>
     * The extents are half the size of the box.<br>
     * The extents are not guaranteed to be positive.
     *
     * @param position the position
     * @param extents  the extents
     * @return the new rect
     */
    public static AABBox3f ofExtent(Vector3fc position, Vector3fc extents) {
        return ofExtent(position.x(), position.y(), position.z(),
            extents.x(), extents.y(), extents.z());
    }

    private AABBox3f forceFix() {
        fixed = true;
        if (minX > maxX) {
            float f = minX;
            minX = maxX;
            maxX = f;
        }
        if (minY > maxY) {
            float f = minY;
            minY = maxY;
            maxY = f;
        }
        if (minZ > maxZ) {
            float f = minZ;
            minZ = maxZ;
            maxZ = f;
        }
        return this;
    }

    public AABBox3f tryFix() {
        if (fixed)
            return this;
        return forceFix();
    }

    /**
     * Check if this is intersected with {@code b}.
     *
     * @param b the other rect
     * @return is intersected
     */
    public boolean test(AABBox3f b) {
        tryFix();
        return Intersectionf.testAabAab(minX, minY, minZ,
            maxX, maxY, maxZ,
            b.minX, b.minY, b.minZ,
            b.maxX, b.maxY, b.maxZ);
    }

    //test(BSphere2f)

    /**
     * Check if this is intersected with specified position.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return is intersected
     */
    public boolean test(float x, float y, float z) {
        tryFix();
        return x >= minX && x <= maxX &&
               y >= minY && y <= maxY &&
               z >= minZ && z <= maxZ;
    }

    /**
     * Check if this is intersected with specified position.
     *
     * @param position the position
     * @return is intersected
     */
    public boolean test(Vector3fc position) {
        return test(position.x(), position.y(), position.z());
    }

    /**
     * Clip the movement x from this box.
     * This box is the moving object and {@code b} is tester (static object).
     *
     * @param dx the movement x
     * @param b  the other box
     * @return the clipped movement
     */
    public float clipXCollide(float dx, AABBox3f b) {
        tryFix();
        if (maxY <= b.minY || minY >= b.maxY ||
            maxZ <= b.minZ || minZ >= b.maxZ)
            return dx;
        if (dx > 0.0f) {
            if (maxX <= b.minX) {
                float clip = b.minX - maxX;
                return Math.min(clip, dx);
            }
            return dx;
        }
        if (dx < 0.0f) {
            if (minX >= b.maxX) {
                float clip = b.maxX - minX;
                return Math.max(clip, dx);
            }
            return dx;
        }
        return 0.0f;
    }

    /**
     * Clip the movement y from this box.
     * This box is the moving object and {@code b} is tester (static object).
     *
     * @param dy the movement y
     * @param b  the other box
     * @return the clipped movement
     */
    public float clipYCollide(float dy, AABBox3f b) {
        tryFix();
        if (maxX <= b.minX || minX >= b.maxX ||
            maxZ <= b.minZ || minZ >= b.maxZ)
            return dy;
        if (dy > 0.0f) {
            if (maxY <= b.minY) {
                float clip = b.minY - maxY;
                return Math.min(clip, dy);
            }
            return dy;
        }
        if (dy < 0.0f) {
            if (minY >= b.maxY) {
                float clip = b.maxY - minY;
                return Math.max(clip, dy);
            }
            return dy;
        }
        return 0.0f;
    }

    /**
     * Clip the movement z from this box.
     * This box is the moving object and {@code b} is tester (static object).
     *
     * @param dz the movement z
     * @param b  the other box
     * @return the clipped movement
     */
    public float clipZCollide(float dz, AABBox3f b) {
        tryFix();
        if (maxX <= b.minX || minX >= b.maxX ||
            maxY <= b.minY || minY >= b.maxY)
            return dz;
        if (dz > 0.0f) {
            if (maxZ <= b.minZ) {
                float clip = b.minZ - maxZ;
                return Math.min(clip, dz);
            }
            return dz;
        }
        if (dz < 0.0f) {
            if (minZ >= b.maxZ) {
                float clip = b.maxZ - minZ;
                return Math.max(clip, dz);
            }
            return dz;
        }
        return 0.0f;
    }

    public AABBox3f move(float x, float y, float z, AABBox3f dst) {
        return dst.set(minX + x, minY + y, minZ + z,
            maxX + x, maxY + y, maxZ + z);
    }

    public AABBox3f move(float x, float y, float z) {
        return move(x, y, z, this);
    }

    public AABBox3f expand(float x, float y, float z, AABBox3f dst) {
        float fx0 = minX;
        float fy0 = minY;
        float fz0 = minZ;
        float fx1 = maxX;
        float fy1 = maxY;
        float fz1 = maxZ;
        if (x < 0.0f)
            fx0 += x;
        if (x > 0.0f)
            fx1 += x;
        if (y < 0.0f)
            fy0 += y;
        if (y > 0.0f)
            fy1 += y;
        if (z < 0.0f)
            fz0 += z;
        if (z > 0.0f)
            fz1 += z;
        return dst.set(fx0, fy0, fz0, fx1, fy1, fz1);
    }

    public AABBox3f expand(float x, float y, float z) {
        return expand(x, y, z, this);
    }

    public AABBox3f grow(float x, float y, float z, AABBox3f dst) {
        float fx0 = minX - x;
        float fy0 = minY - y;
        float fz0 = minZ - z;
        float fx1 = maxX + x;
        float fy1 = maxY + y;
        float fz1 = maxZ + z;
        return dst.set(fx0, fy0, fz0, fx1, fy1, fz1);
    }

    public AABBox3f grow(float x, float y, float z) {
        return grow(x, y, z, this);
    }

    public AABBox3f setMin(Vector3fc min) {
        return setMin(min.x(), min.y(), min.z());
    }

    public AABBox3f setMin(float x, float y, float z) {
        minX = x;
        minY = y;
        minZ = z;
        return forceFix();
    }

    public AABBox3f setMax(Vector3fc max) {
        return setMax(max.x(), max.y(), max.z());
    }

    public AABBox3f setMax(float x, float y, float z) {
        maxX = x;
        maxY = y;
        maxZ = z;
        return forceFix();
    }

    public AABBox3f set(float minX, float minY, float minZ,
                        float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return forceFix();
    }

    public AABBox3f set(Vector3fc min, Vector3fc max) {
        return set(min.x(), min.y(), min.x(),
            max.x(), max.y(), max.z());
    }

    /**
     * Set this rect to the other rect.
     *
     * @param b the other rect
     * @return this
     */
    public AABBox3f set(AABBox3f b) {
        return set(b.minX, b.minY, b.minZ,
            b.maxX, b.maxY, b.maxZ);
    }

    public Vector3f getCenter(Vector3f dst) {
        tryFix();
        return dst.set((maxX - minX) * 0.5f + minX,
            (maxY - minY) * 0.5f + minY,
            (maxZ - minZ) * 0.5f + minZ);
    }

    public Vector3f getExtent(Vector3f dst) {
        tryFix();
        return dst.set((maxX - minX) * 0.5f,
            (maxY - minY) * 0.5f,
            (maxZ - minZ) * 0.5f);
    }

    public float minX() {
        return minX;
    }

    public float minY() {
        return minY;
    }

    public float maxX() {
        return maxX;
    }

    public float maxY() {
        return maxY;
    }

    public float minZ() {
        return minZ;
    }

    public float maxZ() {
        return maxZ;
    }
}
