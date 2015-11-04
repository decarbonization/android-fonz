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

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ScoresStoreTests extends FonzTestCase {
    private final Bus bus = new Bus("ScoresStoreTests#bus");
    private final ScoresStore scoresStore = new ScoresStore(getContext(), bus);
    private final List<BaseEvent> events = new ArrayList<>();

    @Before
    public void setUp() {
        scoresStore.removeOutdatedEntries();
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);
        events.clear();
        scoresStore.clear();
    }

    @Subscribe public void onScoresStoreChanged(@NonNull ScoresStore.Changed event) {
        events.add(event);
    }


    @Test
    public void insert() {
        assertThat(scoresStore.insert("Dina", 500), is(true));
        assertThat(scoresStore.insert("Gene", 900), is(true));
        assertThat(scoresStore.insert("Louise", 9000), is(true));

        assertThat(events.size(), is(equalTo(3)));
    }

    @Test
    public void clear() {
        assertThat(scoresStore.insert("Louise", 9000), is(true));

        final Cursor highScoresBefore = scoresStore.queryHighScores();

        highScoresBefore.moveToNext();
        assertThat(highScoresBefore.getString(ScoresStore.COLUMN_NAME_INDEX),
                   is(equalTo("Louise")));
        assertThat(highScoresBefore.getInt(ScoresStore.COLUMN_SCORE_INDEX),
                   is(equalTo(9000)));
        assertThat(highScoresBefore.getInt(ScoresStore.COLUMN_TIMESTAMP_INDEX),
                   is(not(equalTo(0))));

        highScoresBefore.close();

        scoresStore.clear();
        final Cursor highScoresAfter = scoresStore.queryHighScores();
        while (highScoresAfter.moveToNext()) {
            assertThat(highScoresAfter.getString(ScoresStore.COLUMN_NAME_INDEX),
                       is(equalTo(ScoresStore.NAME_DEFAULT)));
            assertThat(highScoresAfter.getInt(ScoresStore.COLUMN_SCORE_INDEX),
                       is(equalTo(ScoresStore.SCORE_DEFAULT)));
            assertThat(highScoresAfter.getInt(ScoresStore.COLUMN_TIMESTAMP_INDEX),
                       is(not(equalTo(0))));
        }

        assertThat(events.size(), is(equalTo(2)));
    }

    @Test
    public void isNewHighScore() {
        for (int i = 0; i < ScoresStore.SCORE_LIMIT; i++) {
            assertThat(scoresStore.insert("Dina", 500), is(true));
        }

        assertThat(scoresStore.isNewHighScore(499), is(false));
        assertThat(scoresStore.isNewHighScore(300), is(false));
        assertThat(scoresStore.isNewHighScore(20), is(false));

        assertThat(scoresStore.isNewHighScore(501), is(true));
        assertThat(scoresStore.isNewHighScore(800), is(true));
        assertThat(scoresStore.isNewHighScore(9000), is(true));
    }

    @Test
    public void queryHighScores() {
        assertThat(scoresStore.insert("Dina", 500), is(true));
        assertThat(scoresStore.insert("Gene", 800), is(true));
        assertThat(scoresStore.insert("Louise", 9000), is(true));

        final Cursor highScores = scoresStore.queryHighScores();

        highScores.moveToNext();
        assertThat(highScores.getString(ScoresStore.COLUMN_NAME_INDEX),
                   is(equalTo("Louise")));
        assertThat(highScores.getInt(ScoresStore.COLUMN_SCORE_INDEX),
                   is(equalTo(9000)));
        assertThat(highScores.getInt(ScoresStore.COLUMN_TIMESTAMP_INDEX),
                   is(not(equalTo(0))));

        highScores.moveToNext();
        assertThat(highScores.getString(ScoresStore.COLUMN_NAME_INDEX),
                   is(equalTo("Gene")));
        assertThat(highScores.getInt(ScoresStore.COLUMN_SCORE_INDEX),
                   is(equalTo(800)));
        assertThat(highScores.getInt(ScoresStore.COLUMN_TIMESTAMP_INDEX),
                   is(not(equalTo(0))));

        highScores.moveToNext();
        assertThat(highScores.getString(ScoresStore.COLUMN_NAME_INDEX),
                   is(equalTo("Dina")));
        assertThat(highScores.getInt(ScoresStore.COLUMN_SCORE_INDEX),
                   is(equalTo(500)));
        assertThat(highScores.getInt(ScoresStore.COLUMN_TIMESTAMP_INDEX),
                   is(not(equalTo(0))));

        highScores.close();
    }
}
