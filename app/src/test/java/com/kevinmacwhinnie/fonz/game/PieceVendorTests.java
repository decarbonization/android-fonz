package com.kevinmacwhinnie.fonz.game;

import com.kevinmacwhinnie.fonz.data.Piece;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class PieceVendorTests {
    @Test
    public void generatePiece() {
        final PieceFactory vendor = new PieceFactory();

        final List<Piece> pieces = new ArrayList<>();
        pieces.add(vendor.generatePiece());
        pieces.add(vendor.generatePiece());
        pieces.add(vendor.generatePiece());

        assertThat(pieces, hasItem(Piece.GREEN));
        assertThat(pieces, hasItem(Piece.ORANGE));
        assertThat(pieces, hasItem(Piece.PURPLE));
    }
}
