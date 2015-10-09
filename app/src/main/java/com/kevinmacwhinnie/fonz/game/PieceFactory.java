package com.kevinmacwhinnie.fonz.game;

import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Pie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieceFactory {
    private final Random random = new Random();
    private final List<Piece> pieces = new ArrayList<>(3);

    public PieceFactory() {
        pieces.add(Piece.GREEN);
        pieces.add(Piece.ORANGE);
        pieces.add(Piece.PURPLE);
    }

    public Piece generatePiece() {
        return pieces.get(random.nextInt(pieces.size()));
    }

    public int generateSlot() {
        return random.nextInt(Pie.NUMBER_SLOTS);
    }

    public UpcomingPiece generateUpcomingPiece() {
        return new UpcomingPiece(generatePiece(), generateSlot());
    }
}
