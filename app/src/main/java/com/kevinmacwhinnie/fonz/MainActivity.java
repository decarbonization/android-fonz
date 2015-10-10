package com.kevinmacwhinnie.fonz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.kevinmacwhinnie.fonz.game.Game;
import com.kevinmacwhinnie.fonz.state.Life;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Score;
import com.kevinmacwhinnie.fonz.view.BoardView;
import com.kevinmacwhinnie.fonz.view.StatsView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements BoardView.OnPieClickListener {
    private Game game = new Game();

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
        boardView.setOnPieClickListener(this);

        game.bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        game.bus.unregister(this);

        game.gameOver();
        boardView.destroy();
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

    @Subscribe public void onNewGame(@NonNull Game.NewGame ignored) {
        gameToggle.setText(R.string.action_end_game);
    }

    @Subscribe public void onGameOver(@NonNull Game.GameOver ignored) {
        gameToggle.setText(R.string.action_new_game);
        boardView.setUpcomingPiece(null);
    }

    @Override
    public void onUpcomingPieClicked() {
        game.skipPiece();
    }

    @Override
    public void onPieClicked(@NonNull Pie pie) {
        if (!game.tryPlaceCurrentPiece(pie)) {
            Log.e(getClass().getSimpleName(), "Can't put a piece there!");
        }
    }

    //endregion


    //region Actions

    @OnClick(R.id.activity_main_game_control)
    public void onGameControlClicked(@NonNull Button sender) {
        if (game.isInProgress()) {
            game.gameOver();
        } else {
            game.newGame();
        }
    }

    //endregion
}
