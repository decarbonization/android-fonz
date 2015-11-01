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

import com.kevinmacwhinnie.fonz.data.GamePersistence;
import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

import java.util.EnumSet;

public class TimedMechanics implements Handler.Callback, GamePersistence {
    static final String SAVED_PENDING = TimedMechanics.class.getName() + ".SAVED_PENDING";

    public static final long DURATION = 15 * 1000L;

    private static final int MSG_POWER_UP_EXPIRE = 0;

    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final EnumSet<PowerUp> pending = EnumSet.noneOf(PowerUp.class);
    private final Bus bus;

    public TimedMechanics(@NonNull Bus bus) {
        this.bus = bus;
    }

    @Override
    public void restoreState(@NonNull Bundle inState) {
        @SuppressWarnings("unchecked")
        final EnumSet<PowerUp> savedPending =
                (EnumSet<PowerUp>) inState.getSerializable(SAVED_PENDING);
        assert savedPending != null;
        pending.clear();
        pending.addAll(savedPending);
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putSerializable(SAVED_PENDING, pending);
    }

    //region Scheduling

    public void schedulePowerUp(@NonNull PowerUp powerUp) {
        final Message expiration = handler.obtainMessage(MSG_POWER_UP_EXPIRE, powerUp);
        handler.sendMessageDelayed(expiration, DURATION);
        pending.add(powerUp);
        bus.post(new PowerUpScheduled(powerUp));
    }

    public void pause() {
        handler.removeMessages(MSG_POWER_UP_EXPIRE);
    }

    public void resume() {
        for (final PowerUp powerUp : pending) {
            schedulePowerUp(powerUp);
        }
    }

    public void reset() {
        handler.removeMessages(MSG_POWER_UP_EXPIRE);
        pending.clear();
    }

    public boolean isPending(@NonNull PowerUp powerUp) {
        return pending.contains(powerUp);
    }

    //endregion


    //region Events

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_POWER_UP_EXPIRE) {
            final PowerUp powerUp = (PowerUp) msg.obj;
            pending.remove(powerUp);
            bus.post(new PowerUpExpired(powerUp));
        }

        return false;
    }

    public static class PowerUpScheduled extends ValueBaseEvent<PowerUp> {
        public PowerUpScheduled(@NonNull PowerUp value) {
            super(value);
        }
    }

    public static class PowerUpExpired extends ValueBaseEvent<PowerUp> {
        public PowerUpExpired(@NonNull PowerUp value) {
            super(value);
        }
    }

    //endregion
}
