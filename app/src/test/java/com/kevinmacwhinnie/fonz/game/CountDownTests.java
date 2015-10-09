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

public class CountDownTests extends FonzTestCase {
    @Test
    public void basicCountDown() {
        final CountingListener listener = new CountingListener();

        final CountDown countDown = new CountDown(listener);
        countDown.setTickDuration(10L);
        countDown.setTickCount(5);

        assertThat(countDown.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        countDown.start();

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

        final CountDown countDown = new CountDown(listener);
        countDown.setTickDuration(10L);
        countDown.setTickCount(5);

        assertThat(countDown.isRunning(), is(false));

        final Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
        scheduler.pause();
        countDown.start();

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(3)));
        assertThat(listener.completed.get(), is(false));

        countDown.pause();
        assertThat(countDown.isRunning(), is(true));

        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(3)));
        assertThat(listener.completed.get(), is(false));

        countDown.start();
        scheduler.advanceBy(31L);
        assertThat(listener.counter.get(), is(equalTo(5)));
        assertThat(listener.completed.get(), is(true));
    }


    static class CountingListener implements CountDown.Listener {
        final AtomicInteger counter = new AtomicInteger(0);
        final AtomicBoolean completed = new AtomicBoolean(false);

        @Override
        public void onTicked() {
            counter.incrementAndGet();
        }

        @Override
        public void onCompleted() {
            completed.set(true);
        }
    }
}
