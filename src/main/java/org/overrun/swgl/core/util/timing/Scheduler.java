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

package org.overrun.swgl.core.util.timing;

import java.util.function.BooleanSupplier;

/**
 * The scheduler for scheduling tasks.
 *
 * @author squid233
 * @since 0.1.0
 */
public class Scheduler {
    private int currentTick;
    private final int frequency;
    private final BooleanSupplier command;

    public Scheduler(int currentTick,
                     int frequency,
                     final BooleanSupplier command) {
        this.currentTick = currentTick;
        this.frequency = frequency;
        this.command = command;
    }

    public Scheduler(int frequency,
                     final BooleanSupplier command) {
        this(0, frequency, command);
    }

    /**
     * Set the current tick and tick.
     *
     * @param newTick the new tick
     */
    public void tick(int newTick) {
        if (newTick - currentTick >= frequency) {
            if (command.getAsBoolean())
                currentTick = newTick;
        }
    }
}
