/*
 * Copyright (c) 2015, Peter 'Kevin' MacWhinnie
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions may not be sold, nor may they be used in a commercial
 *    product or activity.
 * 2. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 3. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.kevinmacwhinnie.fonz.data;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.state.Pie;

public enum Piece {
    ORANGE(R.string.accessibility_piece_orange) {
        @Override
        public @DrawableRes int getSlotDrawable(@Pie.Slot int forSlot) {
            switch (forSlot) {
                case Pie.SLOT_TOP_LEFT:
                    return R.drawable.piece_top_left_orange;
                case Pie.SLOT_TOP_CENTER:
                    return R.drawable.piece_top_center_orange;
                case Pie.SLOT_TOP_RIGHT:
                    return R.drawable.piece_top_right_orange;
                case Pie.SLOT_BOTTOM_LEFT:
                    return R.drawable.piece_bottom_left_orange;
                case Pie.SLOT_BOTTOM_CENTER:
                    return R.drawable.piece_bottom_center_orange;
                case Pie.SLOT_BOTTOM_RIGHT:
                    return R.drawable.piece_bottom_right_orange;
                default:
                    throw new IllegalArgumentException("unknown slot " + forSlot);
            }
        }
    },
    GREEN(R.string.accessibility_piece_green) {
        @Override
        public @DrawableRes int getSlotDrawable(@Pie.Slot int forSlot) {
            switch (forSlot) {
                case Pie.SLOT_TOP_LEFT:
                    return R.drawable.piece_top_left_green;
                case Pie.SLOT_TOP_CENTER:
                    return R.drawable.piece_top_center_green;
                case Pie.SLOT_TOP_RIGHT:
                    return R.drawable.piece_top_right_green;
                case Pie.SLOT_BOTTOM_LEFT:
                    return R.drawable.piece_bottom_left_green;
                case Pie.SLOT_BOTTOM_CENTER:
                    return R.drawable.piece_bottom_center_green;
                case Pie.SLOT_BOTTOM_RIGHT:
                    return R.drawable.piece_bottom_right_green;
                default:
                    throw new IllegalArgumentException("unknown slot " + forSlot);
            }
        }
    },
    PURPLE(R.string.accessibility_piece_purple) {
        @Override
        public @DrawableRes int getSlotDrawable(@Pie.Slot int forSlot) {
            switch (forSlot) {
                case Pie.SLOT_TOP_LEFT:
                    return R.drawable.piece_top_left_purple;
                case Pie.SLOT_TOP_CENTER:
                    return R.drawable.piece_top_center_purple;
                case Pie.SLOT_TOP_RIGHT:
                    return R.drawable.piece_top_right_purple;
                case Pie.SLOT_BOTTOM_LEFT:
                    return R.drawable.piece_bottom_left_purple;
                case Pie.SLOT_BOTTOM_CENTER:
                    return R.drawable.piece_bottom_center_purple;
                case Pie.SLOT_BOTTOM_RIGHT:
                    return R.drawable.piece_bottom_right_purple;
                default:
                    throw new IllegalArgumentException("unknown slot " + forSlot);
            }
        }
    },
    EMPTY(R.string.accessibility_piece_empty) {
        @Override
        public @DrawableRes int getSlotDrawable(@Pie.Slot int forSlot) {
            return 0;
        }
    };

    public static final int NUMBER_COLORS = 3;
    public static final Piece[] COLORS = { ORANGE, GREEN, PURPLE };

    public final @StringRes int accessibilityName;
    public abstract @DrawableRes int getSlotDrawable(@Pie.Slot int forSlot);

    Piece(@StringRes int accessibilityName) {
        this.accessibilityName = accessibilityName;
    }
}
