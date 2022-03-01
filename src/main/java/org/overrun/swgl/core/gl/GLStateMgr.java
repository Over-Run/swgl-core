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

import org.jetbrains.annotations.ApiStatus;

import static org.lwjgl.opengl.GL43C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLStateMgr {
    /**
     * The core profile enabled status.
     * <p>
     * Can be disabled with JVM arg {@code -Dswgl.coreProfile=false}.
     * </p>
     */
    public static final boolean ENABLE_CORE_PROFILE =
        Boolean.parseBoolean(System.getProperty("swgl.coreProfile", "true"));
    private static int maxCombinedTextureImageUnits;
    private static int maxTextureImageUnits;
    private static int[] texture2dId;
    private static GLTextureState[] texture2DStates;
    private static int activeTexture = 0;
    private static int programId = 0;
    private static boolean debugOutput = false;
    private static boolean depthTest = false;
    private static int depthFunc = GL_LESS;
    private static boolean cullFace = false;
    private static int cullFaceMode = GL_BACK;
    private static boolean blend = false;
    private static int blendSFactorRGB = GL_ONE;
    private static int blendSFactorAlpha = GL_ONE;
    private static int blendDFactorRGB = GL_ZERO;
    private static int blendDFactorAlpha = GL_ZERO;
    private static boolean initialized = false;

    /**
     * Initialize the state manager. You shouldn't invoke it manually.
     */
    @ApiStatus.Internal
    public static void init() {
        if (!initialized) {
            initialized = true;
            maxCombinedTextureImageUnits = glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
            maxTextureImageUnits = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
            texture2dId = new int[maxCombinedTextureImageUnits];
            texture2DStates = new GLTextureState[maxCombinedTextureImageUnits];
        }
    }

    /**
     * Gets the max combined texture image units.
     *
     * @return the max combined texture image units
     */
    public static int getMaxCombTexImgUnits() {
        return maxCombinedTextureImageUnits;
    }

    /**
     * Gets the max texture image units.
     *
     * @return the max texture image units
     */
    public static int getMaxTexImgUnits() {
        return maxTextureImageUnits;
    }

    /**
     * Binds a 2D texture to the active texture unit.
     *
     * @param texture The texture id.
     */
    public static void bindTexture2D(int texture) {
        bindTexture2D(activeTexture, texture);
    }

    /**
     * Active the texture unit and binds a 2D texture to it.
     *
     * @param unit    The texture unit.
     * @param texture The texture id.
     */
    public static void bindTexture2D(int unit, int texture) {
        activeTexture(unit);
        if (texture2dId[unit] != texture) {
            texture2dId[unit] = texture;
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }

    /**
     * Actives a texture unit.
     *
     * @param unit The texture unit.
     */
    public static void activeTexture(int unit) {
        if (activeTexture != unit) {
            activeTexture = unit;
            glActiveTexture(GL_TEXTURE0 + unit);
        }
    }

    /**
     * Get the active texture unit.
     *
     * @return the active texture unit
     */
    public static int getActiveTexture() {
        return activeTexture;
    }

    /**
     * Get the active 2D texture id.
     *
     * @return the active 2D texture id
     */
    public static int get2DTextureId() {
        return get2DTextureId(activeTexture);
    }

    /**
     * Get the 2D texture id by the texture unit.
     *
     * @param unit The texture unit
     * @return the 2D texture id
     */
    public static int get2DTextureId(int unit) {
        return texture2dId[unit];
    }

    /**
     * Use a program.
     *
     * @param program The program id.
     */
    public static void useProgram(int program) {
        if (programId != program) {
            programId = program;
            glUseProgram(program);
        }
    }

    /**
     * Get the program id.
     *
     * @return The program id.
     */
    public static int getProgramId() {
        return programId;
    }

    /**
     * Enable debug output.
     */
    public static void enableDebugOutput() {
        if (!debugOutput) {
            debugOutput = true;
            glEnable(GL_DEBUG_OUTPUT);
        }
    }

    /**
     * Disable debug output.
     */
    public static void disableDebugOutput() {
        if (debugOutput) {
            debugOutput = false;
            glDisable(GL_DEBUG_OUTPUT);
        }
    }

    /**
     * Enable depth test.
     */
    public static void enableDepthTest() {
        if (!depthTest) {
            depthTest = true;
            glEnable(GL_DEPTH_TEST);
        }
    }

    /**
     * Disable depth test.
     */
    public static void disableDepthTest() {
        if (depthTest) {
            depthTest = false;
            glDisable(GL_DEPTH_TEST);
        }
    }

    /**
     * Set the depth function.
     *
     * @param func The depth func
     */
    public static void setDepthFunc(int func) {
        if (depthFunc != func) {
            depthFunc = func;
            glDepthFunc(func);
        }
    }

    /**
     * Enable cull face.
     */
    public static void enableCullFace() {
        if (!cullFace) {
            cullFace = true;
            glEnable(GL_CULL_FACE);
        }
    }

    /**
     * Disable cull face.
     */
    public static void disableCullFace() {
        if (cullFace) {
            cullFace = false;
            glDisable(GL_CULL_FACE);
        }
    }

    /**
     * Set the cull face mode.
     *
     * @param mode The cull face mode
     */
    public static void setCullFace(int mode) {
        if (cullFaceMode != mode) {
            cullFaceMode = mode;
            glCullFace(mode);
        }
    }

    /**
     * Enable blend.
     */
    public static void enableBlend() {
        if (!blend) {
            blend = true;
            glEnable(GL_BLEND);
        }
    }

    /**
     * Disable blend.
     */
    public static void disableBlend() {
        if (blend) {
            blend = false;
            glDisable(GL_BLEND);
        }
    }

    /**
     * Set the blend function.
     *
     * @param sfactor The blend src factor both RGB and alpha
     * @param dfactor The blend dst factor both RGB and alpha
     */
    public static void blendFunc(int sfactor, int dfactor) {
        if (blendSFactorRGB != sfactor && blendSFactorAlpha != sfactor
            && blendDFactorRGB != dfactor && blendDFactorAlpha != dfactor) {
            blendSFactorRGB = blendSFactorAlpha = sfactor;
            blendDFactorRGB = blendDFactorAlpha = dfactor;
            glBlendFunc(sfactor, dfactor);
        }
    }
}
