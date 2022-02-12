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

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The swgl keyboard.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Keyboard {
    private final Map<Integer, Integer> keyStates = new HashMap<>();

    private int getState(int key) {
        return keyStates.computeIfAbsent(key, k -> GLFW_RELEASE);
    }

    public boolean isStatePressed(int key) {
        return getState(key) == GLFW_PRESS;
    }

    public boolean isStateReleased(int key) {
        return getState(key) == GLFW_RELEASE;
    }

    public boolean isStateRepeated(int key) {
        return getState(key) == GLFW_REPEAT;
    }

    public boolean isKeyDown(Window window, int key) {
        return glfwGetKey(window.getHandle(), key) == GLFW_PRESS;
    }

    public boolean isKeyUp(Window window, int key) {
        return glfwGetKey(window.getHandle(), key) == GLFW_RELEASE;
    }

    public void registerToWindow(Window window) {
        glfwSetKeyCallback(window.getHandle(), (handle, key, scancode, action, mods) ->
            keyStates.put(key, action));
    }
}
