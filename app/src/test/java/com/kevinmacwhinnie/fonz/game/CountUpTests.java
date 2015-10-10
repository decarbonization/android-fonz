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

import com.kevinmacwhinnie.fonz.FonzTestCase;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.Scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountUpTests extends FonzTestCase {
    @Test
    public void basicCountDown() {
        final CountingListener listener = new CountingListener();

        final CountUp countUp = new CountUp();
        countUp.addListener(listener);
        countUp.setTickDuration(10L);
        countUp.setTickCount(5);

        assertThat(countUp.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        countUp.start();

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(4)));
        assertThat(listener.completed.get(), is(false));

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(6)));
        assertThat(listener.completed.get(), is(true));
    }

    @Test
    public void pause() {
        final CountingListener listener = new CountingListener();

        final CountUp countUp = new CountUp();
        countUp.addListener(listener);
        countUp.setTickDuration(10L);
        countUp.setTickCount(5);

        assertThat(countUp.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        countUp.start();

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(4)));
        assertThat(listener.completed.get(), is(false));

        countUp.pause();
        assertThat(countUp.isRunning(), is(true));

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(4)));
        assertThat(listener.completed.get(), is(false));

        countUp.resume();
        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(6)));
        assertThat(listener.completed.get(), is(true));
    }

    @Test
    public void scaleTickDuration() {
        final CountUp countUp = new CountUp();
        countUp.setTickDuration(10L);
        countUp.scaleTickDuration(0.5);

        assertThat(countUp.getTickDuration(), is(equalTo(5L)));
    }


    static class CountingListener implements CountUp.Listener {
        final AtomicBoolean started = new AtomicBoolean(false);
        final AtomicInteger counter = new AtomicInteger(0);
        final AtomicBoolean completed = new AtomicBoolean(false);

        @Override
        public void onStarted() {
            started.set(true);
        }

        @Override
        public void onTicked(long number) {
            counter.incrementAndGet();
        }

        @Override
        public void onCompleted() {
            completed.set(true);
        }
    }
}
