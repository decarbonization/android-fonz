package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.kevinmacwhinnie.fonz.state.Pie;

public class PieView extends View {
    private final float DEGREES_PER_SLICE = (360f / Pie.NUMBER_PIECES);
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

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
    }

    //endregion


    //region Drawing

    @Override
    protected void onDraw(Canvas canvas) {
        rect.set(0f, 0f, canvas.getWidth(), canvas.getHeight());

        paint.setColor(Color.LTGRAY);
        canvas.drawArc(rect, 0f, 360f, true, paint);

        final int saveCount = canvas.save();
        canvas.rotate(-90f, rect.centerX(), rect.centerY());

        for (int i = 0; i < Pie.NUMBER_PIECES; i++) {
            final float startAngle = DEGREES_PER_SLICE * i;
            paint.setColor(pie.getPiece(i).color);
            canvas.drawArc(rect, startAngle, DEGREES_PER_SLICE, true, paint);
        }

        canvas.restoreToCount(saveCount);
    }

    //endregion


    //region Attributes

    public void setPie(@NonNull Pie pie) {
        this.pie = pie;
        setWillNotDraw(false);
    }

    //endregion
}
