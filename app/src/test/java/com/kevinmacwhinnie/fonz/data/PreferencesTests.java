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

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.game.CountUp;
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

public class PreferencesTests extends FonzTestCase {
    private final SharedPreferences sharedPreferences =
            getContext().getSharedPreferences("PreferencesTests", 0);
    private final Bus bus = new Bus();
    private final Preferences preferences = new Preferences(sharedPreferences, bus);
    private final List<BaseEvent> events = new ArrayList<>();


    @Before
    public void setUp() {
        bus.register(this);
    }

    @SuppressLint("CommitPrefEdits")
    @After
    public void tearDown() {
        bus.unregister(this);
        events.clear();
        sharedPreferences.edit()
                         .clear()
                         .commit();
    }

    @Subscribe public void onSkipOnUpcomingClickChanged(@NonNull Preferences.SkipOnUpcomingClickChanged event) {
        events.add(event);
    }

    @Subscribe public void onPreventDuplicatePiecesChanged(@NonNull Preferences.PreventDuplicatePiecesChanged event) {
        events.add(event);
    }

    @Subscribe public void onTimerScaleFactorChanged(@NonNull Preferences.TimerScaleFactorChanged event) {
        events.add(event);
    }


    @Test
    public void changeEvents() {
        preferences.setPreventDuplicatePieces(false);
        preferences.setSkipOnUpcomingClick(false);
        preferences.setTimerScaleFactor(0.90f);

        assertThat(events.size(), is(equalTo(3)));
        assertThat(events, hasItem(new Preferences.SkipOnUpcomingClickChanged(false)));
        assertThat(events, hasItem(new Preferences.PreventDuplicatePiecesChanged(false)));
        assertThat(events, hasItem(new Preferences.TimerScaleFactorChanged(0.90f)));
    }

    @Test
    public void readingWriting() {
        preferences.setSavedScoreName("Jane");
        assertThat(preferences.getSavedScoreName(), is(equalTo("Jane")));

        preferences.setPreventDuplicatePieces(true);
        assertThat(preferences.getPreventDuplicatePieces(), is(true));

        preferences.setPreventDuplicatePieces(false);
        assertThat(preferences.getPreventDuplicatePieces(), is(false));

        preferences.setSkipOnUpcomingClick(true);
        assertThat(preferences.getSkipOnUpcomingClick(), is(true));

        preferences.setSkipOnUpcomingClick(false);
        assertThat(preferences.getSkipOnUpcomingClick(), is(false));

        preferences.setTimerScaleFactor(0.90f);
        assertThat(preferences.getTimerScaleFactor(), is(equalTo(0.90f)));
    }

    @Test
    public void restoreDefaults() {
        preferences.setPreventDuplicatePieces(false);
        preferences.setSkipOnUpcomingClick(false);
        preferences.setTimerScaleFactor(0.90f);

        assertThat(events.size(), is(equalTo(3)));
        events.clear();

        preferences.restoreDefaults();

        assertThat(events.size(), is(equalTo(3)));

        assertThat(preferences.getPreventDuplicatePieces(), is(equalTo(true)));
        assertThat(preferences.getSkipOnUpcomingClick(), is(equalTo(true)));
        assertThat(preferences.getTimerScaleFactor(), is(equalTo(CountUp.DEFAULT_SCALE_FACTOR)));
    }
}
