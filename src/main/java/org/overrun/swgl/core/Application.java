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

import org.overrun.swgl.core.io.Mouse;
import org.overrun.swgl.core.io.ResManager;

/**
 * A swgl application.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class Application implements Runnable, AutoCloseable, Mouse.Callback {
    /**
     * Prepare starting argument here, like
     * {@link org.overrun.swgl.core.cfg.GlobalConfig GlobalConfigs}.
     */
    public void prepare() {
    }

    /**
     * Add a resource manager. It should be implemented by its subclasses.
     *
     * @param manager The resource manager.
     */
    public void addResManager(ResManager manager) {
    }

    /**
     * Prepare starting argument here after initializing GLFW.
     */
    public void preStart() {
    }

    /**
     * Initialize anything here, like
     * {@link org.overrun.swgl.core.gl.GLProgram GLProgram} and
     * {@link org.overrun.swgl.core.model.simple.SimpleModel Models}.
     */
    public abstract void start();

    /**
     * Initialize anything here, but after showing window.
     * <p>
     * It can be instead of {@link #start()}, if you want to show a progress
     * bar, but you need to use multi-{@link Thread threads}.
     * </p>
     */
    public void postStart() {
    }

    /**
     * Update anything per frames.
     */
    public void update() {
    }

    /**
     * Update physical things per ticks.
     */
    public void tick() {
    }

    /**
     * Called on resizing. You can update the viewport here.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    public void onResize(int width, int height) {
    }

    @Override
    public void onCursorPos(double x, double y,
                            double xd, double yd) {
    }

    /**
     * Update and render anything after {@link #tick() ticking}.
     */
    @Override
    public abstract void run();

    /**
     * Called after running.
     */
    public void postRun() {
    }

    /**
     * Called at the last of the application.
     */
    public void postClose() {
    }
}
