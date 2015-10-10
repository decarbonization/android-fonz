package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

public class Life {
    public static int INITIAL_VALUE = 5;

    private final Bus bus;
    private int value = INITIAL_VALUE;

    public Life(@NonNull Bus bus) {
        this.bus = bus;
    }

    public int getValue() {
        return value;
    }

    public void decrement() {
        if (value > 0) {
            this.value--;
            bus.post(new Changed(value));
        }
    }

    public void increment() {
        this.value++;
        bus.post(new Changed(value));
    }

    public boolean isAlive() {
        return (value > 0);
    }

    public void reset() {
        this.value = INITIAL_VALUE;
        bus.post(new Changed(value));
    }

    @Override
    public String toString() {
        return "Life{" +
                "value=" + value +
                '}';
    }


    public static class Changed extends ValueBaseEvent<Integer> {
        public Changed(Integer value) {
            super(value);
        }
    }
}
