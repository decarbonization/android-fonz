package com.kevinmacwhinnie.fonz.game;

import android.support.annotation.NonNull;

public class CountDown {
    private final Listener listener;
    private long duration;

    public CountDown(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void start() {

    }

    public void halt() {

    }


    public interface Listener {
        void onTicked();
        void onCompleted();
    }
}
