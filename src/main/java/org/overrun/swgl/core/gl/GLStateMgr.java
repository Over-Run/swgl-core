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

    ///////////////////////////////////////////////////////////////////////////
    // Texture
    ///////////////////////////////////////////////////////////////////////////

    private static int maxCombinedTextureImageUnits;
    private static int maxTextureImageUnits;
    private static int[] texture2dId;
    private static GLTextureState[] texture2DStates;
    private static int activeTexture = 0;

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

    public static void enableTexture2D() {
        texture2DStates[activeTexture].enable();
    }

    public static void disableTexture2D() {
        texture2DStates[activeTexture].disable();
    }

    public static boolean isTexture2dEnabled(int unit) {
        return texture2DStates[unit].isEnabled();
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

    ///////////////////////////////////////////////////////////////////////////
    // Debug output
    ///////////////////////////////////////////////////////////////////////////

    private static boolean debugOutput = false;

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

    ///////////////////////////////////////////////////////////////////////////
    // Depth
    ///////////////////////////////////////////////////////////////////////////

    private static boolean depthTest = false;
    private static int depthFunc = GL_LESS;

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

    ///////////////////////////////////////////////////////////////////////////
    // Cull face
    ///////////////////////////////////////////////////////////////////////////

    private static boolean cullFace = false;
    private static int cullFaceMode = GL_BACK;

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

    ///////////////////////////////////////////////////////////////////////////
    // Stencil
    ///////////////////////////////////////////////////////////////////////////

    private static boolean stencilTest = false;
    private static int stencilWriteMask = -1;
    private static int stencilBackWriteMask = -1;
    private static int stencilFunc = GL_ALWAYS;
    private static int stencilBackFunc = GL_ALWAYS;
    private static int stencilRef = 0;
    private static int stencilBackRef = 0;
    private static int stencilValueMask = -1;
    private static int stencilBackValueMask = -1;
    private static int stencilFail = GL_KEEP;
    private static int stencilPassDepthFail = GL_KEEP;
    private static int stencilPassDepthPass = GL_KEEP;
    private static int stencilBackFail = GL_KEEP;
    private static int stencilBackPassDepthFail = GL_KEEP;
    private static int stencilBackPassDepthPass = GL_KEEP;

    /**
     * Enable stencil test.
     */
    public static void enableStencilTest() {
        if (!stencilTest) {
            stencilTest = true;
            glEnable(GL_STENCIL_TEST);
        }
    }

    /**
     * Disable stencil test.
     */
    public static void disableStencilTest() {
        if (stencilTest) {
            stencilTest = false;
            glDisable(GL_STENCIL_TEST);
        }
    }

    /**
     * Set the stencil mask.
     *
     * @param mask the mask
     */
    public static void stencilMask(int mask) {
        stencilMaskSeparate(GL_FRONT_AND_BACK, mask);
    }

    /**
     * Set the stencil mask.
     *
     * @param face the face
     * @param mask the mask
     */
    public static void stencilMaskSeparate(int face, int mask) {
        switch (face) {
            case GL_FRONT -> {
                if (stencilWriteMask != mask) {
                    stencilWriteMask = mask;
                    glStencilMaskSeparate(GL_FRONT, mask);
                }
            }
            case GL_BACK -> {
                if (stencilBackWriteMask != mask) {
                    stencilBackWriteMask = mask;
                    glStencilMaskSeparate(GL_BACK, mask);
                }
            }
            case GL_FRONT_AND_BACK -> {
                if (stencilWriteMask != mask || stencilBackWriteMask != mask) {
                    stencilWriteMask = mask;
                    stencilBackWriteMask = mask;
                    glStencilMask(mask);
                }
            }
        }
    }

    /**
     * Set the stencil func.
     *
     * @param func the func
     * @param ref  the ref value
     * @param mask the mask
     */
    public static void stencilFunc(int func, int ref, int mask) {
        stencilFuncSeparate(GL_FRONT_AND_BACK, func, ref, mask);
    }

    /**
     * Set the stencil func.
     *
     * @param face the face
     * @param func the func
     * @param ref  the ref value
     * @param mask the mask
     */
    public static void stencilFuncSeparate(int face, int func, int ref, int mask) {
        switch (face) {
            case GL_FRONT -> {
                if (stencilFunc != func
                    || stencilRef != ref
                    || stencilValueMask != mask) {
                    stencilFunc = func;
                    stencilRef = ref;
                    stencilValueMask = mask;
                    glStencilFuncSeparate(GL_FRONT, func, ref, mask);
                }
            }
            case GL_BACK -> {
                if (stencilBackFunc != func
                    || stencilBackRef != ref
                    || stencilBackValueMask != mask) {
                    stencilBackFunc = func;
                    stencilBackRef = ref;
                    stencilBackValueMask = mask;
                    glStencilFuncSeparate(GL_BACK, func, ref, mask);
                }
            }
            case GL_FRONT_AND_BACK -> {
                if (stencilFunc != func || stencilBackFunc != func
                    || stencilRef != ref || stencilBackRef != ref
                    || stencilValueMask != mask || stencilBackValueMask != mask) {
                    stencilFunc = func;
                    stencilBackFunc = func;
                    stencilRef = ref;
                    stencilBackRef = ref;
                    stencilValueMask = mask;
                    stencilBackValueMask = mask;
                    glStencilFunc(func, ref, mask);
                }
            }
        }
    }

    /**
     * set front and back stencil test actions
     *
     * @param sfail  Specifies the action to take when the stencil test fails.
     * @param dpfail Specifies the stencil action when the stencil test passes,
     *               but the depth test fails.
     * @param dppass Specifies the stencil action when both the stencil test
     *               and the depth test pass, or when the stencil test passes and
     *               either there is no depth buffer or depth testing is not enabled.
     */
    public static void stencilOp(int sfail, int dpfail, int dppass) {
        stencilOpSeparate(GL_FRONT_AND_BACK, sfail, dpfail, dppass);
    }

    /**
     * set front and/or back stencil test actions
     *
     * @param face   Specifies whether front and/or back stencil state is updated.
     * @param sfail  Specifies the action to take when the stencil test fails.
     * @param dpfail Specifies the stencil action when the stencil test passes,
     *               but the depth test fails.
     * @param dppass Specifies the stencil action when both the stencil test
     *               and the depth test pass, or when the stencil test passes and
     *               either there is no depth buffer or depth testing is not enabled.
     */
    public static void stencilOpSeparate(int face, int sfail, int dpfail, int dppass) {
        switch (face) {
            case GL_FRONT -> {
                if (stencilFail != sfail
                    || stencilPassDepthFail != dpfail
                    || stencilPassDepthPass != dppass) {
                    stencilFail = sfail;
                    stencilPassDepthFail = dpfail;
                    stencilPassDepthPass = dppass;
                    glStencilOpSeparate(GL_FRONT, sfail, dpfail, dppass);
                }
            }
            case GL_BACK -> {
                if (stencilBackFail != sfail
                    || stencilBackPassDepthFail != dpfail
                    || stencilBackPassDepthPass != dppass) {
                    stencilBackFail = sfail;
                    stencilBackPassDepthFail = dpfail;
                    stencilBackPassDepthPass = dppass;
                    glStencilOpSeparate(GL_BACK, sfail, dpfail, dppass);
                }
            }
            case GL_FRONT_AND_BACK -> {
                if (stencilFail != sfail || stencilBackFail != sfail
                    || stencilPassDepthFail != dpfail || stencilBackPassDepthFail != dpfail
                    || stencilPassDepthPass != dppass || stencilBackPassDepthPass != dppass) {
                    stencilFail = sfail;
                    stencilBackFail = sfail;
                    stencilPassDepthFail = dpfail;
                    stencilBackPassDepthFail = dpfail;
                    stencilPassDepthPass = dppass;
                    stencilBackPassDepthPass = dppass;
                    glStencilOp(sfail, dpfail, dppass);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Blend
    ///////////////////////////////////////////////////////////////////////////

    private static boolean blend = false;
    private static int blendSFactorRGB = GL_ONE;
    private static int blendSFactorAlpha = GL_ONE;
    private static int blendDFactorRGB = GL_ZERO;
    private static int blendDFactorAlpha = GL_ZERO;

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
     * Specifies the weighting factors used by the blend equation, for both RGB and alpha functions and for all draw buffers.
     *
     * @param sfactor The blend src factor both RGB and alpha
     * @param dfactor The blend dst factor both RGB and alpha
     */
    public static void blendFunc(int sfactor, int dfactor) {
        if (blendSFactorRGB != sfactor || blendSFactorAlpha != sfactor
            || blendDFactorRGB != dfactor || blendDFactorAlpha != dfactor) {
            blendSFactorRGB = blendSFactorAlpha = sfactor;
            blendDFactorRGB = blendDFactorAlpha = dfactor;
            glBlendFunc(sfactor, dfactor);
        }
    }

    /**
     * Specifies pixel arithmetic for RGB and alpha components separately.
     *
     * @param sfactorRGB   how the red, green, and blue blending factors are computed. The initial value is GL_ONE.
     * @param dfactorRGB   how the red, green, and blue destination blending factors are computed. The initial value is GL_ZERO.
     * @param sfactorAlpha how the alpha source blending factor is computed. The initial value is GL_ONE.
     * @param dfactorAlpha how the alpha destination blending factor is computed. The initial value is GL_ZERO.
     */
    public static void blendFuncSeparate(
        int sfactorRGB,
        int dfactorRGB,
        int sfactorAlpha,
        int dfactorAlpha
    ) {
        if (blendSFactorRGB != sfactorRGB || blendSFactorAlpha != sfactorAlpha
            || blendDFactorRGB != dfactorRGB || blendDFactorAlpha != dfactorAlpha) {
            blendSFactorRGB = sfactorRGB;
            blendSFactorAlpha = sfactorAlpha;
            blendDFactorRGB = dfactorRGB;
            blendDFactorAlpha = dfactorAlpha;
            glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
        }
    }

    /**
     * Specifies the weighting factors used by the blend equation, for both RGB and alpha functions and for all draw buffers.
     *
     * @param sfactor The blend src factor both RGB and alpha
     * @param dfactor The blend dst factor both RGB and alpha
     * @since 0.2.0
     */
    public static void blendFunc(GLBlendFunc sfactor, GLBlendFunc dfactor) {
        blendFunc(sfactor.getValue(), dfactor.getValue());
    }

    /**
     * Specifies pixel arithmetic for RGB and alpha components separately.
     *
     * @param sfactorRGB   how the red, green, and blue blending factors are computed. The initial value is GL_ONE.
     * @param dfactorRGB   how the red, green, and blue destination blending factors are computed. The initial value is GL_ZERO.
     * @param sfactorAlpha how the alpha source blending factor is computed. The initial value is GL_ONE.
     * @param dfactorAlpha how the alpha destination blending factor is computed. The initial value is GL_ZERO.
     * @since 0.2.0
     */
    public static void blendFuncSeparate(
        GLBlendFunc sfactorRGB,
        GLBlendFunc dfactorRGB,
        GLBlendFunc sfactorAlpha,
        GLBlendFunc dfactorAlpha
    ) {
        blendFuncSeparate(sfactorRGB.getValue(), dfactorRGB.getValue(), sfactorAlpha.getValue(), dfactorAlpha.getValue());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Program
    ///////////////////////////////////////////////////////////////////////////

    private static int programId = 0;

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
            stencilWriteMask = glGetInteger(GL_STENCIL_WRITEMASK);
            stencilBackWriteMask = glGetInteger(GL_STENCIL_BACK_WRITEMASK);
            stencilValueMask = glGetInteger(GL_STENCIL_VALUE_MASK);
            stencilBackValueMask = glGetInteger(GL_STENCIL_BACK_VALUE_MASK);
            texture2dId = new int[maxCombinedTextureImageUnits];
            texture2DStates = new GLTextureState[maxCombinedTextureImageUnits];
            for (int i = 0; i < maxCombinedTextureImageUnits; i++) {
                texture2DStates[i] = new GLTextureState(GL_TEXTURE_2D);
            }
        }
    }
}
