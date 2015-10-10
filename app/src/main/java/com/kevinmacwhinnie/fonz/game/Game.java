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
package com.kevinmacwhinnie.fonz.game;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Life;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Score;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

// TODO: state saving
public class Game implements CountUp.Listener {
    public static final String LOG_TAG = Game.class.getSimpleName();

    public final Bus bus = new Bus(ThreadEnforcer.MAIN, "Game#bus");
    public final CountUp countUp = new CountUp();
    public final Life life = new Life(bus);
    public final Score score = new Score(bus);
    public final Board board = new Board(bus);

    private final PieceFactory pieceFactory = new PieceFactory();
    private boolean inProgress = false;
    @VisibleForTesting UpcomingPiece upcomingPiece;


    //region Lifecycle

    public Game() {
        countUp.addListener(this);
    }

    public void destroy() {
        reset();
        countUp.clearListeners();
    }

    //endregion


    //region Attributes

    public boolean isInProgress() {
        return inProgress;
    }

    public @Nullable UpcomingPiece getUpcomingPiece() {
        return upcomingPiece;
    }

    //endregion


    //region Internal

    @VisibleForTesting
    void doNewCountUp() {
        Log.d(LOG_TAG, "doNewCountUp()");

        this.inProgress = true;
        this.upcomingPiece = pieceFactory.generateUpcomingPiece();
        Log.d(LOG_TAG, "Upcoming piece " + upcomingPiece);

        countUp.start();

        bus.post(UpcomingPieceAvailable.INSTANCE);
    }

    @VisibleForTesting
    void reset() {
        Log.d(LOG_TAG, "reset()");

        pieceFactory.reset();
        countUp.reset();
        life.reset();
        score.reset();
        board.reset();

        this.inProgress = false;
        this.upcomingPiece = null;
    }

    //endregion


    //region Controls

    public void newGame() {
        Log.d(LOG_TAG, "newGame()");

        if (!inProgress) {
            reset();
            doNewCountUp();

            bus.post(NewGame.INSTANCE);
        }
    }

    public boolean tryPlaceCurrentPiece(@NonNull Pie pie) {
        if (inProgress && pie.tryPlacePiece(upcomingPiece.slot, upcomingPiece.piece)) {
            if (pie.isFull()) {
                final boolean isSingleColor = pie.isSingleColor();
                score.addPie(isSingleColor);
                pie.reset();

                if (isSingleColor) {
                    life.increment();
                }
            }

            countUp.scaleTickDuration(CountUp.DEFAULT_SCALE_FACTOR);
            doNewCountUp();
            return true;
        } else {
            return false;
        }
    }

    public void skipPiece() {
        Log.d(LOG_TAG, "skipPiece()");

        if (inProgress) {
            life.decrement();
            if (life.isAlive()) {
                countUp.scaleTickDuration(CountUp.DEFAULT_SCALE_FACTOR);
                doNewCountUp();
            } else {
                gameOver(GameOver.How.GAME_LOGIC);
            }
        }
    }

    public void gameOver(@NonNull GameOver.How how) {
        Log.d(LOG_TAG, "gameOver()");

        if (inProgress) {
            reset();
            bus.post(new GameOver(how));
        }
    }

    //endregion


    //region Timer

    @Override
    public void onStarted() {
        // Don't care
    }

    @Override
    public void onTicked(long number) {
        // Don't care
    }

    @Override
    public void onCompleted() {
        skipPiece();
    }

    //endregion


    public static class NewGame extends BaseEvent {
        static final NewGame INSTANCE = new NewGame();
    }

    public static class GameOver extends ValueBaseEvent<GameOver.How> {
        public GameOver(@NonNull How value) {
            super(value);
        }

        public enum How {
            USER_INTERVENTION,
            GAME_LOGIC,
        }
    }

    public static class UpcomingPieceAvailable extends BaseEvent {
        static final UpcomingPieceAvailable INSTANCE = new UpcomingPieceAvailable();
    }
}
