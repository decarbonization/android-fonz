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

package com.kevinmacwhinnie.fonz.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

// TODO: persistent representation
public class Scores {
    public static final int NUMBER_SCORES = 12;
    public static final int POSITION_REJECTED = -1;
    public static final int SCORE_DEFAULT = 100;

    public Bus bus;

    private final Context context;
    @VisibleForTesting final List<Entry> entries = new ArrayList<>(NUMBER_SCORES);

    public Scores(@NonNull Context context, @NonNull Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    public void initialize(boolean withDefaults) {
        if (entries.isEmpty() || withDefaults) {
            final Entry placeholder = new Entry(context.getString(R.string.score_name_default),
                                                SCORE_DEFAULT,
                                                System.currentTimeMillis());
            entries.clear();
            for (int i = 0; i < NUMBER_SCORES; i++) {
                entries.add(placeholder);
            }
        }
    }

    public void clear() {
        final Entry placeholder = new Entry("", 0, System.currentTimeMillis());
        entries.clear();
        for (int i = 0; i < NUMBER_SCORES; i++) {
            entries.add(placeholder);
        }

        bus.post(new Cleared());
    }

    public boolean isScoreEligible(int score) {
        for (int i = NUMBER_SCORES - 1; i >= 0; i--) {
            if (getEntry(i).score > score) {
                return false;
            }
        }

        return true;
    }

    public Entry getEntry(int position) {
        return entries.get(position);
    }

    public int trackScore(@NonNull String name, int score) {
        for (int i = 0; i < NUMBER_SCORES; i++) {
            if (score > entries.get(i).score) {
                entries.add(i, new Entry(name, score, System.currentTimeMillis()));
                entries.remove(entries.size() - 1);

                bus.post(new TrackedAtPosition(i));

                return i;
            }
        }

        return POSITION_REJECTED;
    }


    public static class Cleared extends BaseEvent {
        @Override
        public boolean equals(Object o) {
            return (o instanceof Cleared);
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    public static class TrackedAtPosition extends ValueBaseEvent<Integer> {
        public TrackedAtPosition(int value) {
            super(value);
        }
    }


    public static final class Entry {
        public final String name;
        public final int score;
        public final long timestamp;

        Entry(String name, int score, long timestamp) {
            this.name = name;
            this.score = score;
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Entry entry = (Entry) o;
            return (score == entry.score && timestamp == entry.timestamp &&
                    !(name != null ? !name.equals(entry.name) : entry.name != null));
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + score;
            result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "name='" + name + '\'' +
                    ", score=" + score +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
