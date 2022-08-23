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

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.*;
import org.overrun.swgl.core.cfg.GlobalConfig;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.LongConsumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.stb.STBImage.*;
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

    public void createHandle(int width, int height, String title, LongConsumer failFunc) {
        this.width = width;
        this.height = height;
        this.title = title;
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL) {
            failFunc.accept(handle);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callbacks
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    public GLFWKeyCallback setKeyCb(@Nullable GLFWKeyCallbackI cb) {
        return glfwSetKeyCallback(handle, cb);
    }

    @Nullable
    public GLFWMouseButtonCallback setMouseBtnCb(@Nullable GLFWMouseButtonCallbackI cb) {
        return glfwSetMouseButtonCallback(handle, cb);
    }

    @Nullable
    public GLFWWindowFocusCallback setFocusCb(@Nullable GLFWWindowFocusCallbackI cb) {
        return glfwSetWindowFocusCallback(handle, cb);
    }

    @Nullable
    public GLFWScrollCallback setScrollCb(@Nullable GLFWScrollCallbackI cb) {
        return glfwSetScrollCallback(handle, cb);
    }

    @Nullable
    public GLFWFramebufferSizeCallback setFBResizeCb(@Nullable GLFWFramebufferSizeCallbackI cb) {
        return glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            if (cb != null)
                cb.invoke(window, width, height);
        });
    }

    public void makeContextCurr() {
        glfwMakeContextCurrent(handle);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public void show() {
        glfwShowWindow(handle);
    }

    public void hide() {
        glfwHideWindow(handle);
    }

    public void setIcon(IFileProvider provider, String... images) {
        if (images.length < 1)
            return;
        var pixelBuffers = new ByteBuffer[images.length];
        try (var buf = GLFWImage.calloc(images.length)) {
            int[] x = {0}, y = {0}, c = {0};
            for (int i = 0; i < images.length; i++) {
                var data = provider.res2BBWithRE(images[i], 8192);
                var pixels = stbi_load_from_memory(data, x, y, c, STBI_rgb_alpha);
                buf.get(i).width(x[0]).height(y[0]).pixels(Objects.requireNonNull(pixels));
                pixelBuffers[i] = pixels;
            }
            setIcon(buf);
        } catch (Exception e) {
            GlobalConfig.getDebugLogger().error("Window setIcon ERROR", e);
        }
        for (var buf : pixelBuffers) {
            stbi_image_free(buf);
        }
    }

    public void setIcon(GLFWImage.Buffer images) {
        glfwSetWindowIcon(handle, images);
    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(handle);
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
     * Close the window.
     *
     * @since 0.2.0
     */
    public void close() {
        glfwSetWindowShouldClose(handle, true);
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
