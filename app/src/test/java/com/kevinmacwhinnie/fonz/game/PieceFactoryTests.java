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
package com.kevinmacwhinnie.fonz.game;

import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.state.Pie;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class PieceFactoryTests {
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
