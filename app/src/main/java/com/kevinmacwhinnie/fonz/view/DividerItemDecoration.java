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
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.view.util.Drawing;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint dividerPaint = new Paint();
    private final int dividerHeight;
    private final int leftInset;
    private final int rightInset;

    public DividerItemDecoration(@NonNull Resources resources,
                                 int leftInset,
                                 int rightInset) {
        this.dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_size);
        dividerPaint.setColor(Drawing.getColor(resources, R.color.divider));

        this.leftInset = leftInset;
        this.rightInset = rightInset;
    }

    public DividerItemDecoration(@NonNull Resources resources) {
        this(resources, 0, 0);
    }


    @Override
    public void getItemOffsets(Rect outRect, View child, RecyclerView parent, RecyclerView.State state) {
        final int lastPosition = parent.getAdapter().getItemCount() - 1;
        final int childPosition = parent.getChildAdapterPosition(child);
        if (childPosition < lastPosition) {
            outRect.bottom += dividerHeight;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int lastPosition = parent.getAdapter().getItemCount() - 1;
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            final View child = parent.getChildAt(i);
            final int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition < lastPosition) {
                c.drawRect(child.getLeft() + leftInset, child.getBottom(),
                           child.getRight() - rightInset, child.getBottom() + dividerHeight,
                           dividerPaint);
            }
        }
    }
}
