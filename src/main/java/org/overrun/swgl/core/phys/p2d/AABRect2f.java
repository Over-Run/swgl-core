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

package org.overrun.swgl.core.phys.p2d;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.overrun.swgl.core.util.math.Direction;

/**
 * The 2d axis-aligned bounding rect.
 *
 * @author squid233
 * @since 0.2.0
 */
public class AABRect2f {
    private float minX, minY, maxX, maxY;
    private boolean fixed = false;

    public AABRect2f() {
    }

    public AABRect2f(float minX, float minY, float maxX, float maxY) {
        set(minX, minY, maxX, maxY);
    }

    public AABRect2f(Vector2fc min, Vector2fc max) {
        this(min.x(), min.y(), max.x(), max.y());
    }

    /**
     * Copy the other rect.
     *
     * @param other the other rect
     */
    public AABRect2f(AABRect2f other) {
        minX = other.minX;
        minY = other.minY;
        maxX = other.maxX;
        maxY = other.maxY;
        fixed = other.fixed;
    }

    /**
     * Create a new rect with the size and position.
     *
     * @param x the x position
     * @param y the y position
     * @param w the width
     * @param h the height
     * @return the new rect
     */
    public static AABRect2f ofSize(float x, float y, float w, float h) {
        return new AABRect2f(x, y, x + w, y + h);
    }

    /**
     * Create a new rect with the size and position.
     *
     * @param position the position
     * @param size     the size
     * @return the new rect
     */
    public static AABRect2f ofSize(Vector2fc position, Vector2fc size) {
        return ofSize(position.x(), position.y(), size.x(), size.y());
    }

    /**
     * Create a new rect with the extents and position.
     * The position is the center of the rect.
     * The extents are half the size of the rect.
     * The extents are not guaranteed to be positive.
     *
     * @param x       the x position
     * @param y       the y position
     * @param extentX the x extent
     * @param extentY the y extent
     * @return the new rect
     */
    public static AABRect2f ofExtent(float x, float y, float extentX, float extentY) {
        return new AABRect2f(x - extentX, y - extentY, x + extentX, y + extentY);
    }

    /**
     * Create a new rect with the extents and position.
     * The position is the center of the rect.
     * The extents are half the size of the rect.
     * The extents are not guaranteed to be positive.
     *
     * @param position the position
     * @param extents  the extents
     * @return the new rect
     */
    public static AABRect2f ofExtent(Vector2fc position, Vector2fc extents) {
        return ofExtent(position.x(), position.y(), extents.x(), extents.y());
    }

    /**
     * The point consumer.
     *
     * @author squid233
     * @since 0.2.0
     */
    @FunctionalInterface
    public interface PointConsumer {
        /**
         * Accepts the point.
         *
         * @param x        The x.
         * @param y        The y.
         * @param quadrant The point quadrant.
         */
        void accept(float x, float y,
                    int quadrant);
    }

    /**
     * The edge consumer.
     *
     * @author squid233
     * @since 0.2.0
     */
    @FunctionalInterface
    public interface EdgeConsumer {
        /**
         * Accepts the edge.
         *
         * @param direction The direction.
         * @param minX      The min x.
         * @param minY      The min y.
         * @param maxX      The max x.
         * @param maxY      The max y.
         */
        void accept(Direction direction,
                    float minX, float minY,
                    float maxX, float maxY);
    }

    private AABRect2f forceFix() {
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
        return this;
    }

    public AABRect2f tryFix() {
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
    public boolean test(AABRect2f b) {
        tryFix();
        return Intersectionf.testAarAar(minX, minY, maxX, maxY, b.minX, b.minY, b.maxX, b.maxY);
    }

    /**
     * Check if this is intersected with {@code b}.
     *
     * @param b the other circle
     * @return is intersected
     */
    public boolean test(BCircle2f b) {
        return Intersectionf.testAarCircle(minX, minY,
            maxX, maxY,
            b.x(), b.y(), b.r() * b.r());
    }

    /**
     * Check if this is intersected with specified position.
     *
     * @param x the x position
     * @param y the y position
     * @return is intersected
     */
    public boolean test(float x, float y) {
        tryFix();
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    /**
     * Check if this is intersected with specified position.
     *
     * @param position the position
     * @return is intersected
     */
    public boolean test(Vector2fc position) {
        return test(position.x(), position.y());
    }

    /**
     * Clip the movement x from this rect.
     * This rect is the moving object and {@code b} is tester (static object).
     *
     * @param dx the movement x
     * @param b  the other rect
     * @return the clipped movement
     */
    public float clipXCollide(float dx, AABRect2f b) {
        tryFix();
        if (maxY <= b.minY || minY >= b.maxY)
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
     * Clip the movement y from this rect.
     * This rect is the moving object and {@code b} is tester (static object).
     *
     * @param dy the movement y
     * @param b  the other rect
     * @return the clipped movement
     */
    public float clipYCollide(float dy, AABRect2f b) {
        tryFix();
        if (maxX <= b.minX || minX >= b.maxX)
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
     * Performs the given action for each point.
     *
     * @param action the action
     */
    public void forEachPoint(PointConsumer action) {
        action.accept(maxX, maxY, 1);
        action.accept(minX, maxY, 2);
        action.accept(minX, minY, 3);
        action.accept(maxX, minY, 4);
    }

    /**
     * Performs the given action for each edge.
     *
     * @param action the action
     */
    public void forEachEdge(EdgeConsumer action) {
        action.accept(Direction.UP, minX, maxY, maxX, maxY);
        action.accept(Direction.DOWN, minX, minY, maxX, minY);
        action.accept(Direction.WEST, minX, minY, minX, maxY);
        action.accept(Direction.EAST, maxX, minY, maxX, maxY);
    }

    public AABRect2f move(float x, float y, AABRect2f dst) {
        return dst.set(minX + x, minY + y, maxX + x, maxY + y);
    }

    public AABRect2f move(float x, float y) {
        return move(x, y, this);
    }

    public AABRect2f expand(float x, float y, AABRect2f dst) {
        float fx0 = minX;
        float fy0 = minY;
        float fx1 = maxX;
        float fy1 = maxY;
        if (x < 0.0f)
            fx0 += x;
        if (x > 0.0f)
            fx1 += x;
        if (y < 0.0f)
            fy0 += y;
        if (y > 0.0f)
            fy1 += y;
        return dst.set(fx0, fy0, fx1, fy1);
    }

    public AABRect2f expand(float x, float y) {
        return expand(x, y, this);
    }

    public AABRect2f setMin(Vector2fc min) {
        return setMin(min.x(), min.y());
    }

    public AABRect2f setMin(float x, float y) {
        minX = x;
        minY = y;
        return forceFix();
    }

    public AABRect2f setMax(Vector2fc max) {
        return setMax(max.x(), max.y());
    }

    public AABRect2f setMax(float x, float y) {
        maxX = x;
        maxY = y;
        return forceFix();
    }

    public AABRect2f set(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return forceFix();
    }

    public AABRect2f set(Vector2fc min, Vector2fc max) {
        return set(min.x(), min.y(), max.x(), max.y());
    }

    /**
     * Set this rect to the other rect.
     *
     * @param b the other rect
     * @return this
     */
    public AABRect2f set(AABRect2f b) {
        return set(b.minX, b.minY, b.maxX, b.maxY);
    }

    public Vector2f getCenter(Vector2f dst) {
        tryFix();
        return dst.set((maxX - minX) * 0.5f + minX, (maxY - minY) * 0.5f + minY);
    }

    public Vector2f getExtent(Vector2f dst) {
        tryFix();
        return dst.set((maxX - minX) * 0.5f, (maxY - minY) * 0.5f);
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
}
