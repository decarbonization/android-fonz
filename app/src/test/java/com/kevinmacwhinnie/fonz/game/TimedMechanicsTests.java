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

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.Scheduler;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TimedMechanicsTests extends FonzTestCase {
    private final Bus bus = new Bus();
    private final List<BaseEvent> events = new ArrayList<>();
    private TimedMechanics timedMechanics;

    @Before
    public void setUp() {
        this.timedMechanics = new TimedMechanics(bus);
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);
    }

    @Subscribe public void onPowerUpScheduled(@NonNull TimedMechanics.PowerUpScheduled info) {
        events.add(info);
    }

    @Subscribe public void onPowerUpExpired(@NonNull TimedMechanics.PowerUpExpired expiration) {
        events.add(expiration);
    }


    @Test
    public void schedule() {
        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();

        timedMechanics.schedulePowerUp(PowerUp.SLOW_DOWN_TIME);
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(true));

        scheduler.advanceBy(TimedMechanics.DURATION + 10L);
        assertThat(events.size(), is(equalTo(2)));
        assertThat(events, hasItem(new TimedMechanics.PowerUpScheduled(PowerUp.SLOW_DOWN_TIME)));
        assertThat(events, hasItem(new TimedMechanics.PowerUpExpired(PowerUp.SLOW_DOWN_TIME)));
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(false));
    }

    @Test
    public void reset() {
        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();

        timedMechanics.schedulePowerUp(PowerUp.SLOW_DOWN_TIME);
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(true));
        timedMechanics.reset();

        scheduler.advanceBy(TimedMechanics.DURATION + 10L);
        assertThat(events.size(), is(equalTo(1)));
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(false));
    }

    @Test
    public void pause() {
        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();

        timedMechanics.schedulePowerUp(PowerUp.SLOW_DOWN_TIME);
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(true));

        timedMechanics.pause();

        scheduler.advanceBy(TimedMechanics.DURATION + 10L);
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(true));

        timedMechanics.resume();

        scheduler.advanceBy(TimedMechanics.DURATION + 10L);
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(false));
    }

    @Test
    public void serialization() {
        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();

        timedMechanics.schedulePowerUp(PowerUp.SLOW_DOWN_TIME);
        assertThat(timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME), is(true));

        final Bundle savedState = new Bundle();
        timedMechanics.saveState(savedState);

        final TimedMechanics restored = new TimedMechanics(bus);
        restored.restoreState(savedState);

        assertThat(restored.isPending(PowerUp.SLOW_DOWN_TIME), is(true));
        assertThat(restored.isPending(PowerUp.MULTIPLY_SCORE), is(false));
        assertThat(restored.isPending(PowerUp.CLEAR_ALL), is(false));
    }
}
