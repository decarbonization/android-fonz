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
package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.kevinmacwhinnie.fonz.Testing.occurrencesOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieTests extends FonzTestCase {
    private final Bus bus = new Bus("test-bus");
    private final List<BaseEvent> events = new ArrayList<>();

    @Before
    public void setUp() {
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);
        events.clear();
    }

    @Subscribe public void onPieChanged(@NonNull Pie.Changed event) {
        events.add(event);
    }


    @Test
    public void constructor() {
        final Pie pie = new Pie(bus);
        for (@Pie.Slot int slot = Pie.SLOT_TOP_LEFT; slot < Pie.NUMBER_SLOTS; slot++) {
            assertThat(pie.getPiece(slot), is(equalTo(Piece.EMPTY)));
        }
    }

    @Test
    public void canPlacePiece() {
        final Pie pie = new Pie(bus);

        assertThat(pie.canPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN), is(true));
        assertThat(pie.canPlacePiece(Pie.SLOT_TOP_LEFT, Piece.PURPLE), is(false));

        assertThat(pie.canPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.ORANGE), is(true));
        assertThat(pie.canPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.GREEN), is(false));
    }

    @Test
    public void tryPlacePiece() {
        final Pie pie = new Pie(bus);

        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN), is(false));

        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE), is(false));

        assertThat(occurrencesOf(events, pie.thisChanged), is(equalTo(2)));
    }

    @Test
    public void isFull() {
        final Pie pie = new Pie(bus);

        assertThat(pie.isFull(), is(false));

        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        assertThat(pie.isFull(), is(false));

        for (@Pie.Slot int slot = Pie.SLOT_TOP_LEFT; slot < Pie.NUMBER_SLOTS; slot++) {
            pie.tryPlacePiece(slot, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));
    }

    @Test
    public void reset() {
        final Pie pie = new Pie(bus);

        assertThat(pie.isFull(), is(false));

        for (@Pie.Slot int slot = Pie.SLOT_TOP_LEFT; slot < Pie.NUMBER_SLOTS; slot++) {
            pie.tryPlacePiece(slot, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));

        pie.reset();

        assertThat(pie.isFull(), is(false));

        assertThat(occurrencesOf(events, pie.thisChanged), is(equalTo(7)));
    }

    @Test
    public void isSingleColor() {
        final Pie pie = new Pie(bus);

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
