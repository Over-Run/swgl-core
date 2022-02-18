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
import org.joml.Matrix4fc;

/**
 * A swgl camera that can move freely.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Camera implements ICamera {
//    todo remove this
//    private final Vector3f prevPosition = new Vector3f();
//    private final Vector3f position = new Vector3f();
//    private final Vector3f lerpPosition = new Vector3f();
//    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
//    private final Vector3f movement = new Vector3f();
//    private final Vector3f center = new Vector3f();
//    private static final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
//    private final Quaternionf rotation = new Quaternionf();
//    /**
//     * The Euler angles holder.
//     */
//    private final Vector3f eulerAngles = new Vector3f();
//    private final Matrix4f matrix = new Matrix4f();
//    public float smoothStep = 1.0f;
//
//    public void setPosition(Vector3fc position) {
//        prevPosition.set(this.position);
//        this.position.set(position);
//    }
//
//    public void move(float speed,
//                     Direction direction) {
//        prevPosition.set(position);
//        switch (direction) {
//            case WEST -> position.sub(front.cross(up, movement).normalize().mul(speed));
//            case EAST -> position.add(front.cross(up, movement).normalize().mul(speed));
//            case DOWN -> position.add(up.mul(1, -1, 1, movement).normalize().mul(speed));
//            case UP -> position.add(up.normalize(movement).mul(speed));
//            case NORTH -> position.add(front.mul(speed, movement));
//            case SOUTH -> position.sub(front.mul(speed, movement));
//        }
//    }
//
//    private void updateFacing() {
//        final float rad90 = (float) Math.toRadians(89.5);
////        final float rad360 = (float) Math.toRadians(360.0);
//        var v  = getEulerAngles();
//        if (v.x > rad90) {
//            rotation.rotationX(rad90);
//        }
//        if (v.x < -rad90) {
//            rotation.rotationX(-rad90);
//        }
////        if (v.y > rad360) {
////            rotation.rotationY(0);
////        }
////        if (v.y < 0) {
////            rotation.rotationY(rad360);
////        }
//        v = getEulerAngles();
//        front.set(0.0f, 0.0f, -1.0f)
//            .rotateY(v.y)
//            .rotateZ(v.z)
//            .rotateX(v.x);
//    }
//
//    public void setRotation(Quaternionfc rotation) {
//        this.rotation.set(rotation);
//        updateFacing();
//    }
//
//    public void setRotation(Vector3fc rotation) {
//        this.rotation.rotationXYZ(rotation.x(), rotation.y(), rotation.z());
//        updateFacing();
//    }
//
//    public void setRotation(float yaw, float pitch, float roll) {
//        rotation.rotationXYZ(pitch, yaw, roll);
//        updateFacing();
//    }
//
//    public void rotate(Quaternionfc rotation) {
//        this.rotation.mul(rotation);
//        updateFacing();
//    }
//
//    public void rotate(Vector3fc rotation) {
//        this.rotation.rotateXYZ(rotation.x(), rotation.y(), rotation.z());
//        updateFacing();
//    }
//
//    public void rotate(float yaw, float pitch, float roll) {
//        rotation.rotateXYZ(pitch, yaw, roll);
//        updateFacing();
//    }
//
//    public Vector3f getEulerAngles() {
//        return rotation.getEulerAnglesXYZ(eulerAngles);
//    }
//
//    public Vector3f getLerpPosition(float t) {
//        prevPosition.lerp(position, t, lerpPosition);
//        return lerpPosition;
//    }
//
//    @Override
//    public Matrix4f getMatrix(@Nullable Matrix4fc multiplier) {
//        matrix.identity();
//        if (multiplier != null)
//            matrix.mul(multiplier);
//        var pos = getLerpPosition(smoothStep);
//        prevPosition.set(position);
////        return matrix.lookAt(pos, pos.add(front, center), up);
//        return matrix.rotate(rotation).translate(pos);
//    }

//    public final Vector3f prevPosition = new Vector3f();
//    private final Vector3f prevPositionCpy = new Vector3f();
//    public final Vector3f position = new Vector3f();
//    private final Vector3f positionCpy = new Vector3f();
//    public final Quaternionf rotation = new Quaternionf();
//    private final Quaternionf rotationCpy = new Quaternionf();
//    public final Vector3f velocity = new Vector3f();
//    /**
//     * The Euler angles holder.
//     */
//    private final Vector3f eulerAngles = new Vector3f();
////    private final Vector3f eulerAnglesCpy = new Vector3f();
//    /**
//     * The matrix object holder.
//     */
//    private final Matrix4f matrix = new Matrix4f();
//    public boolean enableSmoothStep = true;
//    public float smoothStep = 1.0f;
//    public boolean lockPitch = false;
//    public final Vector2f pitchLock = new Vector2f(-89f, 89f);
//
//    public void setPosition(Vector3fc position) {
//        prevPosition.set(this.position);
//        this.position.set(position);
//    }
//
//    public void setPosition(float x, float y, float z) {
//        prevPosition.set(position);
//        position.set(x, y, z);
//    }
//
//    public void move(Vector3fc movement) {
//        velocity.add(movement);
//    }
//
//    public void move(float x, float y, float z) {
//        velocity.add(x, y, z);
//    }
//
//    public void moveRelative(Vector3fc movement, float speed) {
//        moveRelative(movement.x(), movement.y(), movement.z(), speed);
//    }
//
//    public void moveRelative(float x, float y, float z, float speed) {
//        float dist = x * x + y * y + z * z;
//        if (dist >= 0.01f) {
//            dist = speed / sqrt(dist);
//            x *= dist;
//            y *= dist;
//            z *= dist;
//            var v = getEulerAngles();
//            float sin = sin(v.y);
//            float cos = cos(v.y);
//            velocity.x += x * cos - z * sin;
//            velocity.y += y;
//            velocity.z += z * cos + z * sin;
//        }
//    }
//
//    public void setRotation(Quaternionfc rotation) {
//        this.rotation.set(rotation);
//    }
//
//    public void setRotation(Vector3fc rotation) {
//        this.rotation.rotationXYZ(rotation.x(), rotation.y(), rotation.z());
//    }
//
//    public void setRotation(float yaw, float pitch, float roll) {
//        rotation.rotationXYZ(pitch, yaw, roll);
//    }
//
//    public void rotate(Quaternionfc rotation) {
//        this.rotation.mul(rotation);
//    }
//
//    public void rotate(Vector3fc rotation) {
//        this.rotation.rotateXYZ(rotation.x(), rotation.y(), rotation.z());
//    }
//
//    public void rotate(float yaw, float pitch, float roll) {
////        rotation.rotateXYZ(pitch, yaw, roll);
////        rotation.rotateZ(roll).rotateX(pitch).rotateY(yaw);
//        rotation.rotateZYX(roll, yaw, pitch);
//    }
//
//    public void zeroVelocity() {
//        velocity.zero();
//    }
//
//    public Vector3f getEulerAngles() {
//        return rotation.getEulerAnglesXYZ(eulerAngles);
//    }
//
//    public void update() {
//        prevPosition.set(position);
//        position.add(velocity).negate(positionCpy);
////        rotationCpy.x = -rotation.x;
////        rotationCpy.y = -rotation.y;
////        rotationCpy.z = -rotation.z;
//        rotationCpy.set(rotation);
//        prevPosition.lerp(position, smoothStep, prevPositionCpy).negate();
//        var v = getEulerAngles();
//        if (lockPitch) {
//            if (v.x < pitchLock.x)
//                v.x = pitchLock.x;
//            else if (v.x > pitchLock.y)
//                //noinspection SuspiciousNameCombination
//                v.x = pitchLock.y;
//        }
//        setRotation(v);
//    }
//
//    @Override
//    public Matrix4f getMatrix(@Nullable Matrix4fc multiplier) {
//        matrix.identity();
//        if (multiplier != null)
//            matrix.mul(multiplier);
//        matrix.rotate(rotationCpy);
//        if (enableSmoothStep)
//            return matrix.translate(prevPositionCpy);
//        else
//            return matrix.translate(positionCpy);
//    }

    @Override
    public Matrix4fc getMatrix(@Nullable Matrix4fc multiplier) {
        return null;//todo here matrix
    }
}
