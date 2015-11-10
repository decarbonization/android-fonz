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

import com.kevinmacwhinnie.fonz.FonzTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.Scheduler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimerTests extends FonzTestCase {
    private final TestTimer timer = new TestTimer();
    private int counter = 0;
    private boolean completed = false;

    @Before
    public void setUp() {
        timer.setNumberOfTicks(10);
        timer.setTickDurationMs(Timer.DEFAULT_TICK_DURATION_MS);
    }

    @After
    public void tearDown() {
        timer.stop();
        
        this.counter = 0;
        this.completed = false;
    }


    @Test
    public void basicCountUp() {
        assertThat(timer.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        timer.start();

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 4);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 6);
        assertThat(counter, is(equalTo(10)));
        assertThat(completed, is(true));
    }

    @Test
    public void pause() {
        assertThat(timer.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        timer.start();

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 4);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        timer.pause();
        assertThat(timer.isRunning(), is(true));

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 6);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        timer.resume();
        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 6);
        assertThat(counter, is(equalTo(10)));
        assertThat(completed, is(true));
    }

    @Test
    public void serialization() {
        final TestTimer outTimer = new TestTimer();
        outTimer.running = true;
        outTimer.currentTick = 20;

        final Bundle savedState = new Bundle();
        outTimer.saveState(savedState);

        final TestTimer inTimer = new TestTimer();
        inTimer.restoreState(savedState);

        assertThat(inTimer.isRunning(), is(equalTo(outTimer.isRunning())));
        assertThat(inTimer.getCurrentTick(), is(equalTo(outTimer.getCurrentTick())));
        assertThat(inTimer.getTickDurationMs(), is(equalTo(outTimer.getTickDurationMs())));
        assertThat(inTimer.getNumberOfTicks(), is(equalTo(outTimer.getNumberOfTicks())));
    }

    @Test
    public void setTickDuration() {
        assertThat(timer.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        timer.setTickDurationMs(200L);
        timer.start();

        scheduler.advanceBy(200L * 4);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        scheduler.advanceBy(400L * 6);
        assertThat(counter, is(equalTo(10)));
        assertThat(completed, is(true));
    }

    @Test
    public void setNumberOfTicks() {
        assertThat(timer.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        timer.setNumberOfTicks(12);
        timer.start();

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 4);
        assertThat(counter, is(equalTo(5)));
        assertThat(completed, is(false));

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 5);
        assertThat(counter, is(equalTo(10)));
        assertThat(completed, is(false));

        scheduler.advanceBy(Timer.DEFAULT_TICK_DURATION_MS * 3);
        assertThat(counter, is(equalTo(12)));
        assertThat(completed, is(true));
    }
    
    
    class TestTimer extends Timer {
        @Override
        protected void onTick(int tick) {
            TimerTests.this.counter++;
        }

        @Override
        protected void onCompleted() {
            TimerTests.this.completed = true;
        }
    }
}
