package com.kevinmacwhinnie.fonz.view;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.state.Pie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieViewTests extends FonzTestCase {
    private PieView pieView;
    private Pie pie = new Pie(null);

    @Before
    public void setUp() {
        this.pieView = new PieView(getContext());
        pieView.setPie(pie);
    }

    @After
    public void tearDown() {
        pie.reset();
    }


    @Test
    public void generateContentDescriptionEmpty() {
        final String contentDescription = pieView.generateContentDescription().toString();
        assertThat(contentDescription, is(equalTo("An empty puzzle with 6 slots open")));
    }

    @Test
    public void generateContentDescriptionPartiallyFilled() {
        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN);
        assertThat(pieView.generateContentDescription().toString(),
                   is(equalTo("A puzzle with 1 slot filled where the top left slot is green")));

        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.ORANGE);
        assertThat(pieView.generateContentDescription().toString(),
                   is(equalTo("A puzzle with 2 slots filled where the top left slot is green " +
                                      "and the top right slot is orange")));

        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.PURPLE);
        assertThat(pieView.generateContentDescription().toString(),
                   is(equalTo("A puzzle with 3 slots filled where the top left slot is green " +
                                      "and the top right slot is orange " +
                                      "and the bottom center slot is purple")));
    }

    @Test
    public void generateContentDescriptionFull() {
        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.PURPLE);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.PURPLE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.PURPLE);
        assertThat(pieView.generateContentDescription().toString(),
                   is(equalTo("A full puzzle where the slots are of varying colors")));

        pie.reset();

        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.ORANGE);
        assertThat(pieView.generateContentDescription().toString(),
                   is(equalTo("A full puzzle where all slots are the same color")));
    }
}
