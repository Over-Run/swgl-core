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

package org.overrun.swgl.core.level;

import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.overrun.swgl.core.util.math.Direction;
import org.overrun.swgl.core.util.math.Numbers;

import static org.joml.Math.*;

/**
 * A swgl camera that can only move on xz plane and rotate on yaw and pitch.
 *
 * @author squid233
 * @since 0.1.0
 */
public class FpsCamera implements ICamera {
    private final Vector3f position = new Vector3f();
    private final Vector3f prevPosition = new Vector3f();
    private final Vector3f lerpPosition = new Vector3f();
    private final Vector2f rotation = new Vector2f(0.0f, -Numbers.RAD90F);
    private final Vector3f resultPosition = new Vector3f();
    private final Vector3f front = new Vector3f(0, 0, -1);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final Matrix4f matrix = new Matrix4f();
    public boolean limitedPitch = false;
    /**
     * The max pitch value range {@code [x..y]} (inclusive).
     */
    public final Vector2f pitchRange = new Vector2f(
        toRadians(-89.5f),
        toRadians(89.5f));
    /**
     * Smooth step value for lerp.
     */
    public float smoothStep = -1;

    public FpsCamera(Vector3fc position,
                     Vector2fc rotation) {
        this.position.set(position);
        prevPosition.set(position);
        this.rotation.set(rotation);
        getFrontVec();
    }


    public FpsCamera(float x,
                     float y,
                     float z,
                     float yaw,
                     float pitch) {
        this(x, y, z);
        rotation.set(pitch, yaw);
        getFrontVec();
    }

    public FpsCamera(float x,
                     float y,
                     float z) {
        position.set(x, y, z);
        prevPosition.set(position);
    }

    public FpsCamera() {
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void move(Vector3fc movement) {
        position.add(movement);
    }

    public void move(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void move(float dt, Direction direction) {
        switch (direction) {
            case WEST -> position.x -= dt;
            case EAST -> position.x += dt;
            case DOWN -> position.y -= dt;
            case UP -> position.y += dt;
            case NORTH -> position.z -= dt;
            case SOUTH -> position.z += dt;
        }
    }

    public void moveRelative(float dt, Direction direction) {
        switch (direction) {
            case WEST -> {
                // Cross product
                float cx = front.y * up.z - front.z * up.y;
                float cz = front.x * up.y - front.y * up.x;
                // Normalize
                float length = invsqrt(fma(cx, cx, cz * cz));
                cx *= length;
                cz *= length;
                position.x -= cx * dt;
                position.z -= cz * dt;
            }
            case EAST -> {
                // Cross product
                float cx = front.y * up.z - front.z * up.y;
                float cz = front.x * up.y - front.y * up.x;
                // Normalize
                float length = invsqrt(fma(cx, cx, cz * cz));
                cx *= length;
                cz *= length;
                position.x += cx * dt;
                position.z += cz * dt;
            }
            case DOWN -> position.y -= dt;
            case UP -> position.y += dt;
            case NORTH -> {
                // Normalize
                float length = invsqrt(fma(front.x, front.x, front.z * front.z));
                float fx = front.x * length;
                float fz = front.z * length;
                position.x += dt * fx;
                position.z += dt * fz;
            }
            case SOUTH -> {
                // Normalize
                float length = invsqrt(fma(front.x, front.x, front.z * front.z));
                float fx = front.x * length;
                float fz = front.z * length;
                position.x -= dt * fx;
                position.z -= dt * fz;
            }
        }
    }

    public void moveRelative(float dx, float dy, float dz) {
        if (Numbers.isNonZero(dz)) {
            // Normalize
            float length = invsqrt(fma(front.x, front.x, front.z * front.z));
            float fx = front.x * length;
            float fz = front.z * length;
            position.x -= dz * fx;
            position.z -= dz * fz;
        }

        if (Numbers.isNonZero(dx)) {
            // Cross product
            float cx = front.y * up.z - front.z * up.y;
            float cz = front.x * up.y - front.y * up.x;
            // Normalize
            float length = invsqrt(fma(cx, cx, cz * cz));
            cx *= length;
            cz *= length;
            position.x += cx * dx;
            position.z += cz * dx;
        }

        position.y += dy;
    }

    public void moveRelative(float dx, float dy, float dz, float speed) {
        moveRelative(dx * speed, dy * speed, dz * speed);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void lockRot() {
        if (limitedPitch) {
            if (rotation.x < pitchRange.x) {
                rotation.x = pitchRange.x;
            } else if (rotation.x > pitchRange.y) {
                rotation.x = pitchRange.y;
            }
        }
        while (rotation.y < 0.0f) {
            rotation.y += Numbers.RAD360F;
        }
        while (rotation.y > Numbers.RAD360F) {
            rotation.y -= Numbers.RAD360F;
        }
        getFrontVec();
    }

    public void setRotation(Vector2fc rotation) {
        this.rotation.set(rotation);
        lockRot();
    }

    public void setRotation(float yaw, float pitch) {
        rotation.set(pitch, yaw);
        lockRot();
    }

    public void rotate(Vector2fc rotation) {
        this.rotation.add(rotation);
        lockRot();
    }

    public void rotate(float yaw, float pitch) {
        rotation.add(pitch, yaw);
        lockRot();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getRotationXY() {
        return rotation;
    }

    public void update() {
        prevPosition.set(position);
    }

    public Vector3f getLerpPosition() {
        return prevPosition.lerp(position, smoothStep, lerpPosition);
    }

    public Vector3f getFrontVec() {
        float pitch = rotation.x;
        float sinPitch = sin(pitch);
        float cosPitch = cosFromSin(sinPitch, pitch);
        float yaw = rotation.y;
        float sinYaw = sin(yaw);
        float cosYaw = cosFromSin(sinYaw, yaw);
        front.x = cosPitch * cosYaw;
        front.y = sinPitch;
        front.z = cosPitch * sinYaw;
        return front.normalize();
    }

    @Override
    public Matrix4f getMatrix(@Nullable Matrix4fc multiplier) {
        matrix.identity();
        if (multiplier != null)
            matrix.set(multiplier);
        var pos = smoothStep >= 0 ? getLerpPosition() : position;
        return matrix.lookAt(pos,
            pos.add(getFrontVec(), resultPosition),
            up);
    }

    @Override
    public Matrix4f getMatrix() {
        return getMatrix(null);
    }
}
