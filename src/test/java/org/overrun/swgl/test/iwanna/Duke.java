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

package org.overrun.swgl.test.iwanna;

import org.joml.Vector2f;
import org.overrun.swgl.core.io.Keyboard;
import org.overrun.swgl.core.phys.p2d.AABRect2f;
import org.overrun.swgl.core.util.math.Numbers;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public class Duke {
    public final Vector2f position = new Vector2f();
    public final Vector2f prevPosition = new Vector2f();
    public final Vector2f velocity = new Vector2f();
    public AABRect2f box;
    public boolean onGround = false;
    public Level level;

    public void setPos(float x, float y) {
        position.set(x, y);
        box = AABRect2f.ofSize(x, y, 1.0f, 1.0f);
    }

    public void spawn() {
        setPos(Level.SCENE_WIDTH * 0.5f, 2);
    }

    public void tick(Keyboard keyboard) {
        prevPosition.set(position);
        float speed = onGround ? 0.1f : 0.02f;
        float xa = 0;
        if (keyboard.isKeyDown(GLFW_KEY_A) || keyboard.isKeyDown(GLFW_KEY_LEFT)) {
            --xa;
        }
        if (keyboard.isKeyDown(GLFW_KEY_D) || keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
            ++xa;
        }
        if (keyboard.isKeyDown(GLFW_KEY_SPACE) && onGround) {
            velocity.y = 0.5f;
        }
        moveRelative(xa, speed);
        velocity.y -= 0.08f;
        move(velocity.x, velocity.y);
        velocity.mul(0.91f, 0.98f);
        if (onGround) {
            velocity.mul(0.7f, 1.0f);
        }
    }

    public void moveRelative(float x, float speed) {
        if ((x * x) >= 0.01f) {
            velocity.x += (x * (speed / Math.abs(x)));
        }
    }

    public void move(float x, float y) {
        float xaOrg = x;
        float yaOrg = y;
        var cubes = level.getCubes(box.expand(x, y, new AABRect2f()));
        for (var cube : cubes) {
            y = box.clipYCollide(y, cube);
        }
        box.move(0.0f, y);
        for (var cube : cubes) {
            x = box.clipXCollide(x, cube);
        }
        box.move(x, 0.0f);
        onGround = yaOrg != y && yaOrg < 0.0f;
        if (Numbers.isNonEqual(xaOrg, x))
            velocity.x = 0.0f;
        if (Numbers.isNonEqual(yaOrg, y))
            velocity.y = 0.0f;
        position.set(
            box.minX(),
            box.minY()
        );
    }
}
