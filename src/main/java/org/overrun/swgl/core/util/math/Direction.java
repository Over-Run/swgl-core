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

package org.overrun.swgl.core.util.math;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Locale;
import java.util.StringJoiner;

/**
 * The 6 directions.
 *
 * @author squid233
 * @since 0.1.0
 */
public enum Direction {
    /**
     * The west facing, in the left.
     */
    WEST("west", 0, 1, 1, -1, 0, 0),
    /**
     * The east facing, in the right.
     */
    EAST("east", 1, 0, 3, 1, 0, 0),
    /**
     * The down facing.
     */
    DOWN("down", 2, 3, 3, 0, -1, 0),
    /**
     * The up facing.
     */
    UP("up", 3, 2, 1, 0, 1, 0),
    /**
     * The north facing, in the back.
     */
    NORTH("north", 4, 5, 0, 0, 0, -1),
    /**
     * The south facing, in the front.
     */
    SOUTH("south", 5, 4, 2, 0, 0, 1);

    private final String name;
    private final int id;
    private final int oppositeId;
    private final int horizontalId;
    private final int offsetX, offsetY, offsetZ;

    Direction(String name,
              int id,
              int oppositeId,
              int horizontalId,
              int offsetX,
              int offsetY,
              int offsetZ) {
        this.name = name;
        this.id = id;
        this.oppositeId = oppositeId;
        this.horizontalId = horizontalId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    /**
     * Get the direction by number id.
     *
     * @param id The id.
     * @return The direction default in {@link #SOUTH}.
     * @throws ArrayIndexOutOfBoundsException If {@code id} is out of bounds [0..5]
     */
    public static Direction getById(int id)
        throws ArrayIndexOutOfBoundsException {
        return values()[id];
    }

    /**
     * Get the direction by number id.
     *
     * @param id  The id.
     * @param def The default direction if the id is not in range [0..5]
     * @return The direction.
     */
    public static Direction getById(int id, Direction def) {
        if (id < 0 || id >= values().length)
            return def;
        return values()[id];
    }

    /**
     * Get the direction by name.
     *
     * @param name The direction name. Case-insensitive.
     * @return The direction default in south.
     */
    public static Direction getByName(String name) {
        // Convert to lower case to make case-insensitive
        name = name.toLowerCase(Locale.ROOT);
        return switch (name) {
            case "west" -> WEST;
            case "east" -> EAST;
            case "down" -> DOWN;
            case "up" -> UP;
            case "north" -> NORTH;
            default -> SOUTH;
        };
    }

    /**
     * Get the direction by name, but strict.
     *
     * @param name The direction name. <b>NOT</b> {@link #name()}.
     * @return The direction.
     * @throws IllegalArgumentException If direction not found.
     */
    public static Direction getByNameStrict(String name)
        throws IllegalArgumentException {
        return switch (name) {
            case "west" -> WEST;
            case "east" -> EAST;
            case "down" -> DOWN;
            case "up" -> UP;
            case "north" -> NORTH;
            case "south" -> SOUTH;
            default -> throw new IllegalArgumentException(
                "Can't find direction for name '" + name + "'!");
        };
    }

    /**
     * Get the name of the direction in lower case.
     *
     * @return The direction name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the direction id in enum {@link #ordinal() order}.
     *
     * @return The direction id.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the opposite direction id.
     *
     * @return The id.
     */
    public int getOppositeId() {
        return oppositeId;
    }

    /**
     * Get the opposite direction.
     *
     * @return The direction.
     */
    public Direction opposite() {
        return getById(oppositeId);
    }

    /**
     * Get the horizontal factor.
     *
     * @return The factor.
     */
    public int getHorizontalId() {
        return horizontalId;
    }

    /**
     * Get the rotation in range [0,90,180,270].
     *
     * @return The rotation.
     */
    public int getRotation() {
        return (horizontalId & 3) * 90;
    }

    /**
     * Check if this direction is negative.
     *
     * @return is negative
     */
    public boolean isNegative() {
        return offsetX < 0 || offsetY < 0 || offsetZ < 0;
    }

    /**
     * Check if this direction is positive.
     *
     * @return is positive
     */
    public boolean isPositive() {
        return offsetX > 0 || offsetY > 0 || offsetZ > 0;
    }

    /**
     * Check if this direction is on axis X.
     *
     * @return is on axis X
     */
    public boolean isOnAxisX() {
        return offsetX == -1 || offsetX == 1;
    }

    /**
     * Check if this direction is on axis Y.
     *
     * @return is on axis Y
     */
    public boolean isOnAxisY() {
        return offsetY == -1 || offsetY == 1;
    }

    /**
     * Check if this direction is on axis Z.
     *
     * @return is on axis Z
     */
    public boolean isOnAxisZ() {
        return offsetZ == -1 || offsetZ == 1;
    }

    /**
     * Get the offset X.
     *
     * @return The offset.
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Get the offset Y.
     *
     * @return The offset.
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Get the offset Z.
     *
     * @return The offset.
     */
    public int getOffsetZ() {
        return offsetZ;
    }

    /**
     * Get the normal vector.
     *
     * @return The vector.
     */
    public Vector3i toVectori() {
        return new Vector3i(offsetX, offsetY, offsetZ);
    }

    /**
     * Get the normal vector.
     *
     * @return The vector.
     */
    public Vector3f toVectorf() {
        return new Vector3f(offsetX, offsetY, offsetZ);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Direction.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("id=" + id)
            .add("opposite=" + getById(oppositeId).name)
            .add("rotation=" + getRotation())
            .add("offset=( " + offsetX + " " + offsetY + " " + offsetZ + " )")
            .toString();
    }
}
