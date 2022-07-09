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

package org.overrun.swgl.core.cfg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The swgl global configs.
 *
 * @author squid233
 * @since 0.1.0
 */
public class GlobalConfig {
    /**
     * The swgl-core version major.
     */
    public static final int SWGL_CORE_VER_MAJOR = 0;
    /**
     * The swgl-core version minor.
     */
    public static final int SWGL_CORE_VER_MINOR = 2;
    /**
     * The swgl-core version patch.
     */
    public static final int SWGL_CORE_VER_PATCH = 0;
    /**
     * The swgl-core version snapshot. 0 is the release.
     */
    public static final int SWGL_CORE_VER_SNAPSHOT = 0;
    /**
     * The swgl-core version string.
     */
    public static final String SWGL_CORE_VERSION = SWGL_CORE_VER_MAJOR + "." + SWGL_CORE_VER_MINOR + "." + SWGL_CORE_VER_PATCH + (SWGL_CORE_VER_SNAPSHOT != 0 ? "." + SWGL_CORE_VER_SNAPSHOT : "");
    /**
     * The initial GLFW error callback.
     */
    public static GLFWErrorCallbackI initialErrorCallback = null;
    /**
     * The initial ticks per seconds.
     */
    public static int initialTps = 20;
    /**
     * The initial max ticks per seconds.
     */
    public static int initialMaxTicks = 100;
    private static final String DEFAULT_LOGGER_NAME = "SWGL Debugger";
    @NotNull
    private static Logger debugLogger = LoggerFactory.getLogger(DEFAULT_LOGGER_NAME);

    /**
     * Set the debugging logger.
     *
     * @param logger The Logger. Pass {@code null} to set to the default logger.
     * @since 0.2.0
     */
    public static void setDebugLogger(@Nullable Logger logger) {
        debugLogger = (logger == null)
            ? LoggerFactory.getLogger(DEFAULT_LOGGER_NAME)
            : logger;
    }

    /**
     * Get the debugging logger.
     *
     * @return the debugging logger
     * @since 0.2.0
     */
    @NotNull
    public static Logger getDebugLogger() {
        return debugLogger;
    }
}
