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

package org.overrun.swgl.core.gl.shader;

import org.jetbrains.annotations.Nullable;

/**
 * @author squid233
 * @since 0.2.0
 */
public class GLShaderCreator {
    public static String
    createFragSingleColor(String version,
                          @Nullable String colorModulatorName,
                          @Nullable String outputName,
                          String colors) {
        var sb = new StringBuilder();
        sb.append("#version ").append(version).append('\n');
        if (colorModulatorName != null)
            sb.append("uniform vec4 ").append(colorModulatorName).append(';');
        if (outputName != null)
            sb.append("out vec4 ").append(outputName).append(';');
        sb.append("void main(){");
        sb.append(outputName != null ? outputName : "gl_FragColor").append('=');
        if (colorModulatorName != null)
            sb.append(colorModulatorName).append('*');
        sb.append("vec4(").append(colors).append(");}");
        return sb.toString();
    }
}
