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

package org.overrun.swgl.core.gl.ims;

import org.lwjgl.system.MemoryUtil;
import org.overrun.swgl.core.cfg.GlobalConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLLists {
    private static final Map<Integer, GLList> LIST_MAP = new LinkedHashMap<>();
    private static int nextId = 0;

    public static int lglGenLists(int s) {
        OptionalInt id = OptionalInt.empty();
        for (int i = 0; i < s; i++) {
            int lst = lglGenList();
            if (id.isEmpty())
                id = OptionalInt.of(lst);
        }
        return id.orElseThrow();
    }

    public static int lglGenList() {
        ++nextId;
        LIST_MAP.put(nextId, new GLList(nextId));
        return nextId;
    }

    private static boolean checkNotPresent(int list) {
        if (!LIST_MAP.containsKey(list)) {
            GlobalConfig.getDebugStream().println("List " + list + " not found; ignoring");
            return true;
        }
        return false;
    }

    public static void lglNewList(int n) {
        if (checkNotPresent(n))
            return;
        currentList = LIST_MAP.get(n);
        lglSetRendering(false);
    }

    public static void lglEndList() {
        currentList.drawMode = lglGetDrawMode();
        currentList.vertexCount = lglGetVertexCount();

        currentList.close();
        currentList.buffer = MemoryUtil.memCalloc(buffer.limit());
        for (int i = 0; currentList.buffer.hasRemaining(); i++) {
            currentList.buffer.put(buffer.get(i));
        }
        currentList.buffer.flip();

        currentList.indexBuffer = MemoryUtil.memCallocInt(indicesBuffer.limit());
        for (int i = 0; currentList.indexBuffer.hasRemaining(); i++) {
            currentList.indexBuffer.put(indicesBuffer.get(i));
        }
        currentList.indexBuffer.flip();

        currentList = null;
        lglSetRendering(true);
    }

    public static void lglCallList(int list) {
        if (checkNotPresent(list))
            return;
        var lst = LIST_MAP.get(list);
        if (lst.getBuffer() == null)
            return;
        lglBegin(lst.drawMode);
        lglBuffer(lst.getBuffer());
        lst.getBuffer().position(0);
        if (lst.getIndexBuffer() != null) {
            lglIndexBuffer(lst.getIndexBuffer());
            lst.getIndexBuffer().position(0);
        }
        lglEnd();
    }

    public static void lglDeleteLists(int list, int range) {
        for (int i = 0; i < range; i++) {
            int lst = list + i;
            if (checkNotPresent(lst))
                continue;
            LIST_MAP.get(lst).close();
            LIST_MAP.remove(lst);
        }
    }
}
