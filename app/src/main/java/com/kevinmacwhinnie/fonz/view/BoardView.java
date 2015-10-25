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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.util.PieceDrawing;

import is.hello.go99.animators.OnAnimationCompleted;

public class BoardView extends LinearLayout
        implements View.OnClickListener {
    private final PieView[] pieViews;
    private final UpcomingPieceView upcomingPieceView;

    private Board board;
    private OnPieClickListener onPieClickListener;

    //region Lifecycle

    public BoardView(@NonNull Context context) {
        this(context, null);
    }

    public BoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(HORIZONTAL);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_game_board, this, true);

        final PieceDrawing pieceDrawing = new PieceDrawing(getResources());

        this.pieViews = new PieView[] {
                (PieView) findViewById(R.id.view_game_board_pie_left_top),
                (PieView) findViewById(R.id.view_game_board_pie_center_top),
                (PieView) findViewById(R.id.view_game_board_pie_right_top),
                (PieView) findViewById(R.id.view_game_board_pie_left_bottom),
                (PieView) findViewById(R.id.view_game_board_pie_center_bottom),
                (PieView) findViewById(R.id.view_game_board_pie_right_bottom),
        };
        for (int i = 0, pieViewsLength = pieViews.length; i < pieViewsLength; i++) {
            final PieView pieView = pieViews[i];
            pieView.setTag(R.id.view_board_pie_slot_tag, i);
            pieView.setPieceDrawing(pieceDrawing);
            pieView.setOnClickListener(this);
        }

        this.upcomingPieceView = (UpcomingPieceView) findViewById(R.id.view_game_board_upcoming_piece);
        upcomingPieceView.setOnClickListener(this);
        upcomingPieceView.setPieceDrawing(pieceDrawing);

    }

    public void destroy() {
        for (final PieView pieView : pieViews) {
            pieView.setPie(null);
        }
    }

    //endregion


    //region Attributes

    public void setBoard(@NonNull Board board) {
        this.board = board;

        for (int i = 0; i < Board.NUMBER_PIES; i++) {
            pieViews[i].setPie(board.getPie(i));
        }
    }

    public void setUpcomingPiece(@Nullable UpcomingPiece upcomingPiece) {
        upcomingPieceView.setUpcomingPiece(upcomingPiece);
    }

    public void setOnPieClickListener(@Nullable OnPieClickListener onPieClickListener) {
        this.onPieClickListener = onPieClickListener;
    }

    public void setTick(int tick) {
        upcomingPieceView.setTick(tick);
    }

    //endregion


    //region Animations

    public void animateCurrentPieceIntoPie(final int pieSlot,
                                           @NonNull final OnAnimationCompleted onAnimationCompleted) {
        onAnimationCompleted.onAnimationCompleted(true);
    }

    //endregion


    //region Events

    @Override
    public void onClick(View pieView) {
        if (onPieClickListener != null && board != null) {
            if (pieView == upcomingPieceView) {
                onPieClickListener.onUpcomingPieClicked();
            } else {
                final int slot = (int) pieView.getTag(R.id.view_board_pie_slot_tag);
                onPieClickListener.onPieClicked(slot, board.getPie(slot));
            }
        }
    }

    //endregion


    public interface OnPieClickListener {
        void onUpcomingPieClicked();
        void onPieClicked(int pieSlot, @NonNull Pie pie);
    }
}
