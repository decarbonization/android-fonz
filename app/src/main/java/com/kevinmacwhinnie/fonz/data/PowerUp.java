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

public enum PowerUp {
    // Must match layout of board.
    MULTIPLY_SCORE(0, R.drawable.power_up_2x, R.string.accessibility_power_up_2x_score),
    CLEAR_ALL(1, R.drawable.power_up_clear_all, R.string.accessibility_power_up_clear_all),
    SLOW_DOWN_TIME(2, R.drawable.power_up_timer, R.string.accessibility_power_up_slow_down_time);

    /**
     * The slot the power up is attached to.
     */
    public final int boardSlot;

    /**
     * The icon for the power up.
     */
    public final @DrawableRes int iconRes;

    /**
     * The accessibility description for the power up.
     */
    public final @StringRes int accessibilityRes;

    /**
     * Looks up the power up matching a given slot.
     * @param boardSlot The slot to find the power up for.
     * @return The power up.
     * @throws IllegalArgumentException if no power up matches the slot.
     */
    public static PowerUp forSlot(int boardSlot) {
        for (final PowerUp powerUp : values()) {
            if (powerUp.boardSlot == boardSlot) {
                return powerUp;
            }
        }

        throw new IllegalArgumentException("No power up for board slot " + boardSlot);
    }

    PowerUp(int boardSlot,
            @DrawableRes int iconRes,
            @StringRes int accessibilityRes) {
        this.boardSlot = boardSlot;
        this.iconRes = iconRes;
        this.accessibilityRes = accessibilityRes;
    }
}
