package com.kevinmacwhinnie.fonz.game;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;

public class Sounds {
    private final Vibrator vibrator;

    public Sounds(@NonNull Context context) {
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void playForbiddenPlacement() {
        vibrator.vibrate(50L);
    }

    public void playGameOver() {
        vibrator.vibrate(new long[] {100L, 100L, 200L, 100L, 300L, 100L}, -1);
    }
}
