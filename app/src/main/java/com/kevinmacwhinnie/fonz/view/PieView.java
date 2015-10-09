package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Piece;

import java.util.ArrayList;
import java.util.List;

public class PieView extends View {
    public final float DEGREES_PER_SLICE = (360f / Pie.NUMBER_PIECES);

    private final Resources resources;
    private final int strokeInset;
    private final RectF rect = new RectF();
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pieceFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pieceStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path piecePath = new Path();

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
        this.strokeInset = resources.getDimensionPixelSize(R.dimen.view_pie_stroke_width);

        final @ColorInt int backgroundColor = resources.getColor(R.color.view_pie_background);
        fillPaint.setColor(backgroundColor);
        pieceFillPaint.setStrokeJoin(Paint.Join.ROUND);
        pieceStrokePaint.setStyle(Paint.Style.STROKE);
        pieceStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        pieceStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        pieceStrokePaint.setStrokeWidth(strokeInset);
        pieceStrokePaint.setColor(backgroundColor);
    }

    //endregion


    //region Drawing

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: move to pre-rendered pieces

        rect.set(0f, 0f, canvas.getWidth(), canvas.getHeight());

        canvas.drawOval(rect, fillPaint);

        rect.inset(strokeInset, strokeInset);
        canvas.save();
        canvas.rotate(-180f, rect.centerX(), rect.centerY());
        for (int i = 0; i < Pie.NUMBER_PIECES; i++) {
            final Piece piece = pie.getPiece(i);
            if (piece == Piece.EMPTY) {
                continue;
            }

            piecePath.reset();
            piecePath.moveTo(rect.centerX(), rect.centerY());

            final float startAngle = DEGREES_PER_SLICE * i;
            final @ColorInt int pieceColor = resources.getColor(piece.color);
            pieceFillPaint.setColor(pieceColor);

            piecePath.arcTo(rect, startAngle, DEGREES_PER_SLICE);
            canvas.drawPath(piecePath, pieceFillPaint);
            canvas.drawPath(piecePath, pieceStrokePaint);
        }
    }

    //endregion


    //region Attributes

    public void setPie(@NonNull Pie pie) {
        this.pie = pie;

        final CharSequence contentDescription = generateContentDescription();
        setContentDescription(contentDescription);
        setWillNotDraw(false);
    }

    public void notifyChanged() {
        final CharSequence contentDescription = generateContentDescription();
        setContentDescription(contentDescription);
        announceForAccessibility(contentDescription);

        invalidate();
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
                final List<CharSequence> slotDescriptions = new ArrayList<>(Pie.NUMBER_PIECES);
                for (int i = 0; i < Pie.NUMBER_PIECES; i++) {
                    final Piece piece = pie.getPiece(i);
                    if (piece == Piece.EMPTY) {
                        continue;
                    }

                    final String pieceName = resources.getString(piece.accessibilityName);
                    slotDescriptions.add(resources.getString(Pie.SLOT_ACCESSIBILITY_NAME_FMTS[i], pieceName));
                }
                final String deliminator = resources.getString(R.string.accessibility_pie_slot_conjunction);
                final CharSequence slotSummary = TextUtils.join(deliminator, slotDescriptions);

                return resources.getQuantityString(R.plurals.accessibility_pie_partially_filled,
                                                   occupiedSlotCount, occupiedSlotCount, slotSummary);
            }
        }
    }

    //endregion
}
