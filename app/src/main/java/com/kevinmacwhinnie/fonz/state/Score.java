package com.kevinmacwhinnie.fonz.state;

public class Score {
    public static final int SCORE_PER_PIECE = 5;

    private int accumulator = 0;
    private float multiplier = 1f;


    public void addPieces(int pieces, boolean allSameColor) {
        int score = pieces * SCORE_PER_PIECE;
        if (allSameColor) {
            score *= 2;
        }
        this.accumulator += Math.round(score * multiplier);
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void reset() {
        this.accumulator = 0;
        this.multiplier = 1f;
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
