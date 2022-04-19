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

package org.overrun.swgl.core.gl.batch;

import org.overrun.swgl.core.util.math.Numbers;

import java.util.Arrays;
import java.util.StringJoiner;

import static org.overrun.swgl.core.gl.batch.GLBatchLang.*;

/**
 * The base batch command.
 *
 * @author squid233
 * @since 0.2.0
 */
public abstract class GLBatchCmd {
    protected final String[] args;

    public GLBatchCmd(String[] args) {
        if (args.length < getRequiredArgCount() || args.length > getAllowArgCount())
            throw new ArrayIndexOutOfBoundsException(getValue()
                                                     + "arguments count is invalid. Required: "
                                                     + getRequiredArgCount()
                                                     + ", Allow: " + getAllowArgCount()
                                                     + ", Actual: " + args.length);
        this.args = args;
    }

    public static boolean isSameAs(String cmdName, GLBatchCmd cmd) {
        return cmd.getValue().equals(cmdName);
    }

    public static GLBatchCmd beginf(String[] args) {
        return of(args, KWD_BEGINF, 2, 2);
    }

    public static GLBatchCmd end(String[] args) {
        return of(args, KWD_END, 0, 0);
    }

    public static GLBatchCmd vertex(String[] args) {
        return of(args, KWD_VERTEX, 2, 4);
    }

    public static GLBatchCmd color(String[] args) {
        return of(args, KWD_COLOR, 3, 4);
    }

    public static GLBatchCmd texCoord(String[] args) {
        return of(args, KWD_TEX_COORD, 1, 4);
    }

    public static GLBatchCmd normal(String[] args) {
        return of(args, KWD_NORMAL, 3, 3);
    }

    public static GLBatchCmd ib(String[] args) {
        return of(args, KWD_INDEX_BEFORE, 1, Integer.MAX_VALUE);
    }

    public static GLBatchCmd ia(String[] args) {
        return of(args, KWD_INDEX_AFTER, 1, Integer.MAX_VALUE);
    }

    public static GLBatchCmd emit(String[] args) {
        return of(args, KWD_EMIT, 0, 0);
    }

    private static GLBatchCmd of(String[] args,
                                 String value,
                                 int minCount, int maxCount) {
        return new GLBatchCmd(args) {
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public int getRequiredArgCount() {
                return minCount;
            }

            @Override
            public int getAllowArgCount() {
                return maxCount;
            }
        };
    }

    public abstract String getValue();

    public abstract int getRequiredArgCount();

    public abstract int getAllowArgCount();

    public int getArgCount() {
        return args.length;
    }

    public String getString(int index) {
        return args[index];
    }

    public float getFloat(int index)
        throws NullPointerException, NumberFormatException {
        return Float.parseFloat(args[index]);
    }

    public int getInt(int index, int radix) throws NumberFormatException {
        return Integer.parseInt(args[index], radix);
    }

    public int getInt(int index) throws NumberFormatException {
        return Integer.parseInt(args[index]);
    }

    public int getIntAuto4(int index) throws NumberFormatException {
        return Numbers.parseIntAuto4(args[index]);
    }

    public boolean isSame(String cmd) {
        return isSameAs(cmd, this);
    }

    @Override
    public String toString() {
        var joiner = new StringJoiner(", ", GLBatchCmd.class.getSimpleName() + "[", "]")
            .add("name=" + getValue());
        if (args.length > 0)
            joiner.add("args=" + Arrays.toString(args));
        return joiner.add("minCount=" + getRequiredArgCount())
            .add("maxCount=" + getAllowArgCount())
            .add("count=" + getArgCount())
            .toString();
    }
}
