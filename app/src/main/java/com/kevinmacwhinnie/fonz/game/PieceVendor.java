package com.kevinmacwhinnie.fonz.game;

import com.kevinmacwhinnie.fonz.state.Piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PieceVendor {
    private final Random random = new Random();
    private List<Piece> pieces = new ArrayList<>(3);
    private int pointer = 0;

    public PieceVendor() {
        pieces.add(Piece.GREEN);
        pieces.add(Piece.ORANGE);
        pieces.add(Piece.PURPLE);
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(pieces, random);
        this.pointer = 0;
    }

    public Piece generatePiece() {
        final Piece piece = pieces.get(pointer);
        if (++this.pointer >= pieces.size()) {
            shuffle();
        }
        return piece;
    }
}
