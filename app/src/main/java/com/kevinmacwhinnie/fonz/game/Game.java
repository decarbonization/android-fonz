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

import com.kevinmacwhinnie.fonz.data.PowerUp;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Life;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Score;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class Game {
    public static final String LOG_TAG = Game.class.getSimpleName();

    public final Bus bus;

    public final CountUp countUp;
    public final TimedMechanics timedMechanics;

    public final Life life;
    public final Score score;
    public final Board board;

    private final PieceFactory pieceFactory = new PieceFactory();
    private boolean inProgress = false;
    private boolean isPaused = false;
    @VisibleForTesting UpcomingPiece upcomingPiece;


    //region Lifecycle

    public Game(@NonNull Bus bus) {
        this.bus = bus;

        this.countUp = new CountUp(bus);
        this.timedMechanics = new TimedMechanics(bus);

        this.life = new Life(bus);
        this.score = new Score(bus);
        this.board = new Board(bus);

        bus.register(this);
    }

    public void destroy() {
        bus.unregister(this);
        reset();
    }

    //endregion


    //region Attributes

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public @Nullable UpcomingPiece getUpcomingPiece() {
        return upcomingPiece;
    }

    public void setPreventDuplicatePieces(boolean preventDuplicatePieces) {
        pieceFactory.setPreventDuplicatePieces(preventDuplicatePieces);
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
        timedMechanics.reset();

        life.reset();
        score.reset();
        board.reset();

        this.inProgress = false;
        this.isPaused = false;
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

    public boolean canPlaceCurrentPiece(@NonNull Pie pie) {
        return (inProgress && pie.canPlacePiece(upcomingPiece.slot, upcomingPiece.piece));
    }

    public boolean tryPlaceCurrentPiece(@NonNull Pie pie) {
        if (inProgress && pie.tryPlacePiece(upcomingPiece.slot, upcomingPiece.piece)) {
            if (pie.isFull()) {
                final boolean isSingleColor = pie.isSingleColor();
                score.addPie(isSingleColor);
                pie.reset();

                if (isSingleColor) {
                    life.increment();

                    final int slot = board.getSlot(pie);
                    if (board.pieHasPowerUp(slot)) {
                        board.addPowerUp(PowerUp.values()[slot]);
                    }
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

    public void pause() {
        Log.d(LOG_TAG, "pause()");

        if (!isPaused) {
            countUp.pause();
            this.isPaused = true;

            bus.post(new PauseStateChanged(true));
        }
    }

    public void resume() {
        Log.d(LOG_TAG, "resume()");

        if (isPaused) {
            countUp.resume();
            this.isPaused = false;

            bus.post(new PauseStateChanged(false));
        }
    }

    public void gameOver(@NonNull GameOver.How how) {
        Log.d(LOG_TAG, "gameOver()");

        if (inProgress) {
            final int finalScore = score.getValue();
            reset();
            bus.post(new GameOver(how, finalScore));
        }
    }

    //endregion


    //region Power Ups

    public boolean clearAll() {
        Log.d(LOG_TAG, "clearAll()");

        if (inProgress && board.usePowerUp(PowerUp.CLEAR_ALL)) {
            for (int i = 0; i < Board.NUMBER_PIES; i++) {
                board.getPie(i).reset();
            }
            doNewCountUp();

            return true;
        } else {
            return false;
        }
    }

    public boolean multiplyScore() {
        Log.d(LOG_TAG, "useScoreMultiplier()");

        if (inProgress && board.hasPowerUp(PowerUp.MULTIPLY_SCORE) &&
                !timedMechanics.isPending(PowerUp.MULTIPLY_SCORE)) {
            score.setMultiplier(2f);
            timedMechanics.schedulePowerUp(PowerUp.MULTIPLY_SCORE);
            return true;
        } else {
            return false;
        }
    }

    public boolean slowDownTime() {
        Log.d(LOG_TAG, "slowDownTime()");

        if (inProgress && board.hasPowerUp(PowerUp.SLOW_DOWN_TIME) &&
                !timedMechanics.isPending(PowerUp.SLOW_DOWN_TIME)) {
            countUp.scaleTickDuration(2f);
            timedMechanics.schedulePowerUp(PowerUp.SLOW_DOWN_TIME);
            return true;
        } else {
            return false;
        }
    }

    @Subscribe public void onPowerUpExpired(@NonNull TimedMechanics.PowerUpExpired expiration) {
        final PowerUp powerUp = expiration.value;
        Log.d(LOG_TAG, "onPowerUpExpired(" + powerUp + ")");

        board.usePowerUp(powerUp);
        switch (powerUp) {
            case MULTIPLY_SCORE:
                score.setMultiplier(1f);
                break;

            case CLEAR_ALL:
                // Do nothing.
                break;

            case SLOW_DOWN_TIME:
                countUp.scaleTickDuration(0.5f);
                break;
        }
    }

    //endregion


    //region Timer

    @Subscribe public void onCountUpCompleted(@NonNull CountUp.Completed ignored) {
        skipPiece();
    }

    //endregion


    public static class NewGame extends BaseEvent {
        static final NewGame INSTANCE = new NewGame();
    }

    public static class GameOver extends BaseEvent {
        public final How how;
        public final int score;

        public GameOver(@NonNull How how, int score) {
            this.how = how;
            this.score = score;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GameOver gameOver = (GameOver) o;

            return score == gameOver.score && how == gameOver.how;

        }

        @Override
        public int hashCode() {
            int result = how.hashCode();
            result = 31 * result + score;
            return result;
        }

        @Override
        public String toString() {
            return "GameOver{" +
                    "how=" + how +
                    ", score=" + score +
                    '}';
        }

        public enum How {
            USER_INTERVENTION,
            GAME_LOGIC,
        }
    }

    public static class UpcomingPieceAvailable extends BaseEvent {
        static final UpcomingPieceAvailable INSTANCE = new UpcomingPieceAvailable();
    }

    public static class PauseStateChanged extends ValueBaseEvent<Boolean> {
        public PauseStateChanged(boolean value) {
            super(value);
        }
    }
}
