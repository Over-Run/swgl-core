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

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.overrun.swgl.core.cfg.GlobalConfig;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The swgl window.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Window {
    private int width;
    private int height;
    private String title;
    private long handle;

    public void createHandle(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL) {
            throw GlobalConfig.wndCreateFailure;
        }
    }

    public void setResizeFunc(GLFWFramebufferSizeCallbackI cb) {
        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            cb.invoke(window, width, height);
        });
    }

    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (handle != NULL)
            glfwSetWindowSize(handle, width, height);
    }

    public void setTitle(String title) {
        this.title = title;
        if (handle != NULL)
            glfwSetWindowTitle(handle, title);
    }

    public void setHandle(long handle) {
        this.handle = handle;
    }

    /**
     * Move the window to center.
     *
     * @param vidWidth  The video mode width.
     * @param vidHeight The video mode height.
     */
    public void moveToCenter(int vidWidth,
                             int vidHeight) {
        glfwSetWindowPos(handle, (vidWidth - width) >> 1, (vidHeight - height) >> 1);
    }

    /**
     * Get the main framebuffer width.
     *
     * @return The width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the main framebuffer height.
     *
     * @return The height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the window title.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the window handle pointer.
     *
     * @return The handle.
     */
    public long getHandle() {
        return handle;
    }
}
