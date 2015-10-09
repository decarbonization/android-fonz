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
        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.ORANGE);
        pieView.setPie(pie);
    }
}
