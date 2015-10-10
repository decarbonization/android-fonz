package com.kevinmacwhinnie.fonz;

import android.support.annotation.NonNull;

public class Testing {
    public static <T> int occurrencesOf(@NonNull Iterable<? extends T> collection, T value) {
        int found = 0;
        for (final T item : collection) {
            if (item.equals(value)) {
                found++;
            }
        }

        return found;
    }
}
