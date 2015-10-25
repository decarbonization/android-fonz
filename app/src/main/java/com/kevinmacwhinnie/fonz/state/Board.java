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

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

import java.util.EnumSet;

public class Board {
    public static final int NUMBER_PIES = 6;

    public final Bus bus;

    private final Pie[] pies;
    private final EnumSet<PowerUp> powerUps = EnumSet.noneOf(PowerUp.class);

    public Board(@NonNull Bus bus) {
        this.bus = bus;
        this.pies = new Pie[NUMBER_PIES];
        for (int slot = 0; slot < NUMBER_PIES; slot++) {
            this.pies[slot] = new Pie(bus);
        }
    }

    public int getSlot(@NonNull Pie pie) {
        for (int i = 0, length = pies.length; i < length; i++) {
            if (pies[i].equals(pie)) {
                return i;
            }
        }
        return -1;
    }

    public Pie getPie(int slot) {
        return pies[slot];
    }

    public boolean pieHasPowerUp(int slot) {
        return (slot < PowerUp.values().length);
    }

    public boolean addPowerUp(@NonNull PowerUp powerUp) {
        if (powerUps.add(powerUp)) {
            bus.post(new PowerUpChanged(powerUp));

            return true;
        } else {
            return false;
        }
    }

    public boolean usePowerUp(@NonNull PowerUp powerUp) {
        if (powerUps.remove(powerUp)) {
            bus.post(new PowerUpChanged(powerUp));

            return true;
        } else {
            return false;
        }
    }

    public boolean hasPowerUp(@NonNull PowerUp powerUp) {
        return powerUps.contains(powerUp);
    }

    public void reset() {
        for (int i = 0; i < NUMBER_PIES; i++) {
            pies[i].reset();
        }
        powerUps.clear();
    }


    public static class PowerUpChanged extends ValueBaseEvent<PowerUp> {
        public PowerUpChanged(@NonNull PowerUp value) {
            super(value);
        }
    }
}
