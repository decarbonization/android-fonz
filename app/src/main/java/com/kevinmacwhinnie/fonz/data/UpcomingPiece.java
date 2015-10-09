package com.kevinmacwhinnie.fonz.data;

import android.support.annotation.NonNull;

public class UpcomingPiece {
    public final Piece piece;
    public final int slot;

    public UpcomingPiece(@NonNull Piece piece, int slot) {
        this.piece = piece;
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "UpcomingPiece{" +
                "piece=" + piece +
                ", slot=" + slot +
                '}';
    }
}
