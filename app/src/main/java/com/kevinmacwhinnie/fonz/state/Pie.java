package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;

import java.util.Arrays;

public class Pie {
    public static final int NUMBER_PIECES = 6;
    public static final int PIECE_TOP_LEFT = 0;
    public static final int PIECE_TOP_CENTER = 1;
    public static final int PIECE_TOP_RIGHT = 2;
    public static final int PIECE_BOTTOM_LEFT = 3;
    public static final int PIECE_BOTTOM_CENTER = 4;
    public static final int PIECE_BOTTOM_RIGHT = 5;


    private final Piece[] slots;
    private int occupiedSlots = 0;

    public Pie() {
        this.slots = new Piece[NUMBER_PIECES];
        for (int i = 0; i < NUMBER_PIECES; i++) {
            this.slots[i] = Piece.NONE;
        }
    }

    public boolean tryPlacePiece(int offset, @NonNull Piece piece) {
        if (occupiedSlots == NUMBER_PIECES) {
            return false;
        }

        if (slots[offset] == Piece.NONE) {
            slots[offset] = piece;
            this.occupiedSlots++;
            return true;
        } else {
            return false;
        }
    }

    public Piece getPiece(int offset) {
        return slots[offset];
    }

    public boolean isFull() {
        return (occupiedSlots == NUMBER_PIECES);
    }

    public boolean isSingleColor() {
        if (isFull()) {
            final Piece first = slots[0];
            for (int i = 1; i < NUMBER_PIECES; i++) {
                if (slots[i] != first) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void drain() {
        for (int i = 0; i < NUMBER_PIECES; i++) {
            this.slots[i] = Piece.NONE;
        }
        this.occupiedSlots = 0;
    }


    @Override
    public String toString() {
        return "Pie{" +
                "slots=" + Arrays.toString(slots) +
                ", isFull=" + isFull() +
                '}';
    }
}
