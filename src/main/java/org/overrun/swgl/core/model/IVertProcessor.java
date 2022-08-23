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

package org.overrun.swgl.core.model;

import java.nio.ByteBuffer;

/**
 * The vertex format processor.
 *
 * @author squid233
 * @since 0.2.0
 */
@FunctionalInterface
public interface IVertProcessor {
    /**
     * The 3 bytes processor.
     */
    IVertProcessor BYTE3 = (buffer, x, y, z, w) ->
        buffer.put((byte) x)
            .put((byte) y)
            .put((byte) z);
    /**
     * The 4 bytes processor.
     */
    IVertProcessor BYTE4 = (buffer, x, y, z, w) ->
        buffer.put((byte) x)
            .put((byte) y)
            .put((byte) z)
            .put((byte) w);
    /**
     * The 2 floats processor.
     */
    IVertProcessor FLOAT2 = (buffer, x, y, z, w) ->
        buffer.putFloat((float) x)
            .putFloat((float) y);
    /**
     * The 3 floats processor.
     */
    IVertProcessor FLOAT3 = (buffer, x, y, z, w) ->
        buffer.putFloat((float) x)
            .putFloat((float) y)
            .putFloat((float) z);
    /**
     * The 4 floats processor.
     */
    IVertProcessor FLOAT4 = (buffer, x, y, z, w) ->
        buffer.putFloat((float) x)
            .putFloat((float) y)
            .putFloat((float) z)
            .putFloat((float) w);

    /**
     * Puts into the buffer with the data.
     * <p>
     * <b>Note:</b> the data is all in type {@link Object}, which will be
     * replaced when Java supported to generic enum and primitive type.
     * </p>
     *
     * @param buffer the dest buffer
     * @param x      data x
     * @param y      data y
     * @param z      data z
     * @param w      data w
     */
    void process(ByteBuffer buffer, Object x, Object y, Object z, Object w);
}
