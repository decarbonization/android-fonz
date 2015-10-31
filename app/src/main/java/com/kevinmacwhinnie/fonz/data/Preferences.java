/*
 * Copyright (c) 2015, Peter 'Kevin' MacWhinnie
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions may not be sold, nor may they be used in a commercial
 *    product or activity.
 * 2. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 3. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.kevinmacwhinnie.fonz.data;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.events.ValueBaseEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class Preferences implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SAVED_SCORE_NAME = "saved_score_name";
    public static final String SKIP_ON_UPCOMING_CLICK = "skip_on_upcoming_click";
    public static final String PREVENT_DUPLICATE_PIECES = "prevent_duplicate_pieces";

    private final SharedPreferences preferences;
    private final Bus bus;

    @Inject public Preferences(@NonNull SharedPreferences preferences,
                               @NonNull Bus bus) {
        this.preferences = preferences;
        this.bus = bus;

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SKIP_ON_UPCOMING_CLICK: {
                bus.post(new SkipOnUpcomingClickChanged(getSkipOnUpcomingClick()));
                break;
            }

            case PREVENT_DUPLICATE_PIECES: {
                bus.post(new PreventDuplicatePiecesChanged(getPreventDuplicatePieces()));
                break;
            }
        }
    }


    //region Preferences

    public void setSavedScoreName(@NonNull String name) {
        preferences.edit()
                   .putString(SAVED_SCORE_NAME, name)
                   .apply();
    }

    public String getSavedScoreName() {
        return preferences.getString(SAVED_SCORE_NAME, null);
    }

    public void setSkipOnUpcomingClick(boolean skipOnUpcomingClick) {
        preferences.edit()
                   .putBoolean(SKIP_ON_UPCOMING_CLICK, skipOnUpcomingClick)
                   .apply();
    }

    public boolean getSkipOnUpcomingClick() {
        return preferences.getBoolean(SKIP_ON_UPCOMING_CLICK, true);
    }

    public void setPreventDuplicatePieces(boolean preventDuplicatePieces) {
        preferences.edit()
                   .putBoolean(PREVENT_DUPLICATE_PIECES, preventDuplicatePieces)
                   .apply();
    }

    public boolean getPreventDuplicatePieces() {
        return preferences.getBoolean(PREVENT_DUPLICATE_PIECES, true);
    }

    //endregion


    //region Events

    public static class SkipOnUpcomingClickChanged extends ValueBaseEvent<Boolean> {
        public SkipOnUpcomingClickChanged(boolean value) {
            super(value);
        }
    }

    public static class PreventDuplicatePiecesChanged extends ValueBaseEvent<Boolean> {
        public PreventDuplicatePiecesChanged(boolean value) {
            super(value);
        }
    }

    //endregion
}
