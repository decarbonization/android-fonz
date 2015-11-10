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

import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;

public class CountUp extends Timer {
    public static final int NUMBER_TICKS = 10;
    public static final long MIN_TICK_DURATION = 450L;
    public static final float DEFAULT_SCALE_FACTOR = 0.95f;

    private final Bus bus;

    public CountUp(@NonNull Bus bus) {
        this.bus = bus;

        setNumberOfTicks(NUMBER_TICKS);
    }

    public void scaleTickDuration(double factor) {
        long tickDuration = Math.round(getTickDurationMs() * factor);
        if (tickDuration < MIN_TICK_DURATION) {
            tickDuration = MIN_TICK_DURATION;
        }

        setTickDurationMs(tickDuration);
    }

    @Override
    public void stop() {
        super.stop();

        setTickDurationMs(DEFAULT_TICK_DURATION_MS);
    }

    @Override
    protected void onTick(int tick) {
        bus.post(Ticked.acquire(tick));
    }

    @Override
    protected void onCompleted() {
        bus.post(Completed.INSTANCE);
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
