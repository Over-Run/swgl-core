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

package org.overrun.swgl.game.world.entity;

import org.joml.Math;
import org.overrun.swgl.core.asset.Texture2D;
import org.overrun.swgl.core.gl.GLDrawMode;
import org.overrun.swgl.game.SwglGame;
import org.overrun.swgl.game.world.World;

import static org.overrun.swgl.core.gl.GLStateMgr.bindTexture2D;
import static org.overrun.swgl.core.gl.GLStateMgr.enableTexture2D;
import static org.overrun.swgl.core.gl.ims.GLImmeMode.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class HumanEntity extends Entity {
    public static final String HUMAN_TEXTURE = "swgl_game/steve.png";

    public HumanEntity(World world, float x, float y, float z) {
        super(world);
        setPos(x, y, z);
        eyeHeight = 1.62f;
    }

    @Override
    public void tick() {
        super.tick();

        if (position.y < -64) {
            kill();
        }
        velocity.y -= 0.08f;
        move(velocity.x, velocity.y, velocity.z);
        // x = z = ? = 0.91, y = g = 0.98
        velocity.mul(0.91f, 0.98f, 0.91f);
        if (onGround) {
            velocity.mul(0.7f, 1.0f, 0.7f);
        }
    }

    public void render(double delta) {
        float dt = (float) delta;
        var mgr = SwglGame.getInstance().assetManager;
        Texture2D.getAsset(mgr, HUMAN_TEXTURE).ifPresent(Texture2D::bind);
        enableTexture2D();
        lglSetTexCoordArrayState(true);
        lglPushMatrix();
        lglTranslate(Math.fma(position.x - prevPosition.x, dt, prevPosition.x),
            Math.fma(position.y - prevPosition.y, dt, prevPosition.y),
            Math.fma(position.z - prevPosition.z, dt, prevPosition.z));
        lglScale(0.0625f);

        lglPushMatrix();
        lglTranslate(0.0f, 32.0f, 0.0f);
        lglBegin(GLDrawMode.TRIANGLES);
        lglColor(1, 1, 1);
        lglIndices(0, 1, 2, 2, 3, 0);
        lglTexCoord(8.0f / 64.0f, 8.0f / 64.0f);
        lglVertex(-4, 0, 4);
        lglEmit();
        lglTexCoord(8.0f / 64.0f, 16.0f / 64.0f);
        lglVertex(-4, -8, 4);
        lglEmit();
        lglTexCoord(16.0f / 64.0f, 16.0f / 64.0f);
        lglVertex(4, -8, 4);
        lglEmit();
        lglTexCoord(16.0f / 64.0f, 8.0f / 64.0f);
        lglVertex(4, 0, 4);
        lglEmit();
        lglEnd();
        lglPopMatrix();

        lglPopMatrix();
        bindTexture2D(0);
    }
}
