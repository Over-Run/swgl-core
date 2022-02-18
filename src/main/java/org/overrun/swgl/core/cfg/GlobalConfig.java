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

import java.io.PrintStream;

/**
 * The swgl global config.
 *
 * @author squid233
 * @since 0.1.0
 */
public class GlobalConfig {
    public static int initialWidth = 800;
    public static int initialHeight = 600;
    public static String initialTitle = "swgl Game";
    public static int initialSwapInterval = 1;
    public static int initialTps = 20;
    public static int maxTicks = 100;
    public static boolean hasRawMouseMotion = true;
    public static RuntimeException wndCreateFailure = new RuntimeException("Failed to create the GLFW window");
    private static PrintStream debugStream = System.err;

    public static void setDebugStream(PrintStream stream) {
        if (stream == null) {
            debugStream = System.err;
            return;
        }
        debugStream = stream;
    }

    public static PrintStream getDebugStream() {
        return debugStream;
    }
}
