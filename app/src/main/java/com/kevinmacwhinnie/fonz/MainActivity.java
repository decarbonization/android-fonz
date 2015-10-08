package com.kevinmacwhinnie.fonz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kevinmacwhinnie.fonz.state.Pie;
import com.kevinmacwhinnie.fonz.state.Piece;
import com.kevinmacwhinnie.fonz.view.PieView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PieView pieView = (PieView) findViewById(R.id.activity_main_pie_1);
        final Pie pie = new Pie();
        pie.tryPlacePiece(0, Piece.ORANGE);
        pie.tryPlacePiece(1, Piece.PURPLE);
        pie.tryPlacePiece(2, Piece.GREEN);
        pie.tryPlacePiece(3, Piece.ORANGE);
        pie.tryPlacePiece(4, Piece.PURPLE);
        pie.tryPlacePiece(5, Piece.GREEN);
        pieView.setPie(pie);
    }
}
