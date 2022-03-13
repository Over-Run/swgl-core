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

package org.overrun.swgl.core.util.timing;

import org.lwjgl.glfw.GLFW;
import org.overrun.swgl.core.cfg.GlobalConfig;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Timer {
    protected double lastTime;
    protected double passedTime;
    /**
     * The real delta time.
     * <p>
     * The equation is: {@code currentTime -} {@link #lastTime}
     * </p>
     */
    public double deltaFrameTime;
    /**
     * The delta time floating in range [0..1].
     */
    public double deltaTime;
    /**
     * The ticks per seconds.
     */
    public int tps = GlobalConfig.initialTps;
    /**
     * The ticks should be ticked in one frame.
     */
    public int ticks;
    /**
     * The timescale. Set to 0 to pause.
     */
    public double timescale = 1;
    /**
     * The max ticks per seconds.
     */
    public int maxTicks = GlobalConfig.initialMaxTicks;

    /**
     * The {@link GLFW#glfwGetTime()} function for users.
     *
     * @return Current time in seconds
     */
    public static double getTime() {
        return GLFW.glfwGetTime();
    }

    public void update() {
        var currentTime = getTime();
        var pt = currentTime - lastTime;
        deltaFrameTime = pt;
        lastTime = currentTime;
        if (pt < 0.0) pt = 0.0;
        if (pt > 1.0) pt = 1.0;
        passedTime += pt * timescale * tps;
        ticks = (int) passedTime;
        if (ticks < 0) ticks = 0;
        if (ticks > maxTicks) ticks = maxTicks;
        passedTime -= ticks;
        deltaTime = passedTime;
    }
}
