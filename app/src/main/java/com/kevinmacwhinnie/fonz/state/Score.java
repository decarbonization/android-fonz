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
package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

public class Score {
    public static final int SCORE_PER_PIECE = 5;

    private final Bus bus;
    private int value = 0;
    private float multiplier = 1f;

    public Score(@NonNull Bus bus) {
        this.bus = bus;
    }

    public void addPie(boolean allSameColor) {
        int score = Pie.NUMBER_SLOTS * SCORE_PER_PIECE;
        if (allSameColor) {
            score *= 2;
        }
        this.value += Math.round(score * multiplier);

        bus.post(new Score.Changed(value));
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void reset() {
        this.value = 0;
        this.multiplier = 1f;

        bus.post(new Score.Changed(value));
    }

    public int getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "Score{" +
                "how=" + value +
                ", multiplier=" + multiplier +
                '}';
    }


    public static class Changed extends ValueBaseEvent<Integer> {
        public Changed(Integer value) {
            super(value);
        }
    }
}
