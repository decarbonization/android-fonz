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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.data.GamePersistence;
import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.events.BaseValueEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;

public class TimedMechanics implements GamePersistence {
    public static final int STANDARD_DURATION_TICKS = 30;

    static final String SAVED_SCHEDULED = TimedMechanics.class.getName() + ".SAVED_SCHEDULED";

    private final Bus bus;
    private final ArrayList<PowerUpTicked> scheduled = new ArrayList<>(3);
    private boolean subscribed = false;

    //region Lifecycle

    public TimedMechanics(@NonNull Bus bus) {
        this.bus = bus;
    }

    @Override
    public void restoreState(@NonNull Bundle inState) {
        final ArrayList<PowerUpTicked> inScheduled =
                inState.getParcelableArrayList(SAVED_SCHEDULED);
        if (inScheduled != null) {
            scheduled.clear();
            scheduled.addAll(inScheduled);
        }
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(SAVED_SCHEDULED, scheduled);
    }

    //endregion


    //region Scheduling

    private void subscribe() {
        if (!subscribed) {
            bus.register(this);
            this.subscribed = true;
        }
    }

    private void unsubscribe() {
        if (subscribed) {
            bus.unregister(this);
            this.subscribed = false;
        }
    }

    public void schedulePowerUp(@NonNull PowerUp powerUp, long durationTicks) {
        scheduled.add(new PowerUpTicked(powerUp, durationTicks, 1L));
        bus.post(new PowerUpScheduled(powerUp));
        subscribe();
    }

    public void reset() {
        unsubscribe();
        scheduled.clear();
    }

    public boolean isPending(@NonNull PowerUp powerUp) {
        for (final PowerUpTicked powerUpTicked : scheduled) {
            if (powerUpTicked.powerUp == powerUp) {
                return true;
            }
        }
        return false;
    }

    //endregion


    //region Callbacks

    @Subscribe public void onCountUpTicked(@NonNull CountUp.Ticked tick) {
        final Iterator<PowerUpTicked> iterator = scheduled.iterator();
        while (iterator.hasNext()) {
            final PowerUpTicked powerUpTicked = iterator.next();
            if (++powerUpTicked.durationTickCurrent >= powerUpTicked.durationTicks) {
                iterator.remove();
                bus.post(new PowerUpExpired(powerUpTicked.powerUp));
            } else {
                bus.post(powerUpTicked);
            }
        }

        if (scheduled.isEmpty()) {
            unsubscribe();
        }
    }

    //endregion


    //region Events

    public static class PowerUpTicked extends BaseEvent implements Parcelable {
        public final PowerUp powerUp;
        public final long durationTicks;
        public long durationTickCurrent;

        PowerUpTicked(@NonNull PowerUp powerUp,
                      long durationTicks,
                      long durationTickCurrent) {
            this.powerUp = powerUp;
            this.durationTicks = durationTicks;
            this.durationTickCurrent = durationTickCurrent;
        }

        PowerUpTicked(Parcel in) {
            this(PowerUp.valueOf(in.readString()),
                 in.readLong(),
                 in.readLong());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(powerUp.toString());
            out.writeLong(durationTicks);
            out.writeLong(durationTickCurrent);
        }

        public static final Creator<PowerUpTicked> CREATOR = new Creator<PowerUpTicked>() {
            @Override
            public PowerUpTicked createFromParcel(Parcel in) {
                return new PowerUpTicked(in);
            }

            @Override
            public PowerUpTicked[] newArray(int size) {
                return new PowerUpTicked[size];
            }
        };
    }

    public static class PowerUpScheduled extends BaseValueEvent<PowerUp> {
        public PowerUpScheduled(@NonNull PowerUp value) {
            super(value);
        }
    }

    public static class PowerUpExpired extends BaseValueEvent<PowerUp> {
        public PowerUpExpired(@NonNull PowerUp value) {
            super(value);
        }
    }

    //endregion
}
