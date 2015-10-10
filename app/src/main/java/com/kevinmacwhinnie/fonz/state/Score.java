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
                "value=" + value +
                ", multiplier=" + multiplier +
                '}';
    }


    public static class Changed extends ValueBaseEvent<Integer> {
        public Changed(Integer value) {
            super(value);
        }
    }
}
