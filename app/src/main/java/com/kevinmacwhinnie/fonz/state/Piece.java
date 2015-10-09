package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.kevinmacwhinnie.fonz.R;

public enum Piece {
    ORANGE(R.color.piece_orange, R.string.piece_orange),
    GREEN(R.color.piece_green, R.string.piece_green),
    PURPLE(R.color.piece_purple, R.string.piece_purple),
    NONE(R.color.transparent, R.string.app_name);

    public @ColorRes int color;
    public @StringRes int accessibilityName;

    Piece(@ColorRes int color,
          @StringRes int accessibilityName) {
        this.color = color;
        this.accessibilityName = accessibilityName;
    }
}
