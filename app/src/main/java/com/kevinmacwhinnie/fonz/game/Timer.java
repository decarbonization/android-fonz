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
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.data.GamePersistence;

public abstract class Timer implements GamePersistence, Handler.Callback {
    static final String SAVED_RUNNING = ".SAVED_RUNNING";
    static final String SAVED_NUMBER_TICKS = ".SAVED_NUMBER_TICKS";
    static final String SAVED_CURRENT_TICK = ".SAVED_CURRENT_TICK";
    static final String SAVED_TICK_DURATION = ".SAVED_TICK_DURATION";

    static final int MSG_TICK = 0;

    public static final long DEFAULT_TICK_DURATION_MS = 1000L;

    private final Handler handler = new Handler(Looper.getMainLooper(), this);

    @VisibleForTesting boolean running = false;
    private int numberOfTicks = 10;
    @VisibleForTesting int currentTick = 0;
    private long tickDurationMs = DEFAULT_TICK_DURATION_MS;
    private boolean paused = false;

    @Override
    @CallSuper
    public void restoreState(@NonNull Bundle inState) {
        final String prefix = getClass().getName();
        this.running = inState.getBoolean(prefix + SAVED_RUNNING);
        this.paused = running;
        this.numberOfTicks = inState.getInt(prefix + SAVED_NUMBER_TICKS);
        this.currentTick = inState.getInt(prefix + SAVED_CURRENT_TICK);
        this.tickDurationMs = inState.getLong(prefix + SAVED_TICK_DURATION);
    }

    @Override
    @CallSuper
    public void saveState(@NonNull Bundle outState) {
        final String prefix = getClass().getName();
        outState.putBoolean(prefix + SAVED_RUNNING, running);
        outState.putInt(prefix + SAVED_NUMBER_TICKS, numberOfTicks);
        outState.putInt(prefix + SAVED_CURRENT_TICK, currentTick);
        outState.putLong(prefix + SAVED_TICK_DURATION, tickDurationMs);
    }

    //region Attributes


    public final void setNumberOfTicks(int numberOfTicks) {
        this.numberOfTicks = numberOfTicks;
    }

    public final int getNumberOfTicks() {
        return numberOfTicks;
    }

    public final void setTickDurationMs(long tickDurationMs) {
        this.tickDurationMs = tickDurationMs;

        handler.removeMessages(MSG_TICK);
        if (running && !paused) {
            handler.sendEmptyMessageDelayed(MSG_TICK, tickDurationMs);
        }
    }

    public final long getTickDurationMs() {
        return tickDurationMs;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public final boolean isRunning() {
        return running;
    }

    //endregion


    //region Controlling Timer

    public final void start() {
        this.running = true;

        this.currentTick = 1;

        handler.removeMessages(MSG_TICK);
        handler.sendEmptyMessageDelayed(MSG_TICK, tickDurationMs);

        onTick(currentTick);
    }

    public final void resume() {
        if (paused) {
            handler.sendEmptyMessageDelayed(MSG_TICK, tickDurationMs);
            this.paused = false;
        }
    }

    public final void pause() {
        if (!paused) {
            handler.removeMessages(MSG_TICK);
            this.paused = true;
        }
    }

    public final void stop() {
        handler.removeMessages(MSG_TICK);

        this.running = false;
        this.paused = false;
        this.tickDurationMs = DEFAULT_TICK_DURATION_MS;
        this.currentTick = 1;
    }

    //endregion


    //region Callbacks

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TICK: {
                if (++this.currentTick <= numberOfTicks) {
                    onTick(currentTick);
                    handler.sendEmptyMessageDelayed(MSG_TICK, tickDurationMs);
                } else {
                    stop();
                    onCompleted();
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }

    //endregion


    //region Hooks

    protected abstract void onTick(int tick);
    protected abstract void onCompleted();

    //endregion
}
