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

import com.kevinmacwhinnie.fonz.FonzTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ScoresTests extends FonzTestCase {
    private Scores scores;

    @Before
    public void setUp() {
        this.scores = new Scores(getContext());
        scores.initialize(true);
    }

    @After
    public void tearDown() {
        scores.clear();
    }


    @Test
    public void initialize() {
        for (int i = 0; i < Scores.NUMBER_SCORES; i++) {
            final Scores.Entry entry = scores.getEntry(i);
            assertThat(entry, is(notNullValue()));
            assertThat(entry.name, is(notNullValue()));
            assertThat(entry.score, is(not(equalTo(0))));
            assertThat(entry.timestamp, is(not(equalTo(0L))));
        }
    }

    @Test
    public void clear() {
        for (int i = 0; i < Scores.NUMBER_SCORES; i++) {
            final Scores.Entry entry = scores.getEntry(i);
            assertThat(entry, is(notNullValue()));
            assertThat(entry.score, is(not(equalTo(0))));
        }

        scores.clear();
        for (int i = 0; i < Scores.NUMBER_SCORES; i++) {
            final Scores.Entry entry = scores.getEntry(i);
            assertThat(entry, is(notNullValue()));
            assertThat(entry.score, is(equalTo(0)));
        }
    }

    @Test
    public void trackScoreQualified() {
        final int firstPosition = scores.trackScore("John Doe", Scores.SCORE_DEFAULT * 2);
        assertThat(firstPosition, is(not(equalTo(Scores.POSITION_REJECTED))));
        assertThat(firstPosition, is(equalTo(0)));
        assertThat(scores.entries.size(), is(equalTo(Scores.NUMBER_SCORES)));

        final int secondPosition = scores.trackScore("John Doe", Math.round(Scores.SCORE_DEFAULT * 1.5f));
        assertThat(secondPosition, is(not(equalTo(Scores.POSITION_REJECTED))));
        assertThat(secondPosition, is(equalTo(1)));
        assertThat(scores.entries.size(), is(equalTo(Scores.NUMBER_SCORES)));
    }

    @Test
    public void trackScoreUnqualified() {
        assertThat(scores.trackScore("John Doe", Scores.SCORE_DEFAULT / 2),
                   is(equalTo(Scores.POSITION_REJECTED)));
    }
}
