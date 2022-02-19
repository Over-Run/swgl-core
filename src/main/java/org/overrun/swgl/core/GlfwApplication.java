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

package org.overrun.swgl.core;

import org.lwjgl.opengl.GL;
import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.io.Keyboard;
import org.overrun.swgl.core.io.Mouse;
import org.overrun.swgl.core.io.Window;
import org.overrun.swgl.core.util.Timer;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.swgl.core.cfg.GlobalConfig.*;

/**
 * A swgl application implement by GLFW.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class GlfwApplication extends Application {
    /**
     * The window.
     */
    protected Window window;
    /**
     * The keyboard.
     */
    protected Keyboard keyboard;
    /**
     * The mouse.
     */
    protected Mouse mouse;
    /**
     * The timer.
     */
    protected Timer timer;
    /**
     * The frames per seconds.
     */
    protected int frames;

    /**
     * Update the time and ticking.
     */
    public void updateTime() {
        timer.update();

        for (int i = 0; i < timer.ticks; i++) {
            tick();
        }
    }

    /**
     * Boot this application.
     */
    public void boot() {
        try {
            prepare();
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");
            preStart();

            // Setup window
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);//todo add to config
            if (GLStateMgr.ENABLE_CORE_PROFILE) {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            } else {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
            }
            window = new Window();
            window.createHandle(initialWidth, initialHeight, initialTitle);
            long hWnd = window.getHandle();
            window.setResizeFunc((handle, width, height) -> onResize(width, height));

            // Setup IO
            keyboard = new Keyboard();
            keyboard.registerToWindow(window);
            glfwSetKeyCallback(hWnd, (handle, key, scancode, action, mods) -> {
                if (action == GLFW_PRESS) onKeyPress(key, scancode, mods);
                else if (action == GLFW_RELEASE) onKeyRelease(key, scancode, mods);
                else if (action == GLFW_REPEAT) onKeyRepeat(key, scancode, mods);
            });
            mouse = new Mouse(this);
            mouse.registerToWindow(window);
            glfwSetMouseButtonCallback(hWnd, (handle, button, action, mods) -> {
                if (action == GLFW_PRESS) onMouseBtnPress(button, mods);
                else if (action == GLFW_RELEASE) onMouseBtnRelease(button, mods);
            });
            glfwSetWindowFocusCallback(hWnd, (handle, focused) -> {
                if (!focused) mouse.firstFocus = true;
            });

            timer = new Timer();
            glfwMakeContextCurrent(hWnd);
            glfwSwapInterval(initialSwapInterval);
            GL.createCapabilities(true);
            GLStateMgr.init();
            start();
            glfwShowWindow(hWnd);
            postStart();
            double lastTime = glfwGetTime();
            while (!glfwWindowShouldClose(hWnd)) {
                updateTime();
                update();
                run();
                glfwSwapBuffers(hWnd);
                glfwPollEvents();
                ++frames;
                while (glfwGetTime() >= lastTime + 1.0) {
                    settingFrames();
                    lastTime += 1.0;
                    frames = 0;
                }
                postRun();
            }
        } finally {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                window.destroy();
                glfwTerminate();
            }
            postClose();
        }
    }

    /**
     * Will be called when a key is pressed.
     *
     * @param key      the keyboard key that was pressed
     * @param scancode the platform-specific scancode of the key
     * @param mods     bitfield describing which modifiers keys were held down
     */
    public void onKeyPress(int key, int scancode, int mods) {
    }

    /**
     * Will be called when a key is released.
     *
     * @param key      the keyboard key that was released
     * @param scancode the platform-specific scancode of the key
     * @param mods     bitfield describing which modifiers keys were held down
     */
    public void onKeyRelease(int key, int scancode, int mods) {
    }

    /**
     * Will be called when a key is repeated.
     *
     * @param key      the keyboard key
     * @param scancode the platform-specific scancode of the key
     * @param mods     bitfield describing which modifiers keys were held down
     */
    public void onKeyRepeat(int key, int scancode, int mods) {
    }

    /**
     * Will be called when a mouse button is pressed.
     *
     * @param btn  the mouse button that was pressed
     * @param mods bitfield describing which modifiers keys were held down
     */
    public void onMouseBtnPress(int btn, int mods) {
    }

    /**
     * Will be called when a mouse button is released.
     *
     * @param btn  the mouse button that was released
     * @param mods bitfield describing which modifiers keys were held down
     */
    public void onMouseBtnRelease(int btn, int mods) {
    }

    /**
     * Called on setting {@link #frames}.
     */
    public void settingFrames() {
    }
}
