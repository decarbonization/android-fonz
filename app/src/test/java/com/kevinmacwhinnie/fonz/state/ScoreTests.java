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

import android.os.Bundle;
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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScoreTests extends FonzTestCase {
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

    @Subscribe public void onScoreChanged(@NonNull Score.Changed event) {
        events.add(event);
    }


    @Test
    public void addDifferentColors() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(30)));
        assertThat(score.getValue(), is(equalTo(30)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(60)));
        assertThat(score.getValue(), is(equalTo(60)));
    }

    @Test
    public void addSameColor() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(true);
        assertThat(events, hasItem(new Score.Changed(60)));
        assertThat(score.getValue(), is(equalTo(60)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(90)));
        assertThat(score.getValue(), is(equalTo(90)));
    }

    @Test
    public void multiplier() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.setMultiplier(3f);
        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(90)));
        assertThat(score.getValue(), is(equalTo(90)));

        score.addPie(true);
        assertThat(events, hasItem(new Score.Changed(270)));
        assertThat(score.getValue(), is(equalTo(270)));
    }

    @Test
    public void reset() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(30)));
        assertThat(score.getValue(), is(equalTo(30)));

        score.reset();
        assertThat(events, hasItem(new Score.Changed(0)));
        assertThat(score.getValue(), is(equalTo(0)));
    }

    @Test
    public void serialization() {
        final Score outScore = new Score(bus);
        outScore.setMultiplier(5f);
        outScore.addPie(true);
        outScore.addPie(true);
        outScore.addPie(true);

        final Bundle savedState = new Bundle();
        outScore.saveState(savedState);

        final Score inScore = new Score(bus);
        inScore.restoreState(savedState);

        assertThat(inScore.getMultiplier(), is(equalTo(outScore.getMultiplier())));
        assertThat(inScore.getValue(), is(equalTo(outScore.getValue())));
    }
}
