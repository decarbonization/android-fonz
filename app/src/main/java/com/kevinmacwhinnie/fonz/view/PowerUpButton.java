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

package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.game.PowerUpTimer;

public class PowerUpButton extends AppCompatImageButton {
    private final TimerDrawable timerDrawable;
    private PowerUp powerUp;

    //region Lifecycle

    public PowerUpButton(@NonNull Context context) {
        this(context, null);
    }

    public PowerUpButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerUpButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setScaleType(ScaleType.CENTER);
        setAdjustViewBounds(true);

        this.timerDrawable = new TimerDrawable(getResources());
        timerDrawable.setNumberOfTicks(PowerUpTimer.STANDARD_NUMBER_TICKS);
        setBackground(timerDrawable);

        if (attrs != null) {
            final TypedArray styles = context.obtainStyledAttributes(attrs, R.styleable.PowerUpButton,
                                                                     defStyle, 0);

            final int powerUpBoardSlot = styles.getInt(R.styleable.PowerUpButton_fonzPowerUp, 0);
            setPowerUp(PowerUp.forSlot(powerUpBoardSlot));

            styles.recycle();
        } else {
            setPowerUp(PowerUp.CLEAR_ALL);
        }
    }

    //endregion


    //region Attributes

    public void setPowerUp(@NonNull PowerUp powerUp) {
        this.powerUp = powerUp;

        final Resources resources = getResources();
        setContentDescription(resources.getString(powerUp.accessibilityRes));
        setImageDrawable(ResourcesCompat.getDrawable(resources, powerUp.iconRes, null));
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void setTick(int tick) {
        timerDrawable.setTick(tick);
    }

    //endregion
}
