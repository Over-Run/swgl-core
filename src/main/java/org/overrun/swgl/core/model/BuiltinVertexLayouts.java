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
    private static VertexLayout v2f, v3f;
    private static VertexLayout c4ub_v2f, c4ub_v3f, c3f_v3f;
    private static VertexLayout n3f_v3f, n3b_v3f;
    private static VertexLayout c4f_n3f_v3f, c4f_n3b_v3f;
    private static VertexLayout t2f_v3f;
    private static VertexLayout t2f_c4ub_v3f, t2f_c3f_v3f;
    private static VertexLayout t2f_n3f_v3f, t2f_n3b_v3f;
    private static VertexLayout t2f_c4f_n3f_v3f, t2f_c4f_n3b_v3f;

    public static VertexLayout V2F() {
        if (v2f == null)
            v2f = new VertexLayout(V2F);
        return v2f;
    }

    public static VertexLayout V3F() {
        if (v3f == null)
            v3f = new VertexLayout(V3F);
        return v3f;
    }

    public static VertexLayout C4UB_V2F() {
        if (c4ub_v2f == null)
            c4ub_v2f = new VertexLayout(C4UB, V2F);
        return c4ub_v2f;
    }

    public static VertexLayout C4UB_V3F() {
        if (c4ub_v3f == null)
            c4ub_v3f = new VertexLayout(C4UB, V3F);
        return c4ub_v3f;
    }

    public static VertexLayout C3F_V3F() {
        if (c3f_v3f == null)
            c3f_v3f = new VertexLayout(C3F, V3F);
        return c3f_v3f;
    }

    public static VertexLayout N3F_V3F() {
        if (n3f_v3f == null)
            n3f_v3f = new VertexLayout(N3F, V3F);
        return n3f_v3f;
    }

    public static VertexLayout N3B_V3F() {
        if (n3b_v3f == null)
            n3b_v3f = new VertexLayout(N3B, V3F);
        return n3b_v3f;
    }

    public static VertexLayout C4F_N3F_V3F() {
        if (c4f_n3f_v3f == null)
            c4f_n3f_v3f = new VertexLayout(C4F, N3F, V3F);
        return c4f_n3f_v3f;
    }

    public static VertexLayout C4F_N3B_V3F() {
        if (c4f_n3b_v3f == null)
            c4f_n3b_v3f = new VertexLayout(C4F, N3B, V3F);
        return c4f_n3b_v3f;
    }

    public static VertexLayout T2F_V3F() {
        if (t2f_v3f == null)
            t2f_v3f = new VertexLayout(T2F, V3F);
        return t2f_v3f;
    }

    public static VertexLayout T2F_C4UB_V3F() {
        if (t2f_c4ub_v3f == null)
            t2f_c4ub_v3f = new VertexLayout(T2F, C4UB, V3F);
        return t2f_c4ub_v3f;
    }

    public static VertexLayout T2F_C3F_V3F() {
        if (t2f_c3f_v3f == null)
            t2f_c3f_v3f = new VertexLayout(T2F, C3F, V3F);
        return t2f_c3f_v3f;
    }

    public static VertexLayout T2F_N3F_V3F() {
        if (t2f_n3f_v3f == null)
            t2f_n3f_v3f = new VertexLayout(T2F, N3F, V3F);
        return t2f_n3f_v3f;
    }

    public static VertexLayout T2F_N3B_V3F() {
        if (t2f_n3b_v3f == null)
            t2f_n3b_v3f = new VertexLayout(T2F, N3B, V3F);
        return t2f_n3b_v3f;
    }

    public static VertexLayout T2F_C4F_N3F_V3F() {
        if (t2f_c4f_n3f_v3f == null)
            t2f_c4f_n3f_v3f = new VertexLayout(T2F, C4F, N3F, V3F);
        return t2f_c4f_n3f_v3f;
    }

    public static VertexLayout T2F_C4F_N3B_V3F() {
        if (t2f_c4f_n3b_v3f == null)
            t2f_c4f_n3b_v3f = new VertexLayout(T2F, C4F, N3B, V3F);
        return t2f_c4f_n3b_v3f;
    }
}
