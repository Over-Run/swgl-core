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

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL40C.*;
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
        buffer = memCalloc(type.getByteLength());
        switch (type) {
            case M2F -> buffer
                .putFloat(1).putFloat(0)
                .putFloat(0).putFloat(1);
            case M2D -> buffer
                .putDouble(1).putDouble(0)
                .putDouble(0).putDouble(1);
            case M3F -> buffer
                .putFloat(1).putFloat(0).putFloat(0)
                .putFloat(0).putFloat(1).putFloat(0)
                .putFloat(0).putFloat(0).putFloat(1);
            case M3D -> buffer
                .putDouble(1).putDouble(0).putDouble(0)
                .putDouble(0).putDouble(1).putDouble(0)
                .putDouble(0).putDouble(0).putDouble(1);
            case M4F -> buffer
                .putFloat(1).putFloat(0).putFloat(0).putFloat(0)
                .putFloat(0).putFloat(1).putFloat(0).putFloat(0)
                .putFloat(0).putFloat(0).putFloat(1).putFloat(0)
                .putFloat(0).putFloat(0).putFloat(0).putFloat(1);
            case M4D -> buffer
                .putDouble(1).putDouble(0).putDouble(0).putDouble(0)
                .putDouble(0).putDouble(1).putDouble(0).putDouble(0)
                .putDouble(0).putDouble(0).putDouble(1).putDouble(0)
                .putDouble(0).putDouble(0).putDouble(0).putDouble(1);
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
        value.get(buffer.position(0));
    }

    public void set(Vector4fc value) {
        markDirty();
        value.get(buffer.position(0));
    }

    public void set(Matrix3fc value) {
        markDirty();
        value.get(buffer.position(0));
    }

    public void set(Matrix4fc value) {
        markDirty();
        value.get(buffer.position(0));
    }

    public void set(float... values) {
        markDirty();
        for (int i = 0; i < values.length; i++) {
            buffer.putFloat(i * 4, values[i]);
        }
    }

    public void set(ByteBuffer value) {
        markDirty();
        buffer.position(0).put(value);
    }

    /**
     * Upload uniform data to GL. Only on dirty.
     */
    public void upload() {
        if (!isDirty)
            return;
        isDirty = false;
        switch (type) {
            case F1 -> glUniform1f(location, buffer.getFloat(0));
            case F2 -> glUniform2f(location, buffer.getFloat(0), buffer.getFloat(4));
            case F3 -> glUniform3f(location, buffer.getFloat(0), buffer.getFloat(4), buffer.getFloat(8));
            case F4 -> glUniform4f(location, buffer.getFloat(0), buffer.getFloat(4), buffer.getFloat(8), buffer.getFloat(12));
            case I1 -> glUniform1i(location, buffer.getInt(0));
            case I2 -> glUniform2i(location, buffer.getInt(0), buffer.getInt(4));
            case I3 -> glUniform3i(location, buffer.getInt(0), buffer.getInt(4), buffer.getInt(8));
            case I4 -> glUniform4i(location, buffer.getInt(0), buffer.getInt(4), buffer.getInt(8), buffer.getInt(12));
            case UI1 -> glUniform1ui(location, buffer.getInt(0));
            case UI2 -> glUniform2ui(location, buffer.getInt(0), buffer.getInt(4));
            case UI3 -> glUniform3ui(location, buffer.getInt(0), buffer.getInt(4), buffer.getInt(8));
            case UI4 -> glUniform4ui(location, buffer.getInt(0), buffer.getInt(4), buffer.getInt(8), buffer.getInt(12));
            case D1 -> glUniform1d(location, buffer.getDouble(0));
            case D2 -> glUniform2d(location, buffer.getDouble(0), buffer.getDouble(8));
            case D3 -> glUniform3d(location, buffer.getDouble(0), buffer.getDouble(8), buffer.getDouble(16));
            case D4 -> glUniform4d(location, buffer.getDouble(0), buffer.getDouble(8), buffer.getDouble(16), buffer.getDouble(24));
            case M2F -> glUniformMatrix2fv(location, false, buffer.asFloatBuffer());
            case M3F -> glUniformMatrix3fv(location, false, buffer.asFloatBuffer());
            case M4F -> glUniformMatrix4fv(location, false, buffer.asFloatBuffer());
            case M2X3F -> glUniformMatrix2x3fv(location, false, buffer.asFloatBuffer());
            case M3X2F -> glUniformMatrix3x2fv(location, false, buffer.asFloatBuffer());
            case M2X4F -> glUniformMatrix2x4fv(location, false, buffer.asFloatBuffer());
            case M4X2F -> glUniformMatrix4x2fv(location, false, buffer.asFloatBuffer());
            case M3X4F -> glUniformMatrix3x4fv(location, false, buffer.asFloatBuffer());
            case M4X3F -> glUniformMatrix4x3fv(location, false, buffer.asFloatBuffer());
            case M2D -> glUniformMatrix2dv(location, false, buffer.asDoubleBuffer());
            case M3D -> glUniformMatrix3dv(location, false, buffer.asDoubleBuffer());
            case M4D -> glUniformMatrix4dv(location, false, buffer.asDoubleBuffer());
            case M2X3D -> glUniformMatrix2x3dv(location, false, buffer.asDoubleBuffer());
            case M3X2D -> glUniformMatrix3x2dv(location, false, buffer.asDoubleBuffer());
            case M2X4D -> glUniformMatrix2x4dv(location, false, buffer.asDoubleBuffer());
            case M4X2D -> glUniformMatrix4x2dv(location, false, buffer.asDoubleBuffer());
            case M3X4D -> glUniformMatrix3x4dv(location, false, buffer.asDoubleBuffer());
            case M4X3D -> glUniformMatrix4x3dv(location, false, buffer.asDoubleBuffer());
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
