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
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Pie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieceFactory {
    public static final int COUNT_PER_COLOR = 30;
    public static final int COUNT_PER_SLOT = 20;

    private final Random random = new Random();
    private final List<Piece> pieces = new ArrayList<>(COUNT_PER_COLOR * Piece.NUMBER_COLORS);
    private final List<Integer> slots = new ArrayList<>(COUNT_PER_SLOT * Pie.NUMBER_SLOTS);

    public PieceFactory() {
        reset();
    }

    public Piece generatePiece() {
        if (pieces.isEmpty()) {
            reset();
        }

        final int position = random.nextInt(pieces.size());
        return pieces.remove(position);
    }

    public @Pie.Slot int generateSlot() {
        if (slots.isEmpty()) {
            reset();
        }

        final int position = random.nextInt(slots.size());
        final @Pie.Slot int slot = slots.remove(position);
        return slot;
    }

    public UpcomingPiece generateUpcomingPiece() {
        return new UpcomingPiece(generatePiece(), generateSlot());
    }

    public void reset() {
        pieces.clear();

        for (final Piece color : Piece.COLORS) {
            for (int i = 0; i < COUNT_PER_COLOR; i++) {
                pieces.add(color);
            }
        }

        slots.clear();
        for (int slot = Pie.SLOT_TOP_LEFT; slot < Pie.NUMBER_SLOTS; slot++) {
            for (int i = 0; i < COUNT_PER_SLOT; i++) {
                slots.add(slot);
            }
        }
    }
}
