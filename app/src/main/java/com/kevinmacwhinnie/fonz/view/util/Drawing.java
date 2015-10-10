package com.kevinmacwhinnie.fonz.view.util;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

public class Drawing {
    @SuppressWarnings("deprecation")
    public static @ColorInt int getColor(@NonNull Resources resources, @ColorRes int colorRes) {
        // Replace this method with ResourcesCompat when it catches up.
        return resources.getColor(colorRes);
    }
}
