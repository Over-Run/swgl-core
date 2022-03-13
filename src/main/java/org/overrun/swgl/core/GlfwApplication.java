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

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.io.Keyboard;
import org.overrun.swgl.core.io.Mouse;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.io.Window;
import org.overrun.swgl.core.util.timing.Scheduler;
import org.overrun.swgl.core.util.timing.Timer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

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
     * The resource managers.
     */
    protected final Set<ResManager> resManagers = new LinkedHashSet<>();
    /**
     * The scheduled tasks executed per loop.
     */
    protected final List<Scheduler> scheduledPerLoopTasks = new ArrayList<>();
    /**
     * The passed application ticks.
     */
    protected int passedAppTicks = 0;

    /**
     * Update the time and ticking.
     */
    public void updateTime() {
        timer.update();

        for (int i = 0; i < timer.ticks; i++) {
            for (var task : scheduledPerLoopTasks) {
                task.tick(passedAppTicks);
            }
            tick();
            ++passedAppTicks;
        }
    }

    /**
     * Boot this application.
     */
    public void boot() {
        try {
            prepare();
            if (initialErrorCallback == null) {
                GLFWErrorCallback.createPrint(getDebugStream()).set();
            } else {
                GLFWErrorCallback.create(initialErrorCallback).set();
            }
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");

            // Setup window
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);//todo add to config
            if (GLStateMgr.ENABLE_CORE_PROFILE) {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, requireGlMajorVer);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, requireGlMinorVer);
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            } else {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            }
            preStart();
            window = new Window();
            window.createHandle(initialWidth, initialHeight, initialTitle);
            window.setResizeCb((handle, width, height) -> onResize(width, height));

            // Setup IO
            keyboard = new Keyboard();
            keyboard.setWindow(window);
            window.setKeyCb((handle, key, scancode, action, mods) -> {
                if (action == GLFW_PRESS) onKeyPress(key, scancode, mods);
                else if (action == GLFW_RELEASE) onKeyRelease(key, scancode, mods);
                else if (action == GLFW_REPEAT) onKeyRepeat(key, scancode, mods);
            });
            mouse = new Mouse(this);
            mouse.registerToWindow(window);
            window.setMouseBtnCb((handle, button, action, mods) -> {
                if (action == GLFW_PRESS) onMouseBtnPress(button, mods);
                else if (action == GLFW_RELEASE) onMouseBtnRelease(button, mods);
            });
            window.setFocusCb((handle, focused) -> {
                if (!focused) mouse.firstFocus = true;
            });
            window.setScrollCb((handle, xoffset, yoffset) -> onScroll(xoffset, yoffset));

            timer = new Timer();
            window.makeContextCurr();
            glfwSwapInterval(initialSwapInterval);
            GL.createCapabilities(true);
            GLStateMgr.init();
            start();
            window.show();
            postStart();
            int frames = 0;
            double lastTime = Timer.getTime();
            while (!window.shouldClose()) {
                updateTime();
                update();
                run();
                window.swapBuffers();
                glfwPollEvents();
                ++frames;
                while (Timer.getTime() >= lastTime + 1.0) {
                    settingFrames();
                    this.frames = frames;
                    lastTime += 1.0;
                    frames = 0;
                }
                postRun();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                for (var mgr : resManagers) {
                    if (mgr != null)
                        mgr.close();
                }
                resManagers.clear();
                close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                window.destroy();
                glfwTerminate();
            }
            postClose();
            var cb = glfwSetErrorCallback(null);
            if (cb != null) {
                cb.free();
            }
        }
    }

    @Override
    public void close() throws Exception {
    }

    /**
     * Add a resource manager.
     *
     * @param manager The resource manager.
     */
    @Override
    public void addResManager(ResManager manager) {
        resManagers.add(manager);
    }

    /**
     * Scheduled tasks executed per loop.
     *
     * @param frequency The frequency in ticks.
     * @param command   The command to be executed.
     */
    public void schedulePerLoop(int frequency, BooleanSupplier command) {
        scheduledPerLoopTasks.add(new Scheduler(passedAppTicks, frequency, command));
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
     * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
     *
     * @param xoffset the scroll offset along the x-axis
     * @param yoffset the scroll offset along the y-axis
     */
    public void onScroll(double xoffset, double yoffset) {
    }

    /**
     * Called on setting {@link #frames}.
     */
    public void settingFrames() {
    }
}
