package com.kevinmacwhinnie.fonz.state;

public class Board {
    public static final int NUMBER_PIES = 6;

    private final Pie[] pies;

    public Board() {
        this.pies = new Pie[NUMBER_PIES];
        for (int i = 0; i < NUMBER_PIES; i++) {
            this.pies[i] = new Pie();
        }
    }

    public Pie getPie(int offset) {
        return pies[offset];
    }

    public void reset() {
        for (int i = 0; i < NUMBER_PIES; i++) {
            pies[i].drain();
        }
    }
}
