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
import org.joml.Math;
import org.joml.*;
import org.overrun.swgl.core.util.math.Direction;
import org.overrun.swgl.core.util.math.FloatPoint;

/**
 * A swgl camera that can only move on xz plane and rotate on yaw and pitch.
 *
 * @author squid233
 * @since 0.1.0
 */
public class FpsCamera implements ICamera {
    private final Vector3f position = new Vector3f();
    private final Vector2f rotation = new Vector2f();
    private final Vector2f rotationYX = new Vector2f();
    private final Vector3f negatePosition = new Vector3f();
    private final Vector2f negateRotation = new Vector2f();
    private final Vector3f lerpPosition = new Vector3f();
    private final Matrix4f matrix = new Matrix4f();
    public boolean restrictPitch = false;
    /**
     * Restrict the pitch value in range {@code [x..y]} (inclusive).
     */
    public final Vector2f pitchRange = new Vector2f(
        // toRadians(90)
        -1.5707963267948966f,
        1.5707963267948966f);

    public FpsCamera(Vector3fc position,
                     Vector2fc rotation) {
        this.position.set(position);
        this.rotation.set(rotation);
    }


    public FpsCamera(float x,
                     float y,
                     float z,
                     float yaw,
                     float pitch) {
        this(x, y, z);
        rotation.set(pitch, yaw);
    }

    public FpsCamera(float x,
                     float y,
                     float z) {
        position.set(x, y, z);
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
        float yaw = rotation.y;
        float sin = Math.sin(yaw);
        float cos = Math.cosFromSin(sin, yaw);
        switch (direction) {
            case WEST -> {
                position.x -= dt * cos;
                position.z += dt * sin;
            }
            case EAST -> {
                position.x += dt * cos;
                position.z -= dt * sin;
            }
            case DOWN -> position.y -= dt;
            case UP -> position.y += dt;
            case NORTH -> {
                position.z -= dt * cos;
                position.x -= dt * sin;
            }
            case SOUTH -> {
                position.z += dt * cos;
                position.x += dt * sin;
            }
        }
    }

    public void moveRelative(float dx, float dy, float dz) {
        float yaw = rotation.y;
        float sin = Math.sin(yaw);
        float cos = Math.cosFromSin(sin, yaw);

        if (FloatPoint.isNonZero(dx)) {
            position.x += dx * cos;
            position.z -= dx * sin;
        }
        if (FloatPoint.isNonZero(dz)) {
            position.z += dz * cos;
            position.x += dz * sin;
        }
        position.y += dy;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void restrictPitch() {
        if (restrictPitch) {
            if (rotation.x < pitchRange.x) {
                rotation.x = pitchRange.x;
            } else if (rotation.x > pitchRange.y) {
                rotation.x = pitchRange.y;
            }
        }
    }

    public void setRotation(Vector2fc rotation) {
        this.rotation.set(rotation);
        restrictPitch();
    }

    public void setRotation(float yaw, float pitch) {
        rotation.set(pitch, yaw);
        restrictPitch();
    }

    public void rotate(Vector2fc rotation) {
        this.rotation.add(rotation);
        restrictPitch();
    }

    public void rotate(float yaw, float pitch) {
        rotation.add(pitch, yaw);
        restrictPitch();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getRotationXY() {
        return rotation;
    }

    public Vector2f getRotationYX(Vector2f dst) {
        return dst.set(rotation);
    }

    public Vector2f getRotationYX() {
        return getRotationYX(rotationYX);
    }

    public Vector3f getNegatePosition() {
        return position.negate(negatePosition);
    }

    public Vector2f getNegateRotation() {
        return rotation.negate(negateRotation);
    }

    public Vector3f getLerpPosition(Vector3fc v, float t) {
        return v.lerp(position, t, lerpPosition);
    }

    private void mul(@Nullable Matrix4fc multiplier) {
        matrix.identity();
        if (multiplier != null)
            matrix.set(multiplier);
    }

    @Override
    public Matrix4f getMatrix(@Nullable Matrix4fc multiplier) {
        mul(multiplier);
        return matrix.rotateX(rotation.x).rotateY(rotation.y)
            .translate(position);
    }

    public Matrix4f getForWorldMatrix(@Nullable Matrix4fc multiplier) {
        mul(multiplier);
        return matrix.rotateX(-rotation.x).rotateY(-rotation.y)
            .translate(-position.x, -position.y, -position.z);
    }

    public Matrix4f getForWorldMatrix() {
        return getForWorldMatrix(null);
    }
}
