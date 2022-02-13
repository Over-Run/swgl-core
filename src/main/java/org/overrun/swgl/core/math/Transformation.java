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

package org.overrun.swgl.core.math;

import org.joml.*;

/**
 * A swgl transformation.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Transformation implements ITransformation {
    private final Vector3f position;
    private final Quaternionf rotation;
    private final Vector3f scaling;
    /**
     * The matrix object holder.
     */
    private final Matrix4f matrix = new Matrix4f();

    public Transformation(Vector3f position,
                          Quaternionf rotation,
                          Vector3f scaling) {
        this.position = position;
        this.rotation = rotation;
        this.scaling = scaling;
    }

    public Transformation(Vector3fc position,
                          Quaternionfc rotation,
                          Vector3fc scaling) {
        this(new Vector3f(position), new Quaternionf(rotation), new Vector3f(scaling));
    }

    public Transformation() {
        this(new Vector3f(), new Quaternionf(), new Vector3f(1));
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

    public void moveRelative(Vector3fc movement) {
        position.rotate(rotation).add(movement);
    }

    public void moveRelative(float x, float y, float z) {
        position.rotate(rotation).add(x, y, z);
    }

    public void setRotation(Quaternionfc rotation) {
        this.rotation.set(rotation);
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.rotationXYZ(rotation.x(), rotation.y(), rotation.z());
    }

    public void setRotation(float yaw, float pitch, float roll) {
        rotation.rotationXYZ(pitch, yaw, roll);
    }

    public void rotate(Quaternionfc rotation) {
        this.rotation.mul(rotation);
    }

    public void rotate(Vector3fc rotation) {
        this.rotation.rotateXYZ(rotation.x(), rotation.y(), rotation.z());
    }

    public void rotate(float yaw, float pitch, float roll) {
        rotation.rotateXYZ(pitch, yaw, roll);
    }

    public void setScaling(Vector3fc scaling) {
        this.scaling.set(scaling);
    }

    public void setScaling(float x, float y, float z) {
        scaling.set(x, y, z);
    }

    public void scale(Vector3fc scaling) {
        this.scaling.mul(scaling);
    }

    public void scale(float x, float y, float z) {
        scaling.mul(x, y, z);
    }

    @Override
    public Matrix4f getMatrix() {
        return matrix.scaling(scaling).rotate(rotation).translate(position);
    }
}
