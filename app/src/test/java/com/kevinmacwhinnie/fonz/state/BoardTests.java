package com.kevinmacwhinnie.fonz.state;

import com.kevinmacwhinnie.fonz.data.Piece;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BoardTests {
    @Test
    public void reset() {
        final Board board = new Board(null);
        for (int i = 0; i < Board.NUMBER_PIES; i++) {
            board.getPie(i).tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN);
        }

        board.reset();

        for (int i = 0; i < Board.NUMBER_PIES; i++) {
            assertThat(board.getPie(i).getPiece(0), is(equalTo(Piece.EMPTY)));
        }
    }
}
