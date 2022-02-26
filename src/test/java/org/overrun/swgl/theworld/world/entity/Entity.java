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

package org.overrun.swgl.theworld.world.entity;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.overrun.swgl.core.util.math.Numbers;
import org.overrun.swgl.theworld.phys.AABB;
import org.overrun.swgl.theworld.world.World;

import static java.lang.Math.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Entity {
    public final World world;
    public final Vector3f prevPosition = new Vector3f();
    public final Vector3f position = new Vector3f();
    public final Vector3f velocity = new Vector3f();
    public final Vector2f rotation = new Vector2f();
    public AABB aabb;
    public boolean onGround = false;
    protected float eyeHeight = 0.0f;
    protected float bbWidth = 0.6f;
    protected float bbHeight = 1.8f;

    public Entity(World world) {
        this.world = world;
        resetPos();
    }

    public void setPos(float x, float y, float z) {
        aabb = new AABB();
        float hw = bbWidth / 2.0f;
        aabb.min.set(x - hw, y, z - hw);
        aabb.max.set(x + hw, y + bbHeight, z + hw);
    }

    protected void resetPos() {
        setPos((float) (world.width * Math.random()),
            world.height + 10,
            (float) (world.depth * Math.random()));
    }

    public void rotate(float yaw, float pitch) {
        rotation.add(pitch, yaw);
        if (rotation.x < -Numbers.RAD90F)
            rotation.x = -Numbers.RAD90F;
        else if (rotation.x > Numbers.RAD90F)
            rotation.x = Numbers.RAD90F;
    }

    public void tick() {
        prevPosition.set(position);
    }

    public void move(float x, float y, float z) {
        float xaOrg = x;
        float yaOrg = y;
        float zaOrg = z;
        var cubes = world.getCubes(aabb.expand(x, y, z));
        for (var cube : cubes) {
            y = cube.clipYCollide(aabb, y);
        }
        aabb.move(0.0f, y, 0.0f);
        for (var cube : cubes) {
            x = cube.clipXCollide(aabb, x);
        }
        aabb.move(x, 0.0f, 0.0f);
        for (var cube : cubes) {
            z = cube.clipZCollide(aabb, z);
        }
        aabb.move(0.0f, 0.0f, z);
        onGround = yaOrg != y && yaOrg < 0.0f;
        if (xaOrg != x)
            velocity.x = 0.0f;
        if (yaOrg != y)
            velocity.y = 0.0f;
        if (zaOrg != z)
            velocity.z = 0.0f;
        position.set(
            (aabb.min.x + aabb.max.x) / 2.0f,
            aabb.min.y,
            (aabb.min.z + aabb.max.z) / 2.0f
        );
    }

    public void moveRelative(float x, float z, float speed) {
        float dst = x * x + z * z;
        if (dst >= 0.01f) {
            dst = speed / (float) sqrt(dst);
            x *= dst;
            z *= dst;
            float sin = (float) sin(rotation.y);
            float cos = (float) cos(rotation.y);
            velocity.x += x * cos - z * sin;
            velocity.z += z * cos + x * sin;
        }
    }

    public float getEyeHeight() {
        return eyeHeight;
    }
}
