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

package org.overrun.swgl.core.io;

import org.jetbrains.annotations.ApiStatus;
import org.overrun.swgl.core.cfg.GlobalConfig;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The swgl mouse.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Mouse {
    private final Callback cb;
    private double lastX = 0.0, lastY = 0.0, deltaX = 0.0, deltaY = 0.0;
    @ApiStatus.Internal
    public boolean firstFocus = true;
    private boolean grabbed;
    private long hWnd;

    public Mouse(Callback cb) {
        this.cb = cb;
    }

    /**
     * The swgl cursor pos callback.
     *
     * @author squid233
     * @since 0.1.0
     */
    @FunctionalInterface
    public interface Callback {
        /**
         * Called on cursor pos changed.
         *
         * @param x  The new pos x.
         * @param y  The new pos y.
         * @param xd The delta pos x.
         * @param yd The delta pos y.
         */
        void onCursorPos(double x, double y,
                         double xd, double yd);
    }

    private static int toInt(double d) {
        return (int) Math.floor(d);
    }

    public boolean isBtnDown(int button) {
        return glfwGetMouseButton(hWnd, button) == GLFW_PRESS;
    }

    public boolean isBtnUp(int button) {
        return glfwGetMouseButton(hWnd, button) == GLFW_RELEASE;
    }

    public double getLastX() {
        return lastX;
    }

    public int getIntLastX() {
        return toInt(getLastX());
    }

    public double getLastY() {
        return lastY;
    }

    public int getIntLastY() {
        return toInt(getLastY());
    }

    public double getDeltaX() {
        return deltaX;
    }

    public int getIntDeltaX() {
        return toInt(getDeltaX());
    }

    public double getDeltaY() {
        return deltaY;
    }

    public int getIntDeltaY() {
        return toInt(getDeltaY());
    }

    public void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
        if (grabbed) {
            if (glfwRawMouseMotionSupported()) {
                if (GlobalConfig.hasRawMouseMotion)
                    glfwSetInputMode(hWnd, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
                else
                    glfwSetInputMode(hWnd, GLFW_RAW_MOUSE_MOTION, GLFW_FALSE);
            }
            glfwSetInputMode(hWnd, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        } else
            glfwSetInputMode(hWnd, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void registerToWindow(Window window) {
        hWnd = window.getHandle();
        glfwSetCursorPosCallback(hWnd, (handle, xpos, ypos) -> {
            if (firstFocus) {
                firstFocus = false;
                lastX = xpos;
                lastY = ypos;
            }
            deltaX = xpos - lastX;
            deltaY = ypos - lastY;
            cb.onCursorPos(xpos, ypos, deltaX, deltaY);
            lastX = xpos;
            lastY = ypos;
        });
    }
}
