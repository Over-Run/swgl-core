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

package org.overrun.swgl.core.asset.tex.atlas;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Thanks for <a href="https://codeincomplete.com/articles/bin-packing/">this article</a>.
 * <p>
 * Java port of <a href="https://github.com/jakesgordon/bin-packing/blob/master/js/packer.growing.js"><code>GrowingPacker</code></a>.
 * </p>
 * <p>
 * This is a binary tree based bin packing algorithm that is more complex than
 * the simple Packer (packer.js). Instead of starting off with a fixed width and
 * height, it starts with the width and height of the first block passed and
 * then grows as necessary to accommodate each subsequent block. As it grows it
 * attempts to maintain a roughly square ratio by making 'smart' choices about
 * whether to grow right or down.
 * </p>
 * <p>
 * When growing, the algorithm can only grow to the right <b>OR</b> down.
 * Therefore, if the new block is <b>BOTH</b> wider and taller than the current
 * target then it will be rejected. This makes it very important to initialize
 * with a sensible starting width and height. If you are providing sorted input
 * (largest first) then this will not be an issue.
 * </p>
 * <p>
 * A potential way to solve this limitation would be to allow growth in
 * <b>BOTH</b> directions at once, but this requires maintaining a more complex
 * tree with 3 children (down, right and center) and that complexity can be
 * avoided by simply choosing a sensible starting block.
 * </p>
 * <p>
 * Best results occur when the input blocks are sorted by height, or even better
 * when sorted by {@code max(width,height)}.
 * </p>
 * <h2>Inputs</h2>
 * <p>
 * {@code slots}: array of {@link AtlasSpriteSlot}
 * </p>
 * <h2>Outputs</h2>
 * <p>
 * marks each block that fits with a {@link Node}
 * </p>
 * <h2>Example</h2>
 * <pre>{@code
 * var packer = new GrowingPacker();
 * packer.fit(new AtlasSpriteSlot(100, 100),
 *     new AtlasSpriteSlot(100, 100),
 *     new AtlasSpriteSlot(80, 80),
 *     new AtlasSpriteSlot(80, 80),
 *     etc
 *     etc);
 *
 * for (var slot : slots) {
 *     if (slot.fit != null) {
 *         Draw(slot.fit.x, slot.fit.y, slot.w, slot.h);
 *     }
 * }
 * }</pre>
 *
 * @author squid233
 * @since 0.2.0
 */
public class GrowingPacker {
    public static final Comparator<AtlasSpriteSlot> COMPARATOR = Comparator.comparing(slot -> slot);
    public Node root;

    public void fit(AtlasSpriteSlot... slots) {
        Arrays.sort(slots, COMPARATOR);
        Node node;
        int len = slots.length;
        int w, h;
        if (len > 0) {
            var b0 = slots[0];
            w = b0.w;
            h = b0.h;
        } else {
            w = 0;
            h = 0;
        }
        root = new Node(0, 0, w, h);
        for (var slot : slots) {
            if ((node = findNode(root, slot.w, slot.h)) != null) {
                slot.fit = splitNode(node, slot.w, slot.h);
            } else {
                slot.fit = growNode(slot.w, slot.h);
            }
        }
    }

    public void fit(final Collection<AtlasSpriteSlot> slots) {
        fit(new ArrayList<>(slots));
    }

    public void fit(final List<AtlasSpriteSlot> slots) {
        slots.sort(COMPARATOR);
        Node node;
        int len = slots.size();
        int w, h;
        if (len > 0) {
            var b0 = slots.get(0);
            w = b0.w;
            h = b0.h;
        } else {
            w = 0;
            h = 0;
        }
        root = new Node(0, 0, w, h);
        for (var slot : slots) {
            if ((node = findNode(root, slot.w, slot.h)) != null) {
                slot.fit = splitNode(node, slot.w, slot.h);
            } else {
                slot.fit = growNode(slot.w, slot.h);
            }
        }
    }

    @Nullable
    public Node findNode(Node root,
                         int w,
                         int h) {
        if (root.used) {
            var r = findNode(root.right, w, h);
            return r != null ? r : findNode(root.down, w, h);
        }
        if ((w <= root.w) && (h <= root.h)) {
            return root;
        }
        return null;
    }

    public Node splitNode(Node node,
                          int w,
                          int h) {
        node.used = true;
        node.down = new Node(node.x, node.y + h, node.w, node.h - h);
        node.right = new Node(node.x + w, node.y, node.w - w, h);
        return node;
    }

    @Nullable
    public Node growNode(int w,
                         int h) {
        var canGrowDown = w <= root.w;
        var canGrowRight = h <= root.h;
        // attempt to keep square-ish by growing right when height is much greater than width
        var shouldGrowRight = canGrowRight && (root.h >= (root.w + w));
        // attempt to keep square-ish by growing down  when width  is much greater than height
        var shouldGrowDown = canGrowDown && (root.w >= (root.h + h));
        if (shouldGrowRight) {
            return growRight(w, h);
        }
        if (shouldGrowDown) {
            return growDown(w, h);
        }
        if (canGrowRight) {
            return growRight(w, h);
        }
        if (canGrowDown) {
            return growDown(w, h);
        }
        // need to ensure sensible root starting size to avoid this happening
        return null;
    }

    @Nullable
    public Node growRight(int w,
                          int h) {
        root = new Node(true,
            0,
            0,
            root.w + w,
            root.h,
            root,
            new Node(root.w, 0, w, root.h));
        Node node;
        if ((node = findNode(root, w, h)) != null) {
            return splitNode(node, w, h);
        }
        return null;
    }

    @Nullable
    public Node growDown(int w,
                         int h) {
        root = new Node(
            true,
            0,
            0,
            root.w,
            root.h + h,
            new Node(0, root.h, root.w, h),
            root
        );
        Node node;
        if ((node = findNode(root, w, h)) != null) {
            return splitNode(node, w, h);
        }
        return null;
    }
}
