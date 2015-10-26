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
package com.kevinmacwhinnie.fonz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;

import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.data.Preferences;
import com.kevinmacwhinnie.fonz.game.CountUp;
import com.kevinmacwhinnie.fonz.game.Game;
import com.kevinmacwhinnie.fonz.game.Sounds;
import com.kevinmacwhinnie.fonz.game.TimedMechanics;
import com.kevinmacwhinnie.fonz.graph.GraphActivity;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Life;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Score;
import com.kevinmacwhinnie.fonz.view.BoardView;
import com.kevinmacwhinnie.fonz.view.StatsView;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import is.hello.go99.animators.AnimatorContext;
import is.hello.go99.animators.OnAnimationCompleted;

public class MainActivity extends GraphActivity
        implements BoardView.Listener, AnimatorContext.Scene {
    private final AnimatorContext animatorContext = new AnimatorContext(getClass().getSimpleName());

    @Inject Game game;
    @Inject Sounds sounds;
    @Inject SharedPreferences preferences;

    @Bind(R.id.activity_main_stats) StatsView statsView;
    @Bind(R.id.activity_main_board) BoardView boardView;
    @Bind(R.id.activity_main_game_control) Button gameToggle;


    //region Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        statsView.setLife(game.life.getValue());
        statsView.setScore(game.score.getValue());

        boardView.setBoard(game.board);
        boardView.setUpcomingPiece(game.getUpcomingPiece());
        boardView.setAnimatorContext(getAnimatorContext());
        boardView.setListener(this);

        game.bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        boardView.destroy();

        game.bus.unregister(this);

        game.gameOver(Game.GameOver.How.GAME_LOGIC);
        game.destroy();
    }

    //endregion


    //region Events

    @Subscribe public void onLifeChanged(@NonNull Life.Changed change) {
        statsView.setLife(change.value);
    }

    @Subscribe public void onScoreChanged(@NonNull Score.Changed change) {
        statsView.setScore(change.value);
    }

    @Subscribe public void onUpcomingPieceAvailable(@NonNull Game.UpcomingPieceAvailable ignored) {
        boardView.setUpcomingPiece(game.getUpcomingPiece());
    }

    @Subscribe public void onPowerUpChanged(@NonNull Board.PowerUpChanged change) {
        final PowerUp powerUp = change.value;
        boardView.setPowerUpAvailable(powerUp, game.board.hasPowerUp(powerUp));
    }

    @Subscribe public void onPowerUpScheduled(@NonNull TimedMechanics.PowerUpScheduled change) {
        // Do nothing for now.
    }

    @Subscribe public void onCountUpTicked(@NonNull CountUp.Ticked tick) {
        boardView.setTick(tick.getValue());
    }

    @Subscribe public void onNewGame(@NonNull Game.NewGame ignored) {
        gameToggle.setText(R.string.action_end_game);
        boardView.setTick(1);
    }

    @Subscribe public void onGameOver(@NonNull Game.GameOver event) {
        gameToggle.setText(R.string.action_new_game);
        boardView.setUpcomingPiece(null);
        boardView.setTick(0);
        for (final PowerUp powerUp : PowerUp.values()) {
            boardView.setPowerUpAvailable(powerUp, false);
        }

        if (event.how == Game.GameOver.How.GAME_LOGIC) {
            final Intent scoresActivity = new Intent(this, ScoresActivity.class);
            scoresActivity.putExtras(ScoresActivity.getArguments(event.score));
            startActivity(scoresActivity);
        }

        sounds.playGameOver();
    }

    //endregion


    //region Interaction

    @Override
    public void onUpcomingPieClicked() {
        final boolean skipEnabled = preferences.getBoolean(Preferences.SKIP_ON_UPCOMING_CLICK, true);
        if (skipEnabled) {
            game.skipPiece();
        }
    }

    @Override
    public void onPieClicked(final int pieSlot, @NonNull final Pie pie) {
        if (!game.canPlaceCurrentPiece(pie)) {
            sounds.playForbiddenPlacement();
        } else {
            game.countUp.pause();
            boardView.animateCurrentPieceIntoPie(pieSlot, new OnAnimationCompleted() {
                @Override
                public void onAnimationCompleted(boolean finished) {
                    if (finished && !game.tryPlaceCurrentPiece(pie)) {
                        sounds.playForbiddenPlacement();
                    }
                }
            });
        }
    }

    @Override
    public void onPowerUpClicked(@NonNull PowerUp powerUp) {
        switch (powerUp) {
            case MULTIPLY_SCORE:
                game.multiplyScore();
                break;

            case CLEAR_ALL:
                game.clearAll();
                break;

            case SLOW_DOWN_TIME:
                game.slowDownTime();
                break;
        }
    }

    //endregion


    //region Actions

    @OnClick(R.id.activity_main_game_control)
    public void onGameControlClicked(@NonNull Button sender) {
        if (game.isInProgress()) {
            game.gameOver(Game.GameOver.How.USER_INTERVENTION);
        } else {
            game.newGame();
        }
    }

    //endregion


    //region Animations

    @NonNull
    @Override
    public AnimatorContext getAnimatorContext() {
        return animatorContext;
    }

    //endregion
}
