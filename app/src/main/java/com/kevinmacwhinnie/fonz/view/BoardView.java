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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.game.TimedMechanics;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.util.Animations;
import com.kevinmacwhinnie.fonz.view.util.Views;

import is.hello.go99.animators.AnimatorContext;
import is.hello.go99.animators.AnimatorTemplate;
import is.hello.go99.animators.OnAnimationCompleted;

public class BoardView extends LinearLayout
        implements PieceDrawable.PieceProvider {
    private final PieView[] pieViews;
    private final SimpleArrayMap<PowerUp, PowerUpButton> powerUpButtons;
    private final UpcomingPieceView upcomingPieceView;
    private final PieceDrawable placementPieceDrawable;

    private Board board;
    private Listener listener;

    private @NonNull AnimatorTemplate placementTemplate = Animations.PLACEMENT_TEMPLATE;
    private @Nullable AnimatorContext animatorContext;
    private @Nullable UpcomingPiece placementPiece;
    private @Nullable ValueAnimator placementAnimator;

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

        this.pieViews = new PieView[] {
                (PieView) findViewById(R.id.view_game_board_pie_left_top),
                (PieView) findViewById(R.id.view_game_board_pie_center_top),
                (PieView) findViewById(R.id.view_game_board_pie_right_top),
                (PieView) findViewById(R.id.view_game_board_pie_left_bottom),
                (PieView) findViewById(R.id.view_game_board_pie_center_bottom),
                (PieView) findViewById(R.id.view_game_board_pie_right_bottom),
        };
        for (int i = 0, length = pieViews.length; i < length; i++) {
            final PieView pieView = pieViews[i];
            pieView.setTag(R.id.view_board_pie_slot_tag, i);
            pieView.setOnClickListener(ON_PIE_CLICKED);
        }

        final PowerUpButton[] powerUpButtons = new PowerUpButton[] {
                (PowerUpButton) findViewById(R.id.view_game_board_power_up_multiply_score),
                (PowerUpButton) findViewById(R.id.view_game_board_power_up_clear_all),
                (PowerUpButton) findViewById(R.id.view_game_board_power_up_slow_down),
        };
        this.powerUpButtons = new SimpleArrayMap<>(powerUpButtons.length);
        for (final PowerUpButton powerUpButton : powerUpButtons) {
            powerUpButton.setOnClickListener(ON_POWER_UP_CLICKED);
            this.powerUpButtons.put(powerUpButton.getPowerUp(), powerUpButton);
        }

        this.upcomingPieceView = (UpcomingPieceView) findViewById(R.id.view_game_board_upcoming_piece);
        upcomingPieceView.setOnClickListener(ON_UPCOMING_PIE_CLICKED);

        this.placementPieceDrawable = new PieceDrawable(this, getResources());
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

        for (final PowerUp powerUp : PowerUp.values()) {
            powerUpButtons.get(powerUp).setEnabled(board.hasPowerUp(powerUp));
        }
    }

    public void setUpcomingPiece(@Nullable UpcomingPiece upcomingPiece) {
        upcomingPieceView.setUpcomingPiece(upcomingPiece);

        if (placementAnimator != null) {
            placementAnimator.cancel();
        }
    }

    public void setPaused(boolean isPaused) {
        upcomingPieceView.setPaused(isPaused);

        for (int i = 0, size = powerUpButtons.size(); i < size; i++) {
            powerUpButtons.valueAt(i).setClickable(!isPaused);
        }
        for (final PieView pieView : pieViews) {
            pieView.setClickable(!isPaused);
        }


        if (placementAnimator != null) {
            placementAnimator.cancel();
        }
    }

    public void setPowerUpAvailable(@NonNull PowerUp powerUp, boolean available) {
        powerUpButtons.get(powerUp).setEnabled(available);
    }

    public void setPowerUpActive(@NonNull PowerUp powerUp, boolean active) {
        final PowerUpButton powerUpButton = powerUpButtons.get(powerUp);
        if (active) {
            powerUpButton.setTick(TimedMechanics.STANDARD_DURATION_TICKS);
            powerUpButton.setClickable(false);
        } else {
            powerUpButton.setTick(0);
            powerUpButton.setClickable(true);
        }
    }

    public void setPowerUpTick(@NonNull PowerUp powerUp, int tick) {
        powerUpButtons.get(powerUp).setTick(tick);
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void setTick(int tick) {
        upcomingPieceView.setTick(tick);
    }

    //endregion


    //region Animations

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility != VISIBLE) {
            clearAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        clearAnimation();
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();

        if (placementAnimator != null) {
            placementAnimator.cancel();
        }
    }

    public void setPlacementTemplate(@NonNull AnimatorTemplate placementTemplate) {
        this.placementTemplate = placementTemplate;
    }

    public void setAnimatorContext(@Nullable AnimatorContext animatorContext) {
        this.animatorContext = animatorContext;
    }

    public void animateCurrentPieceIntoPie(final int pieSlot,
                                           @NonNull final OnAnimationCompleted onAnimationCompleted) {
        if (placementAnimator != null) {
            placementAnimator.cancel();
        }

        final Rect startRect = Views.copyChildRect(this, upcomingPieceView);
        startRect.inset(upcomingPieceView.getPieceInset(),
                        upcomingPieceView.getPieceInset());

        final Rect endRect = Views.copyChildRect(this, pieViews[pieSlot]);

        this.placementAnimator = placementTemplate.createRectAnimator(startRect, endRect);
        placementAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                final Rect rect = (Rect) animator.getAnimatedValue();
                placementPieceDrawable.setBounds(rect);
            }
        });
        placementAnimator.addListener(new OnAnimationCompleted.Adapter(onAnimationCompleted));
        placementAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation == placementAnimator) {
                    BoardView.this.placementAnimator = null;
                    BoardView.this.placementPiece = null;

                    getOverlay().remove(placementPieceDrawable);
                }
            }
        });

        this.placementPiece = upcomingPieceView.getUpcomingPiece();
        upcomingPieceView.setUpcomingPiece(null);

        placementPieceDrawable.setBounds(startRect);
        getOverlay().add(placementPieceDrawable);

        if (animatorContext != null) {
            animatorContext.bind(placementAnimator, "BoardView#placementAnimator");
        }
        placementAnimator.start();
    }

    @NonNull
    @Override
    public Piece getPiece(@Pie.Slot int slot) {
        if (placementPiece != null && slot == placementPiece.slot) {
            return placementPiece.piece;
        } else {
            return Piece.EMPTY;
        }
    }

    //endregion


    //region Events

    @SuppressWarnings("FieldCanBeLocal")
    private final OnClickListener ON_PIE_CLICKED = new OnClickListener() {
        @Override
        public void onClick(View pieView) {
            if (listener != null && board != null) {
                final int slot = (int) pieView.getTag(R.id.view_board_pie_slot_tag);
                listener.onPieClicked(slot, board.getPie(slot));
            }
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final OnClickListener ON_UPCOMING_PIE_CLICKED = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null && board != null) {
                listener.onUpcomingPieClicked();
            }
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final OnClickListener ON_POWER_UP_CLICKED = new OnClickListener() {
        @Override
        public void onClick(View powerUpView) {
            if (listener != null && board != null) {
                final PowerUp powerUp = ((PowerUpButton) powerUpView).getPowerUp();
                listener.onPowerUpClicked(powerUp);
            }
        }
    };

    //endregion


    public interface Listener {
        void onUpcomingPieClicked();
        void onPieClicked(int pieSlot, @NonNull Pie pie);
        void onPowerUpClicked(@NonNull PowerUp powerUp);
    }
}
