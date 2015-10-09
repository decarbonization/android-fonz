package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Pie;

public class BoardView extends LinearLayout implements View.OnClickListener {
    private final PieView[] pieViews;
    private final PieView upcomingPieView;

    private final Pie upcomingPie = new Pie();
    private Board board;
    private UpcomingPiece upcomingPiece;
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
        inflater.inflate(R.layout.game_board, this, true);

        this.pieViews = new PieView[] {
                (PieView) findViewById(R.id.game_board_pie_left_top),
                (PieView) findViewById(R.id.game_board_pie_center_top),
                (PieView) findViewById(R.id.game_board_pie_right_top),
                (PieView) findViewById(R.id.game_board_pie_left_bottom),
                (PieView) findViewById(R.id.game_board_pie_center_bottom),
                (PieView) findViewById(R.id.game_board_pie_right_bottom),
        };
        for (int i = 0, pieViewsLength = pieViews.length; i < pieViewsLength; i++) {
            final PieView pieView = pieViews[i];
            pieView.setTag(R.id.view_board_pie_slot_tag, i);
            pieView.setOnClickListener(this);
        }

        this.upcomingPieView = (PieView) findViewById(R.id.game_board_pie_upcoming);
        upcomingPieView.setOnClickListener(this);
        upcomingPieView.setPie(upcomingPie);
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

    public void setUpcomingPiece(UpcomingPiece upcomingPiece) {
        this.upcomingPiece = upcomingPiece;

        upcomingPie.reset();
        if (upcomingPiece != null) {
            upcomingPie.tryPlacePiece(upcomingPiece.slot, upcomingPiece.piece);
        }
    }

    public void setGameClockTick(long tick) {
        // TODO: display this tick somewhere
        Log.i(getClass().getSimpleName(), "setGameClockTick(" + tick + ")");
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

    //endregion


    public interface OnPieClickListener {
        void onUpcomingPieClicked();
        void onPieClicked(@NonNull Pie pie);
    }
}
