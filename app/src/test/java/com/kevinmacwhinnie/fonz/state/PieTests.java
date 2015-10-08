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

        assertThat(pie.tryPlacePiece(0, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(0, Piece.GREEN), is(false));

        assertThat(pie.tryPlacePiece(1, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(1, Piece.ORANGE), is(false));
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
    public void isSingleColor() {
        final Pie pie = new Pie();

        assertThat(pie.isSingleColor(), is(false));

        pie.tryPlacePiece(0, Piece.ORANGE);
        pie.tryPlacePiece(2, Piece.PURPLE);
        pie.tryPlacePiece(3, Piece.GREEN);
        pie.tryPlacePiece(4, Piece.GREEN);
        pie.tryPlacePiece(5, Piece.PURPLE);

        assertThat(pie.isSingleColor(), is(false));

        pie.drain();

        pie.tryPlacePiece(0, Piece.ORANGE);
        pie.tryPlacePiece(1, Piece.ORANGE);
        pie.tryPlacePiece(2, Piece.ORANGE);
        pie.tryPlacePiece(3, Piece.ORANGE);
        pie.tryPlacePiece(4, Piece.ORANGE);
        pie.tryPlacePiece(5, Piece.ORANGE);

        assertThat(pie.isSingleColor(), is(true));
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
}
