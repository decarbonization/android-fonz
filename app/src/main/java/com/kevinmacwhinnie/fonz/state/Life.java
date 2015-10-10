package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

public class Life {
    public static int INITIAL_COUNT = 3;

    private final Bus bus;
    private int count = INITIAL_COUNT;

    public Life(@NonNull Bus bus) {
        this.bus = bus;
    }

    public int getCount() {
        return count;
    }

    public void decrement() {
        if (count > 0) {
            this.count--;
            bus.post(new Changed(count));
        }
    }

    public void increment() {
        this.count++;
        bus.post(new Changed(count));
    }

    public boolean isAlive() {
        return (count > 0);
    }

    public void reset() {
        this.count = INITIAL_COUNT;
        bus.post(new Changed(count));
    }

    @Override
    public String toString() {
        return "Life{" +
                "count=" + count +
                '}';
    }


    public static class Changed extends ValueBaseEvent<Integer> {
        public Changed(Integer value) {
            super(value);
        }
    }
}
