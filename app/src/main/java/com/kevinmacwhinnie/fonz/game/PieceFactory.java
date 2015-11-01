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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.data.GamePersistence;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Pie;

import java.util.ArrayList;
import java.util.Random;

public class PieceFactory implements GamePersistence {
    static final String SAVED_SEED = PieceFactory.class.getName() + ".SAVED_SEED";
    static final String SAVED_PIECES = PieceFactory.class.getName() + ".SAVED_PIECES";
    static final String SAVED_SLOTS = PieceFactory.class.getName() + ".SAVED_SLOTS";
    static final String SAVED_LAST_SLOT = PieceFactory.class.getName() + ".SAVED_LAST_SLOT";


    public static final int COUNT_PER_COLOR = 30;
    public static final int COUNT_PER_SLOT = 20;

    @VisibleForTesting long seed = System.nanoTime();
    @VisibleForTesting final Random random = new Random(seed);
    @VisibleForTesting final ArrayList<Piece> pieces =
            new ArrayList<>(COUNT_PER_COLOR * Piece.NUMBER_COLORS);
    @VisibleForTesting final ArrayList<Integer> slots =
            new ArrayList<>(COUNT_PER_SLOT * Pie.NUMBER_SLOTS);

    @VisibleForTesting @Pie.Slot int lastSlot = Pie.NUMBER_SLOTS;
    private boolean preventDuplicatePieces = true;

    public PieceFactory() {
        populatePieces();
        populateSlots();
    }

    @Override
    public void restoreState(@NonNull Bundle inState) {
        this.seed = inState.getLong(SAVED_SEED);
        random.setSeed(seed);

        @SuppressWarnings("unchecked")
        final ArrayList<Piece> savedPieces =
                (ArrayList<Piece>) inState.getSerializable(SAVED_PIECES);
        assert savedPieces != null;
        pieces.clear();
        pieces.addAll(savedPieces);

        @SuppressWarnings("unchecked")
        final ArrayList<Integer> savedSlots =
                (ArrayList<Integer>) inState.getSerializable(SAVED_SLOTS);
        assert savedSlots != null;
        slots.clear();
        slots.addAll(savedSlots);

        //noinspection ResourceType
        this.lastSlot = inState.getInt(SAVED_LAST_SLOT);
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putLong(SAVED_SEED, seed);
        outState.putSerializable(SAVED_PIECES, pieces);
        outState.putSerializable(SAVED_SLOTS, slots);
        outState.putInt(SAVED_LAST_SLOT, lastSlot);
    }

    public Piece generatePiece() {
        if (pieces.isEmpty()) {
            populatePieces();
        }

        final int position = random.nextInt(pieces.size());
        return pieces.remove(position);
    }

    public @Pie.Slot int generateSlot() {
        if (slots.isEmpty()) {
            populateSlots();
        }

        final int position = random.nextInt(slots.size());
        final @Pie.Slot int slot = slots.remove(position);
        return slot;
    }

    public @Pie.Slot int generateUniqueSlot() {
        @Pie.Slot int slot;
        //noinspection StatementWithEmptyBody
        while ((slot = generateSlot()) == lastSlot);
        this.lastSlot = slot;
        return slot;
    }

    public UpcomingPiece generateUpcomingPiece() {
        if (preventDuplicatePieces) {
            return new UpcomingPiece(generatePiece(), generateUniqueSlot());
        } else {
            return new UpcomingPiece(generatePiece(), generateSlot());
        }
    }

    public void setPreventDuplicatePieces(boolean preventDuplicatePieces) {
        this.preventDuplicatePieces = preventDuplicatePieces;
    }

    public void reset() {
        pieces.clear();
        populatePieces();

        slots.clear();
        populateSlots();

        this.lastSlot = Pie.NUMBER_SLOTS;
    }

    private void populatePieces() {
        for (final Piece color : Piece.COLORS) {
            for (int i = 0; i < COUNT_PER_COLOR; i++) {
                pieces.add(color);
            }
        }
    }

    private void populateSlots() {
        for (int slot = Pie.SLOT_TOP_LEFT; slot < Pie.NUMBER_SLOTS; slot++) {
            for (int i = 0; i < COUNT_PER_SLOT; i++) {
                slots.add(slot);
            }
        }
    }
}
