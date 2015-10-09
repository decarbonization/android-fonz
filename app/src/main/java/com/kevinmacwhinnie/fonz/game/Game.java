package com.kevinmacwhinnie.fonz.game;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.state.Board;
import com.kevinmacwhinnie.fonz.state.Life;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Score;

import java.util.Observable;

// TODO: replace Observable with something that doesn't suck
// TODO: test the crap out of this class
// TODO: state saving
public class Game extends Observable implements CountUp.Listener {
    public static final String LOG_TAG = Game.class.getSimpleName();

    public final CountUp countUp = new CountUp();
    public final Life life = new Life();
    public final Score score = new Score();
    public final Board board = new Board();

    private final PieceFactory pieceFactory = new PieceFactory();
    private UpcomingPiece upcomingPiece;

    public Game() {
        countUp.addListener(this);
    }

    @Override
    public boolean hasChanged() {
        return true;
    }

    public void advance() {
        Log.d(LOG_TAG, "advance()");

        this.upcomingPiece = pieceFactory.generateUpcomingPiece();
        Log.d(LOG_TAG, "Upcoming piece " + upcomingPiece);

        // TODO: figure out correct timing and scale ratio for count up
        countUp.start();

        notifyObservers();
    }

    public void skipPiece() {
        life.death();
        if (life.isAlive()) {
            advance();
        } else {
            gameOver();
        }
    }

    public void pieFilled(@NonNull Pie pie) {
        score.addPie(pie.isSingleColor());
        pie.reset();
        advance();
    }

    public UpcomingPiece getUpcomingPiece() {
        return upcomingPiece;
    }

    public void start() {
        Log.d(LOG_TAG, "start()");

        reset();
        advance();
    }

    public void pause() {
        Log.d(LOG_TAG, "pause()");

        countUp.pause();
    }

    public void stop() {
        Log.d(LOG_TAG, "stop()");

        reset();
    }

    public void reset() {
        Log.d(LOG_TAG, "reset()");

        countUp.reset();
        life.reset();
        score.reset();
        board.reset();

        this.upcomingPiece = null;
    }

    public void gameOver() {
        // TODO: notifications?

        Log.d(LOG_TAG, "Game over");
        reset();
    }


    @Override
    public void onTicked(long number) {
        // Don't care
    }

    @Override
    public void onCompleted() {
        life.death();
        if (life.isAlive()) {
            advance();
        } else {
            gameOver();
        }
    }
}
