package com.kevinmacwhinnie.fonz.game;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.kevinmacwhinnie.fonz.state.Life;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Score;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.kevinmacwhinnie.fonz.Testing.occurrencesOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class GameTests extends FonzTestCase implements CountUp.Listener {
    private final List<BaseEvent> events = new ArrayList<>();
    private boolean countUpStarted = false;
    private Game game;

    @Before
    public void setUp() {
        this.game = new Game();
        game.countUp.addListener(this);
        game.bus.register(this);
    }

    @After
    public void tearDown() {
        game.bus.unregister(this);
        game.destroy();
        events.clear();

        this.countUpStarted = false;
    }

    @Subscribe public void onLifeChanged(@NonNull Life.Changed change) {
        events.add(change);
    }

    @Subscribe public void onScoreChanged(@NonNull Score.Changed change) {
        events.add(change);
    }

    @Subscribe public void onUpcomingPieceAvailable(@NonNull Game.UpcomingPieceAvailable event) {
        events.add(event);
    }

    @Subscribe public void onNewGame(@NonNull Game.NewGame event) {
        events.add(event);
    }

    @Subscribe public void onGameOver(@NonNull Game.GameOver event) {
        events.add(event);
    }

    @Override
    public void onStarted() {
        this.countUpStarted = true;
    }

    @Override
    public void onTicked(long number) {
    }

    @Override
    public void onCompleted() {
    }


    @Test
    public void doNewCountUp() {
        assertThat(game.isInProgress(), is(false));
        assertThat(game.getUpcomingPiece(), is(nullValue()));

        game.doNewCountUp();

        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));

        assertThat(countUpStarted, is(true));
        assertThat(events, hasItem(Game.UpcomingPieceAvailable.INSTANCE));
    }

    @Test
    public void newGame() {
        assertThat(game.isInProgress(), is(false));
        assertThat(game.getUpcomingPiece(), is(nullValue()));

        game.newGame();

        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));

        assertThat(countUpStarted, is(true));
        assertThat(events, hasItem(Game.NewGame.INSTANCE));
        assertThat(events, hasItem(Game.UpcomingPieceAvailable.INSTANCE));
    }

    @Test
    public void tryPlacePieceOneColor() {
        game.newGame();

        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));

        assertThat(countUpStarted, is(true));
        assertThat(events, hasItem(Game.NewGame.INSTANCE));
        assertThat(events, hasItem(Game.UpcomingPieceAvailable.INSTANCE));

        final Pie pie = game.board.getPie(0);
        for (int i = Pie.SLOT_TOP_CENTER; i < Pie.NUMBER_SLOTS; i++) {
            pie.tryPlacePiece(i, Piece.PURPLE);
        }

        game.upcomingPiece = new UpcomingPiece(Piece.PURPLE, Pie.SLOT_TOP_LEFT);
        assertThat(game.tryPlaceCurrentPiece(pie), is(true));

        assertThat(pie.getOccupiedSlotCount(), is(equalTo(0)));
        assertThat(events, hasItem(new Score.Changed(60)));
        assertThat(events, hasItem(new Life.Changed(Life.INITIAL_VALUE + 1)));

        assertThat(occurrencesOf(events, Game.UpcomingPieceAvailable.INSTANCE), is(equalTo(2)));
    }

    @Test
    public void tryPlacePieceMultipleColors() {
        game.newGame();

        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));

        assertThat(countUpStarted, is(true));
        assertThat(events, hasItem(Game.NewGame.INSTANCE));
        assertThat(events, hasItem(Game.UpcomingPieceAvailable.INSTANCE));

        final Pie pie = game.board.getPie(0);
        for (int i = Pie.SLOT_TOP_CENTER; i < Pie.NUMBER_SLOTS; i++) {
            pie.tryPlacePiece(i, Piece.PURPLE);
        }

        game.upcomingPiece = new UpcomingPiece(Piece.GREEN, Pie.SLOT_TOP_LEFT);
        assertThat(game.tryPlaceCurrentPiece(pie), is(true));

        assertThat(pie.getOccupiedSlotCount(), is(equalTo(0)));
        assertThat(events, hasItem(new Score.Changed(30)));
        assertThat(events, not(hasItem(new Life.Changed(Life.INITIAL_VALUE + 1))));

        assertThat(occurrencesOf(events, Game.UpcomingPieceAvailable.INSTANCE), is(equalTo(2)));
    }

    @Test
    public void skipPiece() {
        game.newGame();

        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));

        assertThat(countUpStarted, is(true));
        assertThat(events, hasItem(Game.NewGame.INSTANCE));
        assertThat(events, hasItem(Game.UpcomingPieceAvailable.INSTANCE));

        game.skipPiece();

        assertThat(events, hasItem(new Life.Changed(Life.INITIAL_VALUE - 1)));
        assertThat(occurrencesOf(events, Game.UpcomingPieceAvailable.INSTANCE), is(equalTo(2)));
        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));
    }

    @Test
    public void gameOver() {
        game.newGame();

        assertThat(game.isInProgress(), is(true));
        assertThat(game.getUpcomingPiece(), is(notNullValue()));

        assertThat(countUpStarted, is(true));
        assertThat(events, hasItem(Game.NewGame.INSTANCE));
        assertThat(events, hasItem(Game.UpcomingPieceAvailable.INSTANCE));

        game.gameOver();

        assertThat(game.isInProgress(), is(false));
        assertThat(game.getUpcomingPiece(), is(nullValue()));

        assertThat(events, hasItem(Game.GameOver.INSTANCE));
    }
}
