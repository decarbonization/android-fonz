/*
 * Copyright (c) 2015, Peter 'Kevin' MacWhinnie
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions may not be sold, nor may they be used in a commercial
 *    product or activity.
 * 2. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 3. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
