package com.kevinmacwhinnie.fonz.state;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public enum Piece {
    ORANGE(Color.RED),
    GREEN(Color.GREEN),
    PURPLE(Color.MAGENTA),
    NONE(Color.TRANSPARENT);

    public @ColorInt int color;

    Piece(@ColorInt int color) {
        this.color = color;
    }
}
