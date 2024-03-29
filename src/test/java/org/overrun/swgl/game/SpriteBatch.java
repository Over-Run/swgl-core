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

package org.overrun.swgl.game;

import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.core.level.SpriteDrawer;

import static org.overrun.swgl.core.gl.GLStateMgr.*;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class SpriteBatch {
    public static void draw(
        int id,
        float x,
        float y,
        float w,
        float h
    ) {
        int lastId = get2DTextureId();
        bindTexture2D(id);
        enableTexture2D();
        lglSetTexCoordArrayState(true);
        lglBegin(GLDrawMode.QUADS);

        lglColor(1, 1, 1, 1);

        SpriteDrawer.draw(x, y,
            w, h,
            0, 0,
            1, 1,
            1, 1,
            false,
            (x1, y1, z,
             r, g, b, a,
             s, t, p,
             nx, ny, nz,
             color, tex, normal,
             i) -> {
                lglTexCoord(s, t);
                lglVertex(x1, y1);
                lglEmit();
            });

        lglEnd();
        lglSetTexCoordArrayState(false);
        bindTexture2D(lastId);
    }

    public static void draw(
        String name,
        float x,
        float y,
        float w,
        float h
    ) {
        Texture2D.getAsset(SwglGame.getInstance().assetManager, name).
            ifPresent(tex -> draw(tex.getId(), x, y, w, h));
    }
}
