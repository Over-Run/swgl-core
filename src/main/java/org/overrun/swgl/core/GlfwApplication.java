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
import org.overrun.swgl.core.io.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.swgl.core.cfg.GlobalConfig.*;

/**
 * A swgl application implement by GLFW.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class GlfwApplication extends Application {
    protected Window window;
    protected Keyboard keyboard;
    protected double lastTime;
    /**
     * The delta time.
     * <p>
     * The equation is: {@code currentTime -} {@link #lastTime}
     * </p>
     */
    protected double deltaTime;
    /**
     * The ticks should be ticked in one frame.
     */
    protected int ticks;
    protected double timeScale = 1;

    public final double getTime() {
        return glfwGetTime();
    }

    public void updateTime() {
        var currentTime = glfwGetTime();
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        ticks = (int) (initialTps * timeScale * deltaTime);
        if (ticks < 0) ticks = 0;
    }

    public void boot() {
        try {
            preStart();
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");
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
            keyboard = new Keyboard();
            keyboard.registerToWindow(window);
            glfwMakeContextCurrent(window.getHandle());
            glfwSwapInterval(initialSwapInterval);
            GL.createCapabilities(true);
            start();
            glfwShowWindow(window.getHandle());
            postStart();
            while (!glfwWindowShouldClose(window.getHandle())) {
                updateTime();
                update();
                for (int i = 0; i < ticks; i++) {
                    tick();
                }
                run();
                glfwSwapBuffers(window.getHandle());
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
