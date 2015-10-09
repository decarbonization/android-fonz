package com.kevinmacwhinnie.fonz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.data.UpcomingPiece;
import com.kevinmacwhinnie.fonz.game.CountUp;
import com.kevinmacwhinnie.fonz.game.Game;
import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.view.BoardView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, CountUp.Listener, BoardView.OnPieClickListener {
    private Game game = new Game();

    private TextView livesText;
    private TextView scoreText;
    private BoardView boardView;
    private Button gameToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.livesText = (TextView) findViewById(R.id.activity_main_lives);
        this.scoreText = (TextView) findViewById(R.id.activity_main_score);
        this.boardView = (BoardView) findViewById(R.id.activity_main_board);
        boardView.setBoard(game.board);
        boardView.setOnPieClickListener(this);

        this.gameToggle = (Button) findViewById(R.id.activity_main_game_control);
        gameToggle.setOnClickListener(START_GAME);

        setUpObservations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        tearDownObservations();

        game.reset();
        boardView.destroy();
    }

    private void setUpObservations() {
        game.addObserver(this);
        game.life.addObserver(this);
        game.score.addObserver(this);
        game.countUp.addListener(this);
    }

    private void tearDownObservations() {
        game.deleteObserver(this);
        game.life.deleteObserver(this);
        game.score.deleteObserver(this);
        game.countUp.removeListener(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d(getClass().getSimpleName(), "update(" + observable + ")");

        if (observable == game) {
            boardView.setUpcomingPiece(game.getUpcomingPiece());
        } else if (observable == game.life) {
            livesText.setText(Integer.toString(game.life.getCount()));
        } else if (observable == game.score) {
            scoreText.setText(Integer.toString(game.score.getValue()));
        }
    }

    @Override
    public void onTicked(long number) {
        boardView.setGameClockTick(number);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onUpcomingPieClicked() {
        game.skipPiece();
    }

    @Override
    public void onPieClicked(@NonNull Pie pie) {
        final UpcomingPiece upcomingPiece = game.getUpcomingPiece();
        if (!pie.tryPlacePiece(upcomingPiece.slot, upcomingPiece.piece)) {
            Log.e(getClass().getSimpleName(), "Can't put a piece there!");
        } else if (pie.isFull()) {
            game.pieFilled(pie);
        } else {
            game.advance();
        }
    }

    private final View.OnClickListener START_GAME = new View.OnClickListener() {
        @Override
        public void onClick(View ignored) {
            game.start();
        }
    };
}
