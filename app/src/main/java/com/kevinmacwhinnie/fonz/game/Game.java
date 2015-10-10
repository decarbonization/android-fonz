package com.kevinmacwhinnie.fonz.game;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
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

        // TODO: figure out correct timing and scale ratio for count up
        countUp.start();

        bus.post(UpcomingPieceAvailable.INSTANCE);
    }

    @VisibleForTesting
    void reset() {
        Log.d(LOG_TAG, "reset()");

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

        reset();
        doNewCountUp();

        bus.post(NewGame.INSTANCE);
    }

    public boolean tryPlaceCurrentPiece(@NonNull Pie pie) {
        if (pie.tryPlacePiece(upcomingPiece.slot, upcomingPiece.piece)) {
            if (pie.isFull()) {
                score.addPie(pie.isSingleColor());
                pie.reset();
            }

            doNewCountUp();
            return true;
        } else {
            return false;
        }
    }

    public void skipPiece() {
        Log.d(LOG_TAG, "skipPiece()");

        life.death();
        if (life.isAlive()) {
            doNewCountUp();
        } else {
            gameOver();
        }
    }

    public void gameOver() {
        Log.d(LOG_TAG, "gameOver()");

        reset();
        bus.post(GameOver.INSTANCE);
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

    public static class GameOver extends BaseEvent {
        static final GameOver INSTANCE = new GameOver();
    }

    public static class UpcomingPieceAvailable extends BaseEvent {
        static final UpcomingPieceAvailable INSTANCE = new UpcomingPieceAvailable();
    }
}
