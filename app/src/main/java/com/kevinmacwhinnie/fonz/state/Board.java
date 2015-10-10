package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.Nullable;

import com.squareup.otto.Bus;

public class Board {
    public static final int NUMBER_PIES = 6;

    public final @Nullable Bus bus;

    private final Pie[] pies;

    public Board(@Nullable Bus bus) {
        this.bus = bus;
        this.pies = new Pie[NUMBER_PIES];
        for (int i = 0; i < NUMBER_PIES; i++) {
            this.pies[i] = new Pie(bus);
        }
    }

    public Pie getPie(int slot) {
        return pies[slot];
    }

    public void reset() {
        for (int i = 0; i < NUMBER_PIES; i++) {
            pies[i].reset();
        }
    }
}
