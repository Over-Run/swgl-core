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

package org.overrun.swgl.core.model.simple;

import org.overrun.swgl.core.model.IModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The simple model contains a list of the meshes.
 * <p>
 * <b>Note: </b>The meshes must be freed explicit.
 * </p>
 *
 * @author squid233
 * @since 0.1.0
 */
public class SimpleModel implements IModel {
    private final List<SimpleMesh> meshes = new ArrayList<>();

    public SimpleModel(SimpleMesh mesh, SimpleMesh... meshes) {
        this.meshes.add(mesh);
        Collections.addAll(this.meshes, meshes);
    }

    public SimpleModel(Collection<SimpleMesh> meshes) {
        this.meshes.addAll(meshes);
    }

    public void render() {
        for (var mesh : meshes) {
            mesh.render();
        }
    }
}
