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

import org.overrun.swgl.core.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * The swgl resource manager to manage resources and auto clean.
 *
 * @author squid233
 * @since 0.1.0
 */
public class ResManager implements AutoCloseable {
    private final List<AutoCloseable> resources = new ArrayList<>();

    /**
     * Default constructor.
     */
    public ResManager() {
    }

    /**
     * Construct and add this to an application.
     *
     * @param app The application to be added.
     */
    public ResManager(Application app) {
        this();
        app.addResManager(this);
    }

    /**
     * Add a resource to resources list.
     *
     * @param resource The resource.
     * @param <T>      The resource type.
     * @return The resource.
     */
    public <T extends AutoCloseable> T addResource(T resource) {
        resources.add(resource);
        return resource;
    }

    @Override
    public void close() throws Exception {
        for (var resource : resources) {
            if (resource != null)
                resource.close();
        }
        resources.clear();
    }
}
