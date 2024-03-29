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

package org.overrun.swgl.core.gl;

import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL21C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL40C;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class GLUniform implements AutoCloseable {
    private final int location;
    private final GLUniformType type;
    private final ByteBuffer buffer;
    private boolean isDirty = true;

    /**
     * Construct the uniform.
     *
     * @param location The uniform location.
     * @param type     The uniform type.
     */
    public GLUniform(int location, GLUniformType type) {
        this.location = location;
        this.type = type;
        buffer = memAlloc(type.getByteLength());
        switch (type) {
            case M2F -> buffer
                .putFloat(1).putFloat(0)
                .putFloat(0).putFloat(1)
                .flip();
            case M2D -> buffer
                .putDouble(1).putDouble(0)
                .putDouble(0).putDouble(1)
                .flip();
            case M3F -> buffer
                .putFloat(1).putFloat(0).putFloat(0)
                .putFloat(0).putFloat(1).putFloat(0)
                .putFloat(0).putFloat(0).putFloat(1)
                .flip();
            case M3D -> buffer
                .putDouble(1).putDouble(0).putDouble(0)
                .putDouble(0).putDouble(1).putDouble(0)
                .putDouble(0).putDouble(0).putDouble(1)
                .flip();
            case M4F -> buffer
                .putFloat(1).putFloat(0).putFloat(0).putFloat(0)
                .putFloat(0).putFloat(1).putFloat(0).putFloat(0)
                .putFloat(0).putFloat(0).putFloat(1).putFloat(0)
                .putFloat(0).putFloat(0).putFloat(0).putFloat(1)
                .flip();
            case M4D -> buffer
                .putDouble(1).putDouble(0).putDouble(0).putDouble(0)
                .putDouble(0).putDouble(1).putDouble(0).putDouble(0)
                .putDouble(0).putDouble(0).putDouble(1).putDouble(0)
                .putDouble(0).putDouble(0).putDouble(0).putDouble(1)
                .flip();
        }
    }

    /**
     * Get the uniform location.
     *
     * @return The uniform location.
     */
    public int getLocation() {
        return location;
    }

    /**
     * Get the uniform type.
     *
     * @return The uniform type.
     */
    public GLUniformType getType() {
        return type;
    }

    /**
     * Get the uniform dirty state.
     *
     * @return is dirty
     */
    public boolean isDirty() {
        return isDirty;
    }

    /**
     * Mark this uniform as dirty.
     */
    public void markDirty() {
        isDirty = true;
    }

    public void set(float value) {
        markDirty();
        buffer.putFloat(0, value);
    }

    public void set(float x, float y) {
        markDirty();
        buffer.putFloat(0, x).putFloat(4, y);
    }

    public void set(float x, float y, float z) {
        markDirty();
        buffer.putFloat(0, x).putFloat(4, y).putFloat(8, z);
    }

    public void set(float x, float y, float z, float w) {
        markDirty();
        buffer.putFloat(0, x).putFloat(4, y).putFloat(8, z).putFloat(12, w);
    }

    public void set(boolean value) {
        markDirty();
        buffer.putInt(0, value ? 1 : 0);
    }

    public void set(int value) {
        markDirty();
        buffer.putInt(0, value);
    }

    public void set(double value) {
        markDirty();
        buffer.putDouble(0, value);
    }

    public void set(Vector3fc value) {
        markDirty();
        value.get(buffer);
    }

    public void set(Vector4fc value) {
        markDirty();
        value.get(buffer);
    }

    public void set(Matrix3fc value) {
        markDirty();
        value.get(buffer);
    }

    public void set(Matrix4fc value) {
        markDirty();
        value.get(buffer);
    }

    public void set(float... values) {
        markDirty();
        for (int i = 0; i < values.length; i++) {
            buffer.putFloat(i * 4, values[i]);
        }
    }

    public void set(ByteBuffer value) {
        markDirty();
        buffer.put(value).position(0);
    }

    /**
     * @author squid233
     * @since 0.2.0
     */
    @FunctionalInterface
    private interface Vec {
        void accept(int loc, int count, long addr);
    }

    /**
     * @author squid233
     * @since 0.2.0
     */
    @FunctionalInterface
    private interface Mat {
        void accept(int loc, int count, boolean transpose, long addr);
    }

    private void vecb(Vec vec) {
        vec.accept(location, type.size(), memAddress(buffer));
    }

    private void matb(Mat mat) {
        mat.accept(location, 1, false, memAddress(buffer));
    }

    /**
     * Upload uniform data to GL. Only on dirty.
     */
    public void upload() {
        if (!isDirty)
            return;
        isDirty = false;
        switch (type) {
            case F1 -> vecb(GL20C::nglUniform1fv);
            case F2 -> vecb(GL20C::nglUniform2fv);
            case F3 -> vecb(GL20C::nglUniform3fv);
            case F4 -> vecb(GL20C::nglUniform4fv);
            case I1 -> vecb(GL20C::nglUniform1iv);
            case I2 -> vecb(GL20C::nglUniform2iv);
            case I3 -> vecb(GL20C::nglUniform3iv);
            case I4 -> vecb(GL20C::nglUniform4iv);
            case UI1 -> vecb(GL30C::nglUniform1uiv);
            case UI2 -> vecb(GL30C::nglUniform2uiv);
            case UI3 -> vecb(GL30C::nglUniform3uiv);
            case UI4 -> vecb(GL30C::nglUniform4uiv);
            case D1 -> vecb(GL40C::nglUniform1dv);
            case D2 -> vecb(GL40C::nglUniform2dv);
            case D3 -> vecb(GL40C::nglUniform3dv);
            case D4 -> vecb(GL40C::nglUniform4dv);
            case M2F -> matb(GL20C::nglUniformMatrix2fv);
            case M3F -> matb(GL20C::nglUniformMatrix3fv);
            case M4F -> matb(GL20C::nglUniformMatrix4fv);
            case M2X3F -> matb(GL21C::nglUniformMatrix2x3fv);
            case M3X2F -> matb(GL21C::nglUniformMatrix3x2fv);
            case M2X4F -> matb(GL21C::nglUniformMatrix2x4fv);
            case M4X2F -> matb(GL21C::nglUniformMatrix4x2fv);
            case M3X4F -> matb(GL21C::nglUniformMatrix3x4fv);
            case M4X3F -> matb(GL21C::nglUniformMatrix4x3fv);
            case M2D -> matb(GL40C::nglUniformMatrix2dv);
            case M3D -> matb(GL40C::nglUniformMatrix3dv);
            case M4D -> matb(GL40C::nglUniformMatrix4dv);
            case M2X3D -> matb(GL40C::nglUniformMatrix2x3dv);
            case M3X2D -> matb(GL40C::nglUniformMatrix3x2dv);
            case M2X4D -> matb(GL40C::nglUniformMatrix2x4dv);
            case M4X2D -> matb(GL40C::nglUniformMatrix4x2dv);
            case M3X4D -> matb(GL40C::nglUniformMatrix3x4dv);
            case M4X3D -> matb(GL40C::nglUniformMatrix4x3dv);
        }
    }

    /**
     * Get the buffer of the uniform.
     *
     * @return The buffer.
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public void close() {
        memFree(buffer);
    }
}
