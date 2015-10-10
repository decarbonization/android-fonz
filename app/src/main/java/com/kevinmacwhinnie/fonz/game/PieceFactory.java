package com.kevinmacwhinnie.fonz.game;

import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Pie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieceFactory {
    public static final int COUNT_PER_COLOR = 30;
    public static final int COUNT_PER_SLOT = 20;

    private final Random random = new Random();
    private final List<Piece> pieces = new ArrayList<>(COUNT_PER_COLOR * Piece.NUMBER_COLORS);
    private final List<Integer> slots = new ArrayList<>(COUNT_PER_SLOT * Pie.NUMBER_SLOTS);

    public PieceFactory() {
        reset();
    }

    public Piece generatePiece() {
        if (pieces.isEmpty()) {
            reset();
        }

        final int position = random.nextInt(pieces.size());
        return pieces.remove(position);
    }

    public int generateSlot() {
        if (slots.isEmpty()) {
            reset();
        }

        final int position = random.nextInt(slots.size());
        return slots.remove(position);
    }

    public UpcomingPiece generateUpcomingPiece() {
        return new UpcomingPiece(generatePiece(), generateSlot());
    }

    public void reset() {
        pieces.clear();

        for (final Piece color : Piece.COLORS) {
            for (int i = 0; i < COUNT_PER_COLOR; i++) {
                pieces.add(color);
            }
        }

        slots.clear();
        for (int slot = Pie.SLOT_TOP_LEFT; slot < Pie.NUMBER_SLOTS; slot++) {
            for (int i = 0; i < COUNT_PER_SLOT; i++) {
                slots.add(slot);
            }
        }
    }
}
