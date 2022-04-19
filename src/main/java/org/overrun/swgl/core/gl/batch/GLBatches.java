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

import org.overrun.swgl.core.model.VertexLayout;
import org.overrun.swgl.core.util.math.Numbers;

import java.util.*;
import java.util.function.Function;

import static org.overrun.swgl.core.gl.batch.GLBatchLang.*;

/**
 * The batches.
 *
 * @author squid233
 * @since 0.2.0
 */
public class GLBatches implements AutoCloseable {
    private final Map<String, GLBatch> batches = new LinkedHashMap<>();

    /**
     * The performer of the commands list.
     *
     * @author squid233
     * @since 0.2.0
     */
    private static final class Performer {
        public final List<GLBatchCmd> commands = new ArrayList<>();
        public String[] args = null;

        public void add(Function<String[], GLBatchCmd> function) {
            var cmd = function.apply(args);
            var next = cmd;
            if (commands.size() > 1) {
                int lastIndex = commands.size() - 1;
                var last = commands.get(lastIndex);
                boolean sameIb;
                if ((sameIb = cmd.isSame(KWD_INDEX_BEFORE) && last.isSame(KWD_INDEX_BEFORE))
                    || cmd.isSame(KWD_INDEX_AFTER) && last.isSame(KWD_INDEX_AFTER)) {
                    var result = new String[last.getArgCount() + cmd.getArgCount()];
                    System.arraycopy(last.args, 0, result, 0, last.getArgCount());
                    System.arraycopy(cmd.args, 0, result, last.getArgCount(), cmd.getArgCount());
                    if (sameIb) {
                        next = GLBatchCmd.ib(result);
                    } else {
                        next = GLBatchCmd.ia(result);
                    }
                    commands.remove(lastIndex);
                }
            }

            commands.add(next);
        }
    }

    private static IllegalStateException genCmdError(String cmd, String reason) {
        return new IllegalStateException("Error executing command " + cmd + ": " + reason);
    }

    private static IllegalStateException genVertCmdError(String cmd) {
        return genCmdError(cmd, "can't use without beginf");
    }

    public static GLBatches load(String src) {
        boolean[] failed = {false};
        var failMsg = new StringBuilder();
        int[] currLn = {0};
        var performer = new Performer();
        src.lines().forEachOrdered(s -> {
            if (failed[0]) return;
            ++(currLn[0]);

            var trim = s.trim();
            // Is comment
            if (trim.startsWith("#") || trim.startsWith("//")) {
                return;
            }
            var arr = trim.split("\\s+");
            var cmd = arr[0];
            if (GLBatchLang.isKeyword(cmd)) {
                performer.args = Arrays.copyOfRange(arr, 1, arr.length);
                switch (cmd) {
                    case KWD_BEGINF -> performer.add(GLBatchCmd::beginf);
                    case KWD_END -> performer.add(GLBatchCmd::end);
                    case KWD_VERTEX -> performer.add(GLBatchCmd::vertex);
                    case KWD_COLOR -> performer.add(GLBatchCmd::color);
                    case KWD_TEX_COORD -> performer.add(GLBatchCmd::texCoord);
                    case KWD_NORMAL -> performer.add(GLBatchCmd::normal);
                    case KWD_INDEX_BEFORE -> performer.add(GLBatchCmd::ib);
                    case KWD_INDEX_AFTER -> performer.add(GLBatchCmd::ia);
                    case KWD_EMIT -> performer.add(GLBatchCmd::emit);
                    default -> {
                        failed[0] = true;
                        failMsg.append("Found not supported keyword ").append(cmd);
                    }
                }
            } else {
                failed[0] = true;
                failMsg.append("The first token must be a keyword. Found: ").append(cmd);
            }
        });
        if (failed[0]) {
            throw new RuntimeException("Failed to load batch file at line " + currLn[0] + "! Reason: " + failMsg);
        }
        var batches = new GLBatches();
        boolean batching = false;
        GLBatch batch = null;
        for (var cmd : performer.commands) {
            if (cmd.isSame(KWD_BEGINF)) {
                if (batching) {
                    throw genCmdError("beginf", "can only use once before end");
                }
                var layout = VertexLayout.forName(cmd.getString(1));
                batch = new GLBatch();
                batch.begin(layout);
                batches.batches.put(cmd.getString(0), batch);
                batching = true;
            } else {
                if (!batching) {
                    throw genVertCmdError(cmd.getValue());
                }
                if (cmd.isSame(KWD_END)) {
                    batch.end();
                    batch = null;
                    batching = false;
                } else if (cmd.isSame(KWD_VERTEX)) {
                    if (cmd.getArgCount() == 2) {
                        batch.vertex(cmd.getFloat(0), cmd.getFloat(1));
                    } else if (cmd.getArgCount() == 3) {
                        batch.vertex(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2));
                    } else if (cmd.getArgCount() == 4) {
                        batch.vertex(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2), cmd.getFloat(3));
                    }
                } else if (cmd.isSame(KWD_COLOR)) {
                    if (cmd.getString(0).contains(".")) {
                        if (cmd.getArgCount() == 3) {
                            batch.color(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2));
                        } else if (cmd.getArgCount() == 4) {
                            batch.color(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2), cmd.getFloat(3));
                        }
                    } else {
                        if (cmd.getArgCount() == 3) {
                            batch.color((byte) cmd.getIntAuto4(0), (byte) cmd.getIntAuto4(1), (byte) cmd.getIntAuto4(2));
                        } else if (cmd.getArgCount() == 4) {
                            batch.color((byte) cmd.getIntAuto4(0), (byte) cmd.getIntAuto4(1), (byte) cmd.getIntAuto4(2), (byte) cmd.getIntAuto4(3));
                        }
                    }
                } else if (cmd.isSame(KWD_TEX_COORD)) {
                    if (cmd.getArgCount() == 1) {
                        batch.texCoord(cmd.getFloat(0));
                    } else if (cmd.getArgCount() == 2) {
                        batch.texCoord(cmd.getFloat(0), cmd.getFloat(1));
                    } else if (cmd.getArgCount() == 3) {
                        batch.texCoord(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2));
                    } else if (cmd.getArgCount() == 4) {
                        batch.texCoord(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2), cmd.getFloat(3));
                    }
                } else if (cmd.isSame(KWD_NORMAL)) {
                    if (cmd.getString(0).contains(".")) {
                        batch.normal(cmd.getFloat(0), cmd.getFloat(1), cmd.getFloat(2));
                    } else {
                        batch.normal((byte) cmd.getIntAuto4(0), (byte) cmd.getIntAuto4(1), (byte) cmd.getIntAuto4(2));
                    }
                } else if (cmd.isSame(KWD_EMIT)) {
                    batch.emit();
                } else {
                    int[] indices = new int[cmd.getArgCount()];
                    for (int i = 0; i < indices.length; i++) {
                        var str = cmd.getString(i);
                        indices[i] = Numbers.parseIntAuto4(str);
                    }
                    if (cmd.isSame(KWD_INDEX_BEFORE)) {
                        batch.indexBefore(indices);
                    } else if (cmd.isSame(KWD_INDEX_AFTER)) {
                        batch.indexAfter(indices);
                    }
                }
            }
        }
        return batches;
    }

    public GLBatch get(String name) {
        return batches.get(name);
    }

    public void merge(GLBatches other) {
        batches.putAll(other.batches);
    }

    @Override
    public void close() {
        for (var b : batches.values()) {
            b.close();
        }
        batches.clear();
    }
}
