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

import static org.lwjgl.glfw.GLFW.*;

/**
 * The swgl keyboard.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Keyboard {
    private Window window;

    /**
     * Get the key down state.
     *
     * @param key The key
     * @return Is the state pressed
     */
    public boolean isKeyDown(int key) {
        return glfwGetKey(window.getHandle(), key) == GLFW_PRESS;
    }

    /**
     * Get the key up state.
     *
     * @param key The key
     * @return Is the state released
     */
    public boolean isKeyUp(int key) {
        return glfwGetKey(window.getHandle(), key) == GLFW_RELEASE;
    }

    /**
     * Set the window.
     *
     * @param window The window.
     */
    public void setWindow(Window window) {
        this.window = window;
    }
}
