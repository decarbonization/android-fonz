package com.kevinmacwhinnie.fonz.state;

import java.util.Observable;

public class Score extends Observable {
    public static final int SCORE_PER_PIECE = 5;

    private int accumulator = 0;
    private float multiplier = 1f;

    @Override
    public boolean hasChanged() {
        return true;
    }


    public void addPie(boolean allSameColor) {
        int score = Pie.NUMBER_SLOTS * SCORE_PER_PIECE;
        if (allSameColor) {
            score *= 2;
        }
        this.accumulator += Math.round(score * multiplier);

        notifyObservers();
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void reset() {
        this.accumulator = 0;
        this.multiplier = 1f;

        notifyObservers();
    }

    public int getValue() {
        return accumulator;
    }


    @Override
    public String toString() {
        return "Score{" +
                "accumulator=" + accumulator +
                ", multiplier=" + multiplier +
                '}';
    }
}
