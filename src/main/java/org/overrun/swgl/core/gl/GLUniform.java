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
    private boolean isDirty;

    public GLUniform(int location, GLUniformType type) {
        this.location = location;
        this.type = type;
        buffer = memAlloc(type.getByteLength());
        memSet(buffer, 0);
    }

    public int getLocation() {
        return location;
    }

    public GLUniformType getType() {
        return type;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void markDirty() {
        isDirty = true;
    }

    public void set(float value) {
        markDirty();
        buffer.putFloat(0, value);
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

    public void set(Matrix4fc value) {
        markDirty();
        value.get(buffer.position(0));
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
            case F2 -> glUniform2f(location, buffer.getFloat(0), buffer.getFloat(1));
            case F3 -> glUniform3f(location, buffer.getFloat(0), buffer.getFloat(1), buffer.getFloat(2));
            case F4 -> glUniform4f(location, buffer.getFloat(0), buffer.getFloat(1), buffer.getFloat(2), buffer.getFloat(3));
            case I1 -> glUniform1i(location, buffer.getInt(0));
            case I2 -> glUniform2i(location, buffer.getInt(0), buffer.getInt(1));
            case I3 -> glUniform3i(location, buffer.getInt(0), buffer.getInt(1), buffer.getInt(2));
            case I4 -> glUniform4i(location, buffer.getInt(0), buffer.getInt(1), buffer.getInt(2), buffer.getInt(3));
            case UI1 -> glUniform1ui(location, buffer.getInt(0));
            case UI2 -> glUniform2ui(location, buffer.getInt(0), buffer.getInt(1));
            case UI3 -> glUniform3ui(location, buffer.getInt(0), buffer.getInt(1), buffer.getInt(2));
            case UI4 -> glUniform4ui(location, buffer.getInt(0), buffer.getInt(1), buffer.getInt(2), buffer.getInt(3));
            case D1 -> glUniform1d(location, buffer.getDouble(0));
            case D2 -> glUniform2d(location, buffer.getDouble(0), buffer.getDouble(1));
            case D3 -> glUniform3d(location, buffer.getDouble(0), buffer.getDouble(1), buffer.getDouble(2));
            case D4 -> glUniform4d(location, buffer.getDouble(0), buffer.getDouble(1), buffer.getDouble(2), buffer.getDouble(3));
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

    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public void close() {
        memFree(buffer);
    }
}
