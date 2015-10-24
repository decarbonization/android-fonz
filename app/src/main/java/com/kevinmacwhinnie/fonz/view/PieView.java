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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.util.PieceDrawing;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class PieView extends View implements PieceDrawable.PieceProvider {

    private PieceDrawable pieceDrawable;
    private Pie pie;


    //region Lifecycle

    public PieView(@NonNull Context context) {
        this(context, null);
    }

    public PieView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setBackgroundResource(R.drawable.background_pie);
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

        if (pieceDrawable != null) {
            pieceDrawable.setBounds(0, 0, w, h);
        }
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

        setWillNotDraw(pieceDrawing == null || pie == null);
    }

    public void setPie(@Nullable Pie pie) {
        if (this.pie != null) {
            final Bus bus = this.pie.bus;
            if (this.pie.bus != null) {
                bus.unregister(this);
            }
        }

        this.pie = pie;

        if (pie != null) {
            final CharSequence contentDescription = generateContentDescription();
            setContentDescription(contentDescription);

            final Bus bus = pie.bus;
            if (bus != null) {
                bus.register(this);
            }
        }

        setWillNotDraw(pieceDrawable == null || pie == null);
    }

    //endregion


    //region Accessibility

    @VisibleForTesting
    CharSequence generateContentDescription() {
        final Resources resources = getResources();
        final int occupiedSlotCount = pie.getOccupiedSlotCount();
        if (occupiedSlotCount == 0) {
            return resources.getString(R.string.accessibility_pie_empty);
        } else {
            if (pie.isFull()) {
                if (pie.isSingleColor()) {
                    return resources.getString(R.string.accessibility_pie_full_single_color);
                } else {
                    return resources.getString(R.string.accessibility_pie_full_multiple_colors);
                }
            } else {
                final List<CharSequence> slotDescriptions = new ArrayList<>(Pie.NUMBER_SLOTS);
                for (@Pie.Slot int slot = 0; slot < Pie.NUMBER_SLOTS; slot++) {
                    final Piece piece = pie.getPiece(slot);
                    if (piece == Piece.EMPTY) {
                        continue;
                    }

                    final String pieceName = resources.getString(piece.accessibilityName);
                    slotDescriptions.add(resources.getString(Pie.SLOT_ACCESSIBILITY_NAME_FORMATS[slot], pieceName));
                }
                final String deliminator = resources.getString(R.string.accessibility_pie_slot_conjunction);
                final CharSequence slotSummary = TextUtils.join(deliminator, slotDescriptions);

                return resources.getQuantityString(R.plurals.accessibility_pie_partially_filled,
                                                   occupiedSlotCount, occupiedSlotCount, slotSummary);
            }
        }
    }

    //endregion


    //region Events

    @Subscribe public void onPieChanged(@NonNull Pie.Changed change) {
        if (change.from == this.pie) {
            final CharSequence contentDescription = generateContentDescription();
            setContentDescription(contentDescription);
            announceForAccessibility(contentDescription);

            invalidate();
        }
    }

    @NonNull
    @Override
    public Piece getPiece(@Pie.Slot int slot) {
        return pie.getPiece(slot);
    }

    //endregion
}
