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

import static org.lwjgl.opengl.GL20C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLStateMgr {
    public static final boolean ENABLE_CORE_PROFILE =
        Boolean.parseBoolean(System.getProperty("swgl.coreProfile", "true"));
    private static int[] texture2dId;
    private static int activeTexture2d;
    private static int programId;
    private static boolean initialized = false;

    private static void init() {
        if (!initialized) {
            initialized = true;
            texture2dId = new int[glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)];
        }
    }

    public static void bindTexture2D(int texture) {
        bindTexture2D(activeTexture2d, texture);
    }

    public static void bindTexture2D(int unit, int texture) {
        init();
        activeTexture2D(unit);
        if (texture2dId[unit] != texture) {
            texture2dId[unit] = texture;
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }

    public static void activeTexture2D(int unit) {
        if (activeTexture2d != unit) {
            activeTexture2d = unit;
            glActiveTexture(GL_TEXTURE0 + unit);
        }
    }

    public static int getActive2DTexture() {
        return activeTexture2d;
    }

    public static int get2DTextureId() {
        return get2DTextureId(activeTexture2d);
    }

    public static int get2DTextureId(int unit) {
        init();
        return texture2dId[unit];
    }

    public static void useProgram(int program) {
        if (programId != program) {
            programId = program;
            glUseProgram(program);
        }
    }

    public static int getProgramId() {
        return programId;
    }
}
