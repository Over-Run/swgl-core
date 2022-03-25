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

package org.overrun.swgl.core.model;

import static org.overrun.swgl.core.model.VertexFormat.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class BuiltinVertexLayouts {
    public static final VertexLayout V2F = new VertexLayout(VertexFormat.V2F);
    public static final VertexLayout V3F = new VertexLayout(VertexFormat.V3F);
    public static final VertexLayout C4UB_V2F = new VertexLayout(C4UB, VertexFormat.V2F);
    public static final VertexLayout C4UB_V3F = new VertexLayout(C4UB, VertexFormat.V3F);
    public static final VertexLayout C3F_V3F = new VertexLayout(C3F, VertexFormat.V3F);
    public static final VertexLayout N3F_V3F = new VertexLayout(N3F, VertexFormat.V3F);
    public static final VertexLayout N3B_V3F = new VertexLayout(N3B, VertexFormat.V3F);
    public static final VertexLayout C4F_N3F_V3F = new VertexLayout(C4F, N3F, VertexFormat.V3F);
    public static final VertexLayout C4F_N3B_V3F = new VertexLayout(C4F, N3B, VertexFormat.V3F);
    public static final VertexLayout T2F_V3F = new VertexLayout(T2F, VertexFormat.V3F);
    public static final VertexLayout T4F_V4F = new VertexLayout(T4F, V4F);
    public static final VertexLayout T2F_C4UB_V3F = new VertexLayout(T2F, C4UB, VertexFormat.V3F);
    public static final VertexLayout T2F_C3F_V3F = new VertexLayout(T2F, C3F, VertexFormat.V3F);
    public static final VertexLayout T2F_N3F_V3F = new VertexLayout(T2F, N3F, VertexFormat.V3F);
    public static final VertexLayout T2F_N3B_V3F = new VertexLayout(T2F, N3B, VertexFormat.V3F);
    public static final VertexLayout T2F_C4F_N3F_V3F = new VertexLayout(T2F, C4F, N3F, VertexFormat.V3F);
    public static final VertexLayout T2F_C4F_N3B_V3F = new VertexLayout(T2F, C4F, N3B, VertexFormat.V3F);
    public static final VertexLayout T4F_C4F_N3F_V4F = new VertexLayout(T4F, C4F, N3F, V4F);
    public static final VertexLayout T4F_C4F_N3B_V4F = new VertexLayout(T4F, C4F, N3B, V4F);
}
