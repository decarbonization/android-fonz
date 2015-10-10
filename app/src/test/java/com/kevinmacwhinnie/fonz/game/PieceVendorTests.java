package com.kevinmacwhinnie.fonz.game;

import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.state.Pie;

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
        for (int i = 0; i < (PieceFactory.COUNT_PER_COLOR * Piece.NUMBER_COLORS + 1); i++) {
            pieces.add(vendor.generatePiece());
        }

        assertThat(pieces, hasItem(Piece.GREEN));
        assertThat(pieces, hasItem(Piece.ORANGE));
        assertThat(pieces, hasItem(Piece.PURPLE));
    }

    @Test
    public void generateSlot() {
        final PieceFactory vendor = new PieceFactory();

        final List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < (PieceFactory.COUNT_PER_SLOT * Pie.NUMBER_SLOTS + 1); i++) {
            slots.add(vendor.generateSlot());
        }

        assertThat(slots, hasItem(Pie.SLOT_TOP_LEFT));
        assertThat(slots, hasItem(Pie.SLOT_TOP_CENTER));
        assertThat(slots, hasItem(Pie.SLOT_TOP_RIGHT));
        assertThat(slots, hasItem(Pie.SLOT_BOTTOM_LEFT));
        assertThat(slots, hasItem(Pie.SLOT_BOTTOM_CENTER));
        assertThat(slots, hasItem(Pie.SLOT_BOTTOM_RIGHT));
    }
}
