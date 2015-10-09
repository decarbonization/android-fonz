package com.kevinmacwhinnie.fonz.state;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieTests {
    @Test
    public void constructor() {
        final Pie pie = new Pie();
        for (int i = 0; i < Pie.NUMBER_PIECES; i++) {
            assertThat(pie.getPiece(i), is(equalTo(Piece.NONE)));
        }
    }

    @Test
    public void tryPlacePiece() {
        final Pie pie = new Pie();

        assertThat(pie.tryPlacePiece(Pie.PIECE_TOP_LEFT, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.PIECE_TOP_LEFT, Piece.GREEN), is(false));

        assertThat(pie.tryPlacePiece(Pie.PIECE_TOP_CENTER, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.PIECE_TOP_CENTER, Piece.ORANGE), is(false));
    }

    @Test
    public void isFull() {
        final Pie pie = new Pie();

        assertThat(pie.isFull(), is(false));

        pie.tryPlacePiece(0, Piece.ORANGE);
        assertThat(pie.isFull(), is(false));

        for (int i = 0; i < Pie.NUMBER_PIECES; i++) {
            pie.tryPlacePiece(i, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));
    }

    @Test
    public void drain() {
        final Pie pie = new Pie();

        assertThat(pie.isFull(), is(false));

        for (int i = 0; i < Pie.NUMBER_PIECES; i++) {
            pie.tryPlacePiece(i, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));

        pie.drain();

        assertThat(pie.isFull(), is(false));
    }

    @Test
    public void isSingleColor() {
        final Pie pie = new Pie();

        assertThat(pie.isSingleColor(), is(false));

        pie.tryPlacePiece(Pie.PIECE_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.PIECE_TOP_CENTER, Piece.PURPLE);
        pie.tryPlacePiece(Pie.PIECE_TOP_RIGHT, Piece.PURPLE);
        pie.tryPlacePiece(Pie.PIECE_BOTTOM_LEFT, Piece.GREEN);
        pie.tryPlacePiece(Pie.PIECE_BOTTOM_CENTER, Piece.GREEN);
        pie.tryPlacePiece(Pie.PIECE_BOTTOM_RIGHT, Piece.PURPLE);

        assertThat(pie.isSingleColor(), is(false));

        pie.drain();

        pie.tryPlacePiece(Pie.PIECE_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.PIECE_TOP_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.PIECE_TOP_RIGHT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.PIECE_BOTTOM_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.PIECE_BOTTOM_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.PIECE_BOTTOM_RIGHT, Piece.ORANGE);

        assertThat(pie.isSingleColor(), is(true));
    }
}
