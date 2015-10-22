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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CountUp implements Handler.Callback {
    public static final int NUMBER_TICKS = 10;
    public static final long DEFAULT_TICK_DURATION_MS = 1000L;
    public static final double DEFAULT_SCALE_FACTOR = 0.99;

    private static final int MSG_TICK = 0;

    private final List<Listener> listeners = new ArrayList<>();
    private final Handler handler;

    private boolean running = false;
    private int tickCurrent = 0;
    private long tickDuration = DEFAULT_TICK_DURATION_MS;

    public CountUp() {
        this.handler = new Handler(Looper.getMainLooper(), this);
    }

    public void addListener(@NonNull Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(@NonNull Listener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void setTickDuration(long tickDuration) {
        this.tickDuration = tickDuration;

        handler.removeMessages(MSG_TICK);
        if (running) {
            handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
        }
    }

    public void scaleTickDuration(double factor) {
        this.tickDuration = Math.round(tickDuration * factor);

        handler.removeMessages(MSG_TICK);
        if (running) {
            handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
        }
    }

    public long getTickDuration() {
        return tickDuration;
    }

    public void start() {
        this.running = true;

        this.tickCurrent = 1;

        handler.removeMessages(MSG_TICK);
        handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);

        for (final Listener listener : listeners) {
            listener.onStarted();
            listener.onTicked(tickCurrent);
        }
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
                    for (final Listener listener : listeners) {
                        listener.onTicked(tickCurrent);
                    }

                    handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
                } else {
                    reset();

                    for (final Listener listener : listeners) {
                        listener.onCompleted();
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }


    public interface Listener {
        void onStarted();
        void onTicked(int tick);
        void onCompleted();
    }
}
