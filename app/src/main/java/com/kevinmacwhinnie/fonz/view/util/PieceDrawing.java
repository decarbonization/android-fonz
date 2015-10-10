package com.kevinmacwhinnie.fonz.view.util;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.state.Pie;

public class PieceDrawing {
    public final float DEGREES_PER_SLICE = (360f / Pie.NUMBER_SLOTS);

    private final Path[] piecePaths = new Path[Pie.NUMBER_SLOTS];
    private final @ColorInt int[] colors = new int[Piece.NUMBER_COLORS];

    public PieceDrawing(@NonNull Resources resources) {
        final float width = resources.getDimension(R.dimen.view_pie_width);
        final float height = resources.getDimension(R.dimen.view_pie_height);
        final RectF bounds = new RectF(0, 0, width, height);

        final int strokeInset = resources.getDimensionPixelSize(R.dimen.view_pie_stroke_width);
        bounds.inset(strokeInset, strokeInset);

        final Matrix transform = new Matrix();
        transform.postRotate(-180f, bounds.centerX(), bounds.centerY());

        for (int slot = 0; slot < Pie.NUMBER_SLOTS; slot++) {
            this.piecePaths[slot] = createPiecePath(bounds, transform, slot);
        }

        for (int i = 0, length = Piece.COLORS.length; i < length; i++) {
            final Piece piece = Piece.COLORS[i];
            this.colors[i] = Drawing.getColor(resources, piece.color);
        }
    }

    private Path createPiecePath(@NonNull RectF bounds, @NonNull Matrix transform, int slot) {
        final Path path = new Path();
        path.moveTo(bounds.centerX(), bounds.centerY());
        path.arcTo(bounds, DEGREES_PER_SLICE * slot, DEGREES_PER_SLICE);
        path.transform(transform);
        path.close();
        return path;
    }

    public void draw(int slot,
                     @NonNull Piece piece,
                     @NonNull Canvas canvas,
                     @NonNull Paint paint) {
        final @ColorInt int color = colors[piece.ordinal()];
        paint.setColor(color);
        canvas.drawPath(piecePaths[slot], paint);
    }
}
