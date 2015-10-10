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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.game.CountUp;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.util.PieceDrawing;

public class BoardView extends LinearLayout
        implements View.OnClickListener, CountUp.Listener {
    private final PieView[] pieViews;
    private final PieView upcomingPieView;
    private final TextView debugCountUpText;

    private Pie upcomingPie;
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

        // TODO: view specifically for upcoming pieces
        this.upcomingPieView = (PieView) findViewById(R.id.view_game_board_pie_upcoming);
        upcomingPieView.setOnClickListener(this);
        upcomingPieView.setPieceDrawing(pieceDrawing);

        this.debugCountUpText = (TextView) findViewById(R.id.game_board_debug_counter);
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

        this.upcomingPie = new Pie(board.bus);
        upcomingPieView.setPie(upcomingPie);
    }

    public void setUpcomingPiece(UpcomingPiece upcomingPiece) {
        upcomingPie.reset();
        if (upcomingPiece != null) {
            upcomingPie.tryPlacePiece(upcomingPiece.slot, upcomingPiece.piece);
        }
    }

    public void setOnPieClickListener(@Nullable OnPieClickListener onPieClickListener) {
        this.onPieClickListener = onPieClickListener;
    }

    //endregion


    //region Events

    @Override
    public void onClick(View pieView) {
        if (onPieClickListener != null && board != null) {
            if (pieView == upcomingPieView) {
                onPieClickListener.onUpcomingPieClicked();
            } else {
                final int slot = (int) pieView.getTag(R.id.view_board_pie_slot_tag);
                onPieClickListener.onPieClicked(board.getPie(slot));
            }
        }
    }

    @Override
    public void onStarted() {
        debugCountUpText.setText(Long.toString(1L, 10));
    }

    @Override
    public void onTicked(long tick) {
        Log.i(getClass().getSimpleName(), "BoardView#onTicked(" + tick + ")");
        debugCountUpText.setText(Long.toString(tick + 1L, 10));
    }

    @Override
    public void onCompleted() {
        debugCountUpText.setText(R.string.data_placeholder);
    }

    //endregion


    public interface OnPieClickListener {
        void onUpcomingPieClicked();
        void onPieClicked(@NonNull Pie pie);
    }
}
