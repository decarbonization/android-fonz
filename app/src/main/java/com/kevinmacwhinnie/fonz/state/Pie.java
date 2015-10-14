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

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public class Pie {
    public static final int NUMBER_SLOTS = 6;

    public static final int SLOT_TOP_LEFT = 0;
    public static final int SLOT_TOP_CENTER = 1;
    public static final int SLOT_TOP_RIGHT = 2;
    public static final int SLOT_BOTTOM_LEFT = 3;
    public static final int SLOT_BOTTOM_CENTER = 4;
    public static final int SLOT_BOTTOM_RIGHT = 5;

    public static final @StringRes int SLOT_ACCESSIBILITY_NAME_FORMATS[] = {
            R.string.accessibility_pie_slot_top_left_fmt,
            R.string.accessibility_pie_slot_top_center_fmt,
            R.string.accessibility_pie_slot_top_right_fmt,
            R.string.accessibility_pie_slot_bottom_left_fmt,
            R.string.accessibility_pie_slot_bottom_center_fmt,
            R.string.accessibility_pie_slot_bottom_right_fmt,
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SLOT_TOP_LEFT, SLOT_TOP_CENTER, SLOT_TOP_RIGHT,
             SLOT_BOTTOM_LEFT, SLOT_BOTTOM_CENTER, SLOT_BOTTOM_RIGHT,
             NUMBER_SLOTS})
    public @interface Slot {}


    public final @Nullable Bus bus;

    @VisibleForTesting final Changed thisChanged = new Changed(this);
    private final Piece[] slots;
    private int occupiedSlots = 0;

    public Pie(@Nullable Bus bus) {
        this.bus = bus;
        this.slots = new Piece[NUMBER_SLOTS];
        for (@Slot int i = SLOT_TOP_LEFT; i < NUMBER_SLOTS; i++) {
            this.slots[i] = Piece.EMPTY;
        }
    }

    public boolean tryPlacePiece(int slot, @NonNull Piece piece) {
        if (occupiedSlots == NUMBER_SLOTS) {
            return false;
        }

        if (slots[slot] == Piece.EMPTY) {
            slots[slot] = piece;
            this.occupiedSlots++;

            if (bus != null) {
                bus.post(thisChanged);
            }
            return true;
        } else {
            return false;
        }
    }

    public Piece getPiece(@Slot int offset) {
        return slots[offset];
    }

    public int getOccupiedSlotCount() {
        return occupiedSlots;
    }

    public boolean isFull() {
        return (occupiedSlots == NUMBER_SLOTS);
    }

    public boolean isSingleColor() {
        if (isFull()) {
            final Piece first = slots[SLOT_TOP_LEFT];
            for (@Slot int slot = SLOT_TOP_CENTER; slot < NUMBER_SLOTS; slot++) {
                if (slots[slot] != first) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        for (@Slot int slot = 0; slot < NUMBER_SLOTS; slot++) {
            this.slots[slot] = Piece.EMPTY;
        }
        this.occupiedSlots = 0;

        if (bus != null) {
            bus.post(thisChanged);
        }
    }


    @Override
    public String toString() {
        return "Pie{" +
                "slots=" + Arrays.toString(slots) +
                ", isFull=" + isFull() +
                '}';
    }


    public static class Changed extends BaseEvent {
        public final Pie from;

        Changed(@NonNull Pie from) {
            this.from = from;
        }
    }
}
