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
import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BoardTests extends FonzTestCase {
    private final Bus bus = new Bus();
    private final List<BaseEvent> events = new ArrayList<>();

    private Board board;

    @Before
    public void setUp() {
        this.board = new Board(bus);
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);
        events.clear();
    }

    @Subscribe public void onPowerUpChanged(@NonNull Board.PowerUpChanged change) {
        events.add(change);
    }


    @Test
    public void reset() {
        for (int i = 0; i < Board.NUMBER_PIES; i++) {
            board.getPie(i).tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN);
        }
        board.addPowerUp(PowerUp.CLEAR_ALL);

        board.reset();

        for (int i = 0; i < Board.NUMBER_PIES; i++) {
            assertThat(board.getPie(i).getPiece(Pie.SLOT_TOP_LEFT), is(equalTo(Piece.EMPTY)));
        }
        assertThat(board.hasPowerUp(PowerUp.CLEAR_ALL), is(false));
    }

    @Test
    public void pieHasPowerUp() {
        assertThat(board.pieHasPowerUp(0), is(true));
        assertThat(board.pieHasPowerUp(1), is(true));
        assertThat(board.pieHasPowerUp(2), is(true));
        assertThat(board.pieHasPowerUp(3), is(false));
        assertThat(board.pieHasPowerUp(4), is(false));
        assertThat(board.pieHasPowerUp(5), is(false));
    }

    @Test
    public void addAvailablePowerUp() {
        assertThat(board.addPowerUp(PowerUp.CLEAR_ALL), is(true));
        assertThat(events.size(), is(equalTo(1)));
        assertThat(board.hasPowerUp(PowerUp.CLEAR_ALL), is(true));

        assertThat(board.addPowerUp(PowerUp.CLEAR_ALL), is(false));
        assertThat(events.size(), is(equalTo(1)));
    }

    @Test
    public void usePowerUp() {
        assertThat(board.addPowerUp(PowerUp.CLEAR_ALL), is(true));
        assertThat(board.hasPowerUp(PowerUp.CLEAR_ALL), is(true));
        assertThat(events.size(), is(equalTo(1)));

        assertThat(board.usePowerUp(PowerUp.CLEAR_ALL), is(true));
        assertThat(board.hasPowerUp(PowerUp.CLEAR_ALL), is(false));
        assertThat(events.size(), is(equalTo(2)));

        assertThat(board.usePowerUp(PowerUp.CLEAR_ALL), is(false));
        assertThat(events.size(), is(equalTo(2)));
    }
}
