package com.kevinmacwhinnie.fonz.game;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

public class CountDown implements Handler.Callback {
    public static final long DEFAULT_TICK_COUNT = 60;
    public static final long DEFAULT_TICK_DURATION_MS = 1000L;

    private static final int MSG_TICK = 0;

    private final Listener listener;
    private final Handler handler;

    private boolean running = false;
    private long tickCount = DEFAULT_TICK_COUNT;
    private long tickDuration = DEFAULT_TICK_DURATION_MS;
    private long tickCurrent = 0L;

    public CountDown(@NonNull Listener listener) {
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper(), this);
    }

    public void setTickDuration(long tickDuration) {
        this.tickDuration = tickDuration;

        handler.removeMessages(MSG_TICK);
        if (running) {
            handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
        }
    }

    public void setTickCount(long tickCount) {
        if (running) {
            throw new IllegalStateException("Cannot adjust tick count on running timer.");
        }

        this.tickCount = tickCount;
    }

    public void start() {
        this.running = true;
        handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
    }

    public void pause() {
        handler.removeMessages(MSG_TICK);
    }

    public boolean isRunning() {
        return running;
    }

    public void reset() {
        this.running = false;
        this.tickCount = DEFAULT_TICK_COUNT;
        this.tickDuration = DEFAULT_TICK_DURATION_MS;
        this.tickCurrent = 0L;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TICK: {
                listener.onTicked();
                if (++this.tickCurrent < tickCount) {
                    handler.sendEmptyMessageDelayed(MSG_TICK, tickDuration);
                } else {
                    reset();
                    listener.onCompleted();
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }


    public interface Listener {
        void onTicked();
        void onCompleted();
    }
}
