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

import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.events.BaseValueEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Iterator;

public class PowerUpTimer extends Timer {
    public static final int STANDARD_NUMBER_TICKS = 60;
    public static final long TICK_DURATION_MS = 500L;

    static final String SAVED_SCHEDULED = PowerUpTimer.class.getName() + ".SAVED_SCHEDULED";

    private final Bus bus;
    private final ArrayList<PowerUpTicked> scheduled = new ArrayList<>(3);

    //region Lifecycle

    public PowerUpTimer(@NonNull Bus bus) {
        this.bus = bus;
        setTickDurationMs(TICK_DURATION_MS);
    }

    @Override
    public void restoreState(@NonNull Bundle inState) {
        super.restoreState(inState);

        final ArrayList<PowerUpTicked> inScheduled =
                inState.getParcelableArrayList(SAVED_SCHEDULED);
        if (inScheduled != null) {
            scheduled.clear();
            scheduled.addAll(inScheduled);
        }
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        super.saveState(outState);

        outState.putParcelableArrayList(SAVED_SCHEDULED, scheduled);
    }

    //endregion


    //region Scheduling

    public void schedulePowerUp(@NonNull PowerUp powerUp, int numberOfTicks) {
        scheduled.add(new PowerUpTicked(powerUp, numberOfTicks, 1));
        bus.post(new PowerUpScheduled(powerUp));

        setNumberOfTicks(numberOfTicks);
        if (!isRunning()) {
            start();
        }
    }

    @Override
    public void stop() {
        super.stop();
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


    //region Timing

    @Override
    protected void onTick(int tick) {
        final Iterator<PowerUpTicked> iterator = scheduled.iterator();
        while (iterator.hasNext()) {
            final PowerUpTicked powerUpTicked = iterator.next();
            if (++powerUpTicked.currentTick >= powerUpTicked.numberOfTicks) {
                iterator.remove();
                bus.post(new PowerUpExpired(powerUpTicked.powerUp));
            } else {
                bus.post(powerUpTicked);
            }
        }
    }

    @Override
    protected void onCompleted() {
        // Do nothing.
    }

    //endregion


    //region Events

    public static class PowerUpTicked extends BaseEvent implements Parcelable {
        public final PowerUp powerUp;
        public final int numberOfTicks;
        public int currentTick;

        PowerUpTicked(@NonNull PowerUp powerUp,
                      int numberOfTicks,
                      int currentTick) {
            this.powerUp = powerUp;
            this.numberOfTicks = numberOfTicks;
            this.currentTick = currentTick;
        }

        PowerUpTicked(Parcel in) {
            this(PowerUp.valueOf(in.readString()),
                 in.readInt(),
                 in.readInt());
        }

        public int getTicksRemaining() {
            return (numberOfTicks - currentTick);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(powerUp.toString());
            out.writeInt(numberOfTicks);
            out.writeInt(currentTick);
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
