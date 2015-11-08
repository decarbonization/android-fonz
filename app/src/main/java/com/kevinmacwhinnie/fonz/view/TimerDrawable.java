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

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.game.CountUp;
import com.kevinmacwhinnie.fonz.view.util.Drawing;

public class TimerDrawable extends Drawable {
    private final RectF drawingRect = new RectF();
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float inset;

    private float degreesPerTick = 360f / CountUp.NUMBER_TICKS;
    private int tick = 0;

    public TimerDrawable(@NonNull Resources resources) {
        final int strokeSize = resources.getDimensionPixelSize(R.dimen.timer_stroke);
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setColor(Drawing.getColor(resources, R.color.timer_color));
        fillPaint.setStrokeWidth(strokeSize);

        this.inset = strokeSize / 2f;
    }

    //region Drawing

    @Override
    public void draw(Canvas canvas) {
        canvas.drawArc(drawingRect, 180f,
                       tick * degreesPerTick,
                       false, fillPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        drawingRect.set(bounds);
        drawingRect.inset(inset, inset);
        if (drawingRect.width() > drawingRect.height()) {
            final int diffX = (int) (drawingRect.width() - drawingRect.height()) / 2;
            drawingRect.left += diffX;
            drawingRect.right -= diffX;
        } else if (drawingRect.width() < drawingRect.height()) {
            final int diffY = (int) (drawingRect.height() - drawingRect.width()) / 2;
            drawingRect.top += diffY;
            drawingRect.bottom -= diffY;
        }
    }

    //endregion


    //region Attributes

    @Override
    public void setAlpha(int alpha) {
        fillPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        fillPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setTick(int tick) {
        this.tick = tick;
        invalidateSelf();
    }

    public void setNumberOfTicks(int ticks) {
        this.degreesPerTick = 360f / ticks;
        invalidateSelf();
    }

    //endregion
}
