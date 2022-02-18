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

    public void updateTime() {
        timer.update();

        for (int i = 0; i < timer.ticks; i++) {
            tick();
        }
    }

    public void boot() {
        try {
            prepare();
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");
            preStart();
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
            keyboard = new Keyboard();
            keyboard.registerToWindow(window);
            mouse = new Mouse(this);
            mouse.registerToWindow(window);
            glfwSetWindowFocusCallback(hWnd, (handle, focused) -> {
                if (!focused) mouse.firstFocus = true;
            });
            timer = new Timer();
            glfwMakeContextCurrent(hWnd);
            glfwSwapInterval(initialSwapInterval);
            GL.createCapabilities(true);
            start();
            glfwShowWindow(hWnd);
            postStart();
            while (!glfwWindowShouldClose(hWnd)) {
                updateTime();
                update();
                run();
                glfwSwapBuffers(hWnd);
                glfwPollEvents();
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
}
