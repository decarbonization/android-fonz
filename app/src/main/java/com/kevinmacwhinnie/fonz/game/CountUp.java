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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.data.GamePersistence;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;

public class CountUp implements Handler.Callback, GamePersistence {
    static final String SAVED_RUNNING = CountUp.class.getName() + ".SAVED_RUNNING";
    static final String SAVED_TICK_DURATION = CountUp.class.getName() + ".SAVED_TICK_DURATION";
    static final String SAVED_CURRENT = CountUp.class.getName() + ".SAVED_CURRENT";

    public static final int NUMBER_TICKS = 10;
    public static final long DEFAULT_TICK_DURATION_MS = 1000L;
    public static final long MIN_TICK_DURATION = 450L;
    public static final double DEFAULT_SCALE_FACTOR = 0.99;

    private static final int MSG_TICK = 0;

    private final Bus bus;
    private final Handler handler;

    @VisibleForTesting boolean running = false;
    @VisibleForTesting int tickCurrent = 0;
    @VisibleForTesting long tickDuration = DEFAULT_TICK_DURATION_MS;

    public CountUp(@NonNull Bus bus) {
        this.bus = bus;
        this.handler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public void restoreState(@NonNull Bundle inState) {
        this.running = inState.getBoolean(SAVED_RUNNING);
        this.tickCurrent = inState.getInt(SAVED_CURRENT);
        this.tickDuration = inState.getLong(SAVED_TICK_DURATION);
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putBoolean(SAVED_RUNNING, running);
        outState.putInt(SAVED_CURRENT, tickCurrent);
        outState.putLong(SAVED_TICK_DURATION, tickDuration);
    }

    public void scaleTickDuration(double factor) {
        this.tickDuration = Math.round(tickDuration * factor);
        if (tickDuration < MIN_TICK_DURATION) {
            this.tickDuration = MIN_TICK_DURATION;
        }

        handler.removeMessages(MSG_TICK);
        if (running) {
            handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
        }
    }

    public long getTickDuration() {
        return tickDuration;
    }

    public int getTickCurrent() {
        return tickCurrent;
    }

    public void start() {
        this.running = true;

        this.tickCurrent = 1;

        handler.removeMessages(MSG_TICK);
        handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);

        bus.post(Ticked.acquire(tickCurrent));
    }

    public void resume() {
        handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
    }

    public void pause() {
        handler.removeMessages(MSG_TICK);
    }

    public boolean isRunning() {
        return running;
    }

    public void reset() {
        handler.removeMessages(MSG_TICK);

        this.running = false;
        this.tickDuration = DEFAULT_TICK_DURATION_MS;
        this.tickCurrent = 1;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TICK: {
                if (++this.tickCurrent <= NUMBER_TICKS) {
                    bus.post(Ticked.acquire(tickCurrent));
                    handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
                } else {
                    reset();
                    bus.post(Completed.INSTANCE);
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }


    public static class Ticked extends BaseEvent {
        private static final Ticked INSTANCE = new Ticked();
        private int value = 0;

        public static Ticked acquire(int value) {
            INSTANCE.value = value;
            return INSTANCE;
        }

        public int getValue() {
            return value;
        }

        private Ticked() {
        }
    }

    public static class Completed extends BaseEvent {
        public static final Completed INSTANCE = new Completed();
    }
}
