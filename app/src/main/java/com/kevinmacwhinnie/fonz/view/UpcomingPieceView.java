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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.util.PieceDrawing;

public class UpcomingPieceView extends View implements PieceDrawable.PieceProvider {
    private final TimerDrawable timerDrawable;
    private final int pieceInset;

    private PieceDrawable pieceDrawable;
    private @Nullable UpcomingPiece upcomingPiece;


    //region Lifecycle

    public UpcomingPieceView(@NonNull Context context) {
        this(context, null);
    }

    public UpcomingPieceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpcomingPieceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final Resources resources = getResources();

        this.timerDrawable = new TimerDrawable(resources);
        this.pieceInset = resources.getDimensionPixelSize(R.dimen.timer_stroke) / 2;

        final Drawable pie = ResourcesCompat.getDrawable(resources, R.drawable.background_pie, null);
        final Drawable[] layers = { timerDrawable, pie };
        final LayerDrawable background = new LayerDrawable(layers);
        background.setLayerInset(1, pieceInset, pieceInset, pieceInset, pieceInset);

        setBackground(background);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        setAccessibilityLiveRegion(ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
        setWillNotDraw(true);
        setSoundEffectsEnabled(false);
    }

    //endregion


    //region Drawing

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        pieceDrawable.setBounds(pieceInset, pieceInset,
                                w - pieceInset, h - pieceInset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        pieceDrawable.draw(canvas);
    }

    //endregion


    //region Attributes

    public void setPieceDrawing(@Nullable PieceDrawing pieceDrawing) {
        if (pieceDrawing != null) {
            this.pieceDrawable = new PieceDrawable(this, pieceDrawing);
            pieceDrawable.setBounds(0, 0, getWidth(), getHeight());
        } else {
            this.pieceDrawable = null;
        }

        setWillNotDraw(pieceDrawable == null || upcomingPiece == null);
    }

    public void setUpcomingPiece(@Nullable UpcomingPiece upcomingPiece) {
        this.upcomingPiece = upcomingPiece;
        invalidate();
        setWillNotDraw(pieceDrawable == null || upcomingPiece == null);
    }

    public void setTick(int tick) {
        timerDrawable.setTick(tick);
    }

    @NonNull
    @Override
    public Piece getPiece(@Pie.Slot int slot) {
        if (upcomingPiece != null && slot == upcomingPiece.slot) {
            return upcomingPiece.piece;
        } else {
            return Piece.EMPTY;
        }
    }

    //endregion
}
