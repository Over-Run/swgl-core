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
 * The tri with 3 objects.
 *
 * @param <L>    the left object
 * @param <M>    the middle object
 * @param <R>    the right object
 * @param left   the left object
 * @param middle the middle object
 * @param right  the right object
 * @author squid233
 * @since 0.1.0
 */
public record Tri<L, M, R>(L left, M middle, R right) {
    public static <L, M, R> Tri<L, M, R> of(L left, M middle, R right) {
        return new Tri<>(left, middle, right);
    }

    public Mutable<L, M, R> toMutable() {
        return new Mutable<>(left, middle, right);
    }

    /**
     * The mutable tri with 3 objects.
     *
     * @param <L> the left object
     * @param <M> the middle object
     * @param <R> the right object
     * @author squid233
     * @since 0.1.0
     */
    public static class Mutable<L, M, R> {
        private L left;
        private M middle;
        private R right;

        public Mutable(L left, M middle, R right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }

        public Mutable(L left, M middle) {
            this.left = left;
            this.middle = middle;
        }

        public static <L, M, R> Mutable<L, M, R> of(L left, M middle, R right) {
            return new Mutable<>(left, middle, right);
        }

        public Mutable(L left) {
            this.left = left;
        }

        public Mutable() {
        }

        public L left() {
            return left;
        }

        public void left(L left) {
            this.left = left;
        }

        public M middle() {
            return middle;
        }

        public void middle(M middle) {
            this.middle = middle;
        }

        public R right() {
            return right;
        }

        public void right(R right) {
            this.right = right;
        }

        public Tri<L, M, R> toImmutable() {
            return new Tri<>(left, middle, right);
        }
    }
}
