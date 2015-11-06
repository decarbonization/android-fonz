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

package com.kevinmacwhinnie.fonz.achievements;

import android.support.annotation.StringRes;

import com.kevinmacwhinnie.fonz.R;

/**
 * Achievements possible while playing Fonz.
 */
public enum Achievement {
    /**
     * First time the user completes a pie with a single color.
     */
    FIRST_SINGLE_COLOR_PIE(R.string.achievement_first_single_color_pie),

    /**
     * First time the user unlocks a power up.
     */
    FIRST_POWER_UP(R.string.achievement_first_power_up),

    /**
     * First time the user makes a high score.
     */
    FIRST_HIGH_SCORE(R.string.achievement_first_high_score),

    /**
     * When the user places a piece and the count up timer is on its last tick.
     */
    BEAT_THE_CLOCK(R.string.achievement_beat_the_clock),

    /**
     * First time the user unlocks all power ups.
     */
    ALL_POWER_UPS_UNLOCKED(R.string.achievement_all_power_ups_unlocked),

    /**
     * When the user breaks 20 lives.
     */
    KING_OF_LIVES(R.string.achievement_king_of_lives),

    /**
     * First time the user has high score over 1,000 point.
     */
    SCORE_OVER_1_000(R.string.achievement_score_over_1_000),

    /**
     * First time the user has high score over 10,000 point.
     */
    SCORE_OVER_10_000(R.string.achievement_score_over_10_000),

    /**
     * First time the user has high score over 100,000 point.
     */
    SCORE_OVER_100_000(R.string.achievement_score_over_100_000),

    /**
     * First time the user has high score over 1,000,000 point.
     */
    SCORE_OVER_1_000_000(R.string.achievement_score_over_1_000_000);


    /**
     * The human readable name of the achievement.
     */
    public final @StringRes int name;

    Achievement(@StringRes int name) {
        this.name = name;
    }


    /**
     * The number of lives required for {@link #KING_OF_LIVES}.
     */
    public static final int KING_OF_LIVES_THRESHOLD = 20;
}
