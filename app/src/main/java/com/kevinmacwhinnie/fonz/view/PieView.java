package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.util.Drawing;
import com.kevinmacwhinnie.fonz.view.util.PieceDrawing;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class PieView extends View {
    private final Resources resources;
    private final RectF rect = new RectF();
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pieceFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PieceDrawing pieceDrawing;

    private boolean upcoming = false;
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

        setWillNotDraw(true);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        setAccessibilityLiveRegion(ACCESSIBILITY_LIVE_REGION_ASSERTIVE);

        this.resources = getResources();

        final @ColorInt int backgroundColor = Drawing.getColor(resources, R.color.view_pie_background);
        fillPaint.setColor(backgroundColor);
        pieceFillPaint.setStrokeJoin(Paint.Join.ROUND);

        this.pieceDrawing = new PieceDrawing(resources);
    }

    //endregion


    //region Drawing

    @Override
    protected void onDraw(Canvas canvas) {
        rect.set(0f, 0f, canvas.getWidth(), canvas.getHeight());

        canvas.drawOval(rect, fillPaint);

        for (int slot = 0; slot < Pie.NUMBER_SLOTS; slot++) {
            final Piece piece = pie.getPiece(slot);
            if (piece == Piece.EMPTY) {
                continue;
            }

            pieceDrawing.draw(slot, piece, canvas, pieceFillPaint);
        }
    }

    //endregion


    //region Attributes

    public void setUpcoming(boolean upcoming) {
        this.upcoming = upcoming;
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
            setWillNotDraw(false);

            final Bus bus = pie.bus;
            if (bus != null) {
                bus.register(this);
            }
        } else {
            setWillNotDraw(true);
        }
    }

    //endregion


    //region Accessibility

    @VisibleForTesting
    CharSequence generateContentDescription() {
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
                for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
                    final Piece piece = pie.getPiece(i);
                    if (piece == Piece.EMPTY) {
                        continue;
                    }

                    final String pieceName = resources.getString(piece.accessibilityName);
                    slotDescriptions.add(resources.getString(Pie.SLOT_ACCESSIBILITY_NAME_FORMATS[i], pieceName));
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
            // TODO: we don't want to announce the same thing for the upcoming pie
            announceForAccessibility(contentDescription);

            invalidate();
        }
    }

    //endregion
}
