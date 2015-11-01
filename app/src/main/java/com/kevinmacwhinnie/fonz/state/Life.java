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
package com.kevinmacwhinnie.fonz.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.data.GamePersistence;
import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

public class Life implements GamePersistence {
    static final String SAVED_VALUE = Life.class.getName() + ".SAVED_VALUE";

    public static int INITIAL_VALUE = 5;

    private final Bus bus;
    private int value = INITIAL_VALUE;

    public Life(@NonNull Bus bus) {
        this.bus = bus;
    }

    @Override
    public void restoreState(@NonNull Bundle inState) {
        this.value = inState.getInt(SAVED_VALUE);
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putInt(SAVED_VALUE, value);
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
                "how=" + value +
                '}';
    }


    public static class Changed extends ValueBaseEvent<Integer> {
        public Changed(Integer value) {
            super(value);
        }
    }
}
