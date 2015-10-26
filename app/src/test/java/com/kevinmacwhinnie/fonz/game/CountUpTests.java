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

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.Scheduler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountUpTests extends FonzTestCase {
    private final Bus bus = new Bus();
    private int counter = 0;
    private boolean completed = false;

    @Before
    public void setUp() {
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);

        this.counter = 0;
        this.completed = false;
    }

    @Subscribe public void onCountUpTick(@NonNull CountUp.Ticked tick) {
        this.counter++;
    }

    @Subscribe public void onCountUpCompleted(@NonNull CountUp.Completed ignored) {
        this.completed = true;
    }


    @Test
    public void basicCountDown() {
        final CountUp countUp = new CountUp(bus);

        assertThat(countUp.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        countUp.start();

        scheduler.advanceBy(CountUp.DEFAULT_TICK_DURATION_MS * 4);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        scheduler.advanceBy(CountUp.DEFAULT_TICK_DURATION_MS * 6);
        assertThat(counter, is(equalTo(10)));
        assertThat(completed, is(true));
    }

    @Test
    public void pause() {
        final CountUp countUp = new CountUp(bus);

        assertThat(countUp.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        countUp.start();

        scheduler.advanceBy(CountUp.DEFAULT_TICK_DURATION_MS * 4);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        countUp.pause();
        assertThat(countUp.isRunning(), is(true));

        scheduler.advanceBy(CountUp.DEFAULT_TICK_DURATION_MS * 6);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        countUp.resume();
        scheduler.advanceBy(CountUp.DEFAULT_TICK_DURATION_MS * 6);
        assertThat(counter, is(equalTo(10)));
        assertThat(completed, is(true));
    }

    @Test
    public void scaleTickDuration() {
        final CountUp countUp = new CountUp(bus);

        countUp.scaleTickDuration(0.5);
        assertThat(countUp.getTickDuration(), is(equalTo(500L)));

        countUp.scaleTickDuration(0.5);
        assertThat(countUp.getTickDuration(), is(equalTo(450L)));
    }
}
