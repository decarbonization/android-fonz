package com.kevinmacwhinnie.fonz.state;

import com.kevinmacwhinnie.fonz.data.Piece;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieTests {
    @Test
    public void constructor() {
        final Pie pie = new Pie();
        for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
            assertThat(pie.getPiece(i), is(equalTo(Piece.EMPTY)));
        }
    }

    @Test
    public void tryPlacePiece() {
        final Pie pie = new Pie();

        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN), is(false));

        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE), is(false));
    }

    @Test
    public void isFull() {
        final Pie pie = new Pie();

        assertThat(pie.isFull(), is(false));

        pie.tryPlacePiece(0, Piece.ORANGE);
        assertThat(pie.isFull(), is(false));

        for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
            pie.tryPlacePiece(i, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));
    }

    @Test
    public void reset() {
        final Pie pie = new Pie();

        assertThat(pie.isFull(), is(false));

        for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
            pie.tryPlacePiece(i, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));

        pie.reset();

        assertThat(pie.isFull(), is(false));
    }

    @Test
    public void isSingleColor() {
        final Pie pie = new Pie();

        assertThat(pie.isSingleColor(), is(false));

        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.PURPLE);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.PURPLE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.PURPLE);

        assertThat(pie.isSingleColor(), is(false));

        pie.reset();

        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.ORANGE);

        assertThat(pie.isSingleColor(), is(true));
    }
}
