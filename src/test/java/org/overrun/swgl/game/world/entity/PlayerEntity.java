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

import org.overrun.swgl.core.io.Keyboard;
import org.overrun.swgl.game.world.World;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class PlayerEntity extends Entity {
    public Keyboard keyboard;

    public PlayerEntity(World world) {
        super(world);
        eyeHeight = 1.62f;
    }

    @Override
    public void tick() {
        super.tick();
        float speed = onGround ? 0.1f : 0.02f;
        float xa = 0, za = 0;
        if (keyboard.isKeyDown(GLFW_KEY_A)) {
            --xa;
        }
        if (keyboard.isKeyDown(GLFW_KEY_D)) {
            ++xa;
        }
        if (keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            if (flying)
                velocity.y = -0.5f;
            eyeHeight = 1.32f;
            bbHeight = 1.5f;
            speed *= 0.5f;
            sneaking = true;
        } else {
            eyeHeight = 1.62f;
            bbHeight = 1.8f;
            sneaking = false;
        }
        if (keyboard.isKeyDown(GLFW_KEY_SPACE)/* && onGround*/) {
            velocity.y = getJumpVelocity();
        }
        if (keyboard.isKeyDown(GLFW_KEY_W)) {
            --za;
        }
        if (keyboard.isKeyDown(GLFW_KEY_S)) {
            ++za;
        }
        if (keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL) && za < 0.0f)
            speed += 0.02f;
        moveRelative(xa, za, speed);
        velocity.y -= 0.08f;
        move(velocity.x, velocity.y, velocity.z);
        // x = z = ? = 0.91, y = g = 0.98 / 10
        velocity.mul(0.91f, 0.98f, 0.91f);
        if (onGround) {
            velocity.mul(0.7f, 1.0f, 0.7f);
        }
    }
}
