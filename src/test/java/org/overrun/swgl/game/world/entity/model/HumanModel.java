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

package org.overrun.swgl.game.world.entity.model;

import org.joml.Math;
import org.overrun.swgl.core.util.timing.Timer;

/**
 * @author squid233
 * @since 0.1.0
 */
public class HumanModel {
    private final Cube head = new Cube(0, 0);
    private final Cube body = new Cube(16, 16);
    private final Cube legR = new Cube(0, 16);
    private final Cube armR = new Cube(40, 16);
    private final Cube legL = new Cube(16, 48);
    private final Cube armL = new Cube(32, 48);

    public HumanModel() {
        head.setPos(0, 24, 0);
        head.addBox(-4, 0, -4, 4, 8, 4, 8, 8, 8, 64, 64);
        body.setPos(0, 18, 0);
        body.addBox(-4, -6, -2, 4, 6, 2, 8, 12, 4, 64, 64);
        legR.setPos(-2, 12, 0);
        legR.addBox(-2, 0, -2, 2, -12, 2, 4, 12, 4, 64, 64);
        armR.setPos(-6, 24, 0);
        armR.addBox(-2, 0, -2, 2, -12, 2, 4, 12, 4, 64, 64);
        legL.setPos(2, 12, 0);
        legL.addBox(-2, 0, -2, 2, -12, 2, 4, 12, 4, 64, 64);
        armL.setPos(6, 24, 0);
        armL.addBox(-2, 0, -2, 2, -12, 2, 4, 12, 4, 64, 64);
    }

    public void render() {
        float tm = (float) (Timer.getTime() * 10);
        float sin = Math.sin(tm);
        float cos = Math.cosFromSin(sin, tm);
        head.setRot(cos, sin, 0.0f);
        head.render();
        body.render();
        armR.render();
        armL.render();
        legR.render();
        legL.render();
    }
}
