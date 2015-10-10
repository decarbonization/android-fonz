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
        assertThat(listener.counter.get(), is(equalTo(3)));
        assertThat(listener.completed.get(), is(false));

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(5)));
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
        assertThat(listener.counter.get(), is(equalTo(3)));
        assertThat(listener.completed.get(), is(false));

        countUp.pause();
        assertThat(countUp.isRunning(), is(true));

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(3)));
        assertThat(listener.completed.get(), is(false));

        countUp.resume();
        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(5)));
        assertThat(listener.completed.get(), is(true));
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
