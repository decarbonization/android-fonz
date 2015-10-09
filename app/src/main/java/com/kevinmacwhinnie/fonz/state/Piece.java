package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.kevinmacwhinnie.fonz.R;

public enum Piece {
    ORANGE(R.color.piece_orange, R.string.accessibility_piece_orange),
    GREEN(R.color.piece_green, R.string.accessibility_piece_green),
    PURPLE(R.color.piece_purple, R.string.accessibility_piece_purple),
    EMPTY(R.color.transparent, R.string.accessibility_piece_empty);

    public @ColorRes int color;
    public @StringRes int accessibilityName;

    Piece(@ColorRes int color,
          @StringRes int accessibilityName) {
        this.color = color;
        this.accessibilityName = accessibilityName;
    }
}
