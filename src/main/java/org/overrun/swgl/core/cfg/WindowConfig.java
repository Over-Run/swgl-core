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

import org.jetbrains.annotations.Nullable;
import org.overrun.swgl.core.io.Window;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 * The swgl window configs.
 *
 * @author squid233
 * @since 0.2.0
 */
public class WindowConfig {
    /**
     * The initial window width.
     */
    public static int initialWidth = 800;
    /**
     * The initial window height.
     */
    public static int initialHeight = 600;
    /**
     * The initial window title.
     */
    public static String initialTitle = "swgl Game";
    /**
     * The initial window swap interval. Set to 0 to disable v-sync.
     * <br>
     * Set to 2 to get the 2x swap interval.
     */
    public static int initialSwapInterval = 1;
    /**
     * Set to {@code false} to disable raw mouse motion,
     * set to {@code true} to enable raw mouse motion if supported.
     */
    public static boolean hasRawMouseMotion = true;
    /**
     * The required OpenGL major version. Set to non-positive to use the default value.
     */
    public static int requiredGlMajorVer = -1;
    /**
     * The required OpenGL minor version. Set to negative to use the default value.
     */
    public static int requiredGlMinorVer = -1;
    /**
     * Enable core profile.
     *
     * @since 0.2.0
     */
    public static Boolean coreProfile;
    /**
     * Create GL context with forward compatible.
     *
     * @since 0.2.0
     */
    public static boolean forwardCompatible = true;
    /**
     * Set window visible before calling {@code start()}.
     *
     * @since 0.2.0
     */
    public static boolean visibleBeforeStart = false;
    /**
     * Set window resizable.
     *
     * @since 0.2.0
     */
    public static boolean resizable = true;
    /**
     * The Consumer to be called when the window creation failure.
     *
     * @since 0.2.0
     */
    @Nullable
    public static LongConsumer wndFailFunc = null;
    /**
     * The initial custom window icon.
     */
    @Nullable
    public static Consumer<Window> initialCustomIcon;

    /**
     * Sets the required OpenGL version.
     *
     * @since 0.2.0
     */
    public static void setRequiredGlVer(int major,
                                        int minor) {
        requiredGlMajorVer = major;
        requiredGlMinorVer = minor;
    }
}
