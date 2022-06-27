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
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.APIUtil;
import org.overrun.swgl.core.asset.Asset;
import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.io.*;
import org.overrun.swgl.core.util.timing.Scheduler;
import org.overrun.swgl.core.util.timing.Timer;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BooleanSupplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.swgl.core.cfg.GlobalConfig.*;
import static org.overrun.swgl.core.cfg.WindowConfig.*;

/**
 * A swgl application implement by GLFW.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class GlfwApplication extends Application {
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
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
    protected ResManager resManager;
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
     * Create the error callback to the logger.
     *
     * @param logger the logger
     * @return the error callback
     * @since 0.2.0
     */
    public static GLFWErrorCallback createErrorCb(Logger logger) {
        return new GLFWErrorCallback() {
            // Created from LWJGL GLFWErrorCallback

            private final Map<Integer, String> ERROR_CODES =
                APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000,
                    null,
                    org.lwjgl.glfw.GLFW.class);

            @Override
            public void invoke(int error, long description) {
                var msg = GLFWErrorCallback.getDescription(description);

                logger.error("[LWJGL] {} error", ERROR_CODES.get(error));
                logger.error("\tDescription : {}", msg);
                logger.error("\tStacktrace  :");
                var stack = Thread.currentThread().getStackTrace();
                for (int i = 4; i < stack.length; i++) {
                    logger.error("\t\t{}", stack[i].toString());
                }
            }
        };
    }

    /**
     * Launch this application.
     */
    public void launch() {
        try {
            prepare();
            GLFWErrorCallback.create(Objects.requireNonNullElse(initialErrorCallback,
                createErrorCb(getDebugLogger()))).set();
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");

            // Setup window
            glfwWindowHint(GLFW_VISIBLE, visibleBeforeStart ? GLFW_TRUE : GLFW_FALSE);
            if (!useLegacyGL) {
                if (GLStateMgr.ENABLE_CORE_PROFILE) {
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, requiredGlMajorVer);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, requiredGlMinorVer);
                    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                } else {
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
                }
            }
            preStart();
            window = new Window();
            window.createHandle(initialWidth, initialHeight, initialTitle,
                Objects.requireNonNullElse(wndFailFunc, (handle -> {
                    throw new RuntimeException("Failed to create the GLFW window");
                })));
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
            if (initialCustomIcon != null) {
                initialCustomIcon.accept(window);
            } else {
                window.setIcon(FILE_PROVIDER,
                    Asset.BUILTIN_RES_BASE_DIR + "/icon16.png",
                    Asset.BUILTIN_RES_BASE_DIR + "/icon32.png",
                    Asset.BUILTIN_RES_BASE_DIR + "/icon48.png");
            }

            timer = new Timer();
            window.makeContextCurr();
            glfwSwapInterval(initialSwapInterval);
            GL.createCapabilities(!useLegacyGL);
            GLStateMgr.init();
            start();
            onResize(window.getWidth(), window.getHeight());
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
                    settingFrames(this.frames, frames);
                    this.frames = frames;
                    lastTime += 1.0;
                    frames = 0;
                }
                postRun();
            }
        } catch (Exception e) {
            getDebugLogger().error("GlfwApplication try block ERROR", e);
        } finally {
            try {
                if (resManager != null)
                    resManager.close();
                close();
            } catch (Exception e) {
                getDebugLogger().error("GlfwApplication finally block ERROR", e);
            } finally {
                if (window != null)
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

    @Override
    public void onResize(int width, int height) {
        GL11C.glViewport(0, 0, width, height);
    }

    /**
     * Called on setting {@link #frames}.
     *
     * @param prevFrames the previous frames
     * @param currFrames the current frames
     * @since 0.2.0
     */
    public void settingFrames(int prevFrames,
                              int currFrames) {
    }
}
