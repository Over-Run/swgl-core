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

package org.overrun.swgl.core.util;

/**
 * The pair with 2 objects.
 *
 * @param <L>   the left object
 * @param <R>   the right object
 * @param left  the left object
 * @param right the right object
 * @author squid233
 * @since 0.1.0
 */
public record Pair<L, R>(@Override L left, @Override R right) implements IPair<L, R> {
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public Mutable<L, R> toMutable() {
        return new Mutable<>(left, right);
    }

    /**
     * The mutable pair with 2 objects.
     *
     * @param <L> the left object
     * @param <R> the right object
     * @author squid233
     * @since 0.1.0
     */
    public static class Mutable<L, R> {
        private L left;
        private R right;

        public Mutable(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public Mutable(L left) {
            this.left = left;
        }

        public Mutable() {
        }

        public static <L, R> Mutable<L, R> of(L left, R right) {
            return new Mutable<>(left, right);
        }

        public L left() {
            return left;
        }

        public void left(L left) {
            this.left = left;
        }

        public R right() {
            return right;
        }

        public void right(R right) {
            this.right = right;
        }

        public Pair<L, R> toImmutable() {
            return new Pair<>(left, right);
        }
    }
}
