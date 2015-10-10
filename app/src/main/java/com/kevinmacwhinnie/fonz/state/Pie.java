package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;

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


    public final @Nullable Bus bus;

    @VisibleForTesting final Changed thisChanged = new Changed(this);
    private final Piece[] slots;
    private int occupiedSlots = 0;

    public Pie(@Nullable Bus bus) {
        this.bus = bus;
        this.slots = new Piece[NUMBER_SLOTS];
        for (int i = 0; i < NUMBER_SLOTS; i++) {
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

    public Piece getPiece(int offset) {
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
            final Piece first = slots[0];
            for (int i = 1; i < NUMBER_SLOTS; i++) {
                if (slots[i] != first) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        for (int i = 0; i < NUMBER_SLOTS; i++) {
            this.slots[i] = Piece.EMPTY;
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
