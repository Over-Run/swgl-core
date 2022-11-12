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

package org.overrun.swgl.core.level;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A swgl scene.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Scene {
    private final Map<String, Actor> objects = new LinkedHashMap<>();
    private final Map<String, ICamera> cameras = new LinkedHashMap<>();
    private ICamera attachedCamera;

    /**
     * Add a scene object to this scene.
     *
     * @param name   The object name.
     * @param object The object.
     * @return The object to be added.
     */
    public Actor addObject(String name, Actor object) {
        objects.put(name, object);
        return object;
    }

    /**
     * Add a camera to this scene.
     *
     * @param name   The camera name.
     * @param camera The camera.
     * @return The camera to be added.
     */
    public <T extends ICamera> T addCamera(String name, T camera) {
        cameras.put(name, camera);
        return camera;
    }

    /**
     * Get the scene object by name.
     *
     * @param name The name.
     * @return the scene object
     */
    public Actor getObject(String name) {
        return objects.get(name);
    }

    /**
     * Get the camera by name.
     *
     * @param name The name.
     * @return the camera
     */
    public ICamera getCamera(String name) {
        return cameras.get(name);
    }

    /**
     * Attach the camera from the name of the camera.
     *
     * @param name The name.
     */
    public void attachCamera(String name) {
        attachedCamera = getCamera(name);
    }

    public ICamera getAttachedCamera() {
        return attachedCamera;
    }
}
