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

package org.overrun.swgl.core.model.obj;

import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileIO;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.util.IntTri;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * The obj model reader.
 *
 * @author squid233
 * @since 0.1.0
 */
public final class ObjModels {
    public static ObjModel loadModel(String name, IntTri vaIndices) {
        return loadModel(name, aiProcess_JoinIdenticalVertices, vaIndices);
    }

    public static ObjModel loadModel(String name, int flags, IntTri vaIndices) {
        var fileIo = AIFileIO.create()
            .OpenProc((pFileIO, fileName, openMode) -> {
                ByteBuffer data;
                var fnUtf8 = memUTF8(fileName);
                try {
                    data = IFileProvider.ioRes2BB(fnUtf8, 8192);
                } catch (IOException e) {
                    throw new RuntimeException("Could not open file: " + fnUtf8);
                }

                return AIFile.create()
                    .ReadProc((pFile, pBuffer, size, count) -> {
                        long max = Math.min(data.remaining(), size * count);
                        memCopy(memAddress(data) + data.position(), pBuffer, max);
                        return max;
                    })
                    .SeekProc((pFile, offset, origin) -> {
                        if (origin == aiOrigin_CUR) {
                            data.position(data.position() + (int) offset);
                        } else if (origin == aiOrigin_SET) {
                            data.position((int) offset);
                        } else if (origin == aiOrigin_END) {
                            data.position(data.limit() + (int) offset);
                        }
                        return 0;
                    })
                    .FileSizeProc(pFile -> data.limit())
                    .address();
            })
            .CloseProc((pFileIO, pFile) -> {
                var aiFile = AIFile.create(pFile);

                aiFile.ReadProc().free();
                aiFile.SeekProc().free();
                aiFile.FileSizeProc().free();
            });
        var scene = aiImportFileEx(name,
            // The vertices must be triangles
            flags | aiProcess_Triangulate,
            fileIo);
        fileIo.OpenProc().free();
        fileIo.CloseProc().free();
        if (scene == null)
            throw new IllegalStateException(aiGetErrorString());
        return new ObjModel(scene, name.substring(0, name.lastIndexOf('/')) + '/',
            vaIndices);
    }
}
