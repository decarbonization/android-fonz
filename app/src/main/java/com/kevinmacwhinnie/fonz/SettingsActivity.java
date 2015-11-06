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

package com.kevinmacwhinnie.fonz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.data.Preferences;
import com.kevinmacwhinnie.fonz.graph.GraphActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class SettingsActivity extends GraphActivity {
    @Bind(R.id.activity_settings_version_footer) TextView versionFooter;
    @Bind(R.id.activity_settings_tap_to_skip_piece) Switch tapToSkipPiece;
    @Bind(R.id.activity_settings_prevent_duplicate_pieces) Switch preventDuplicatePieces;
    @Bind(R.id.activity_settings_timer_scale_factor) EditText timerScaleFactor;

    @Inject Bus bus;
    @Inject Preferences preferences;


    //region Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        bus.register(this);

        versionFooter.setText(getString(R.string.settings_version_fmt,
                                        BuildConfig.VERSION_NAME,
                                        BuildConfig.BUILD_TYPE,
                                        BuildConfig.VERSION_CODE));

        tapToSkipPiece.setChecked(preferences.getSkipOnUpcomingClick());
        preventDuplicatePieces.setChecked(preferences.getPreventDuplicatePieces());
        timerScaleFactor.setText(formatTimerScaleFactor(preferences.getTimerScaleFactor()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timerScaleFactor.isFocused()) {
            final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(timerScaleFactor.getWindowToken(), 0);
        }

        bus.unregister(this);
    }

    //endregion


    //region Preferences

    @OnClick(R.id.activity_settings_tap_to_skip_piece)
    public void onTapToSkipClicked(@NonNull Switch sender) {
        preferences.setSkipOnUpcomingClick(sender.isChecked());
    }

    @OnClick(R.id.activity_settings_prevent_duplicate_pieces)
    public void onPreventDuplicatePiecesClicked(@NonNull Switch sender) {
        preferences.setPreventDuplicatePieces(sender.isChecked());
    }

    @OnEditorAction(R.id.activity_settings_timer_scale_factor)
    public boolean onTimerScaleFactorEditorAction(@NonNull TextView sender,
                                                  int editorAction,
                                                  @NonNull KeyEvent event) {
        if (editorAction == EditorInfo.IME_ACTION_GO ||
                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            try {
                final float timerScaleFactor =
                        Float.parseFloat(this.timerScaleFactor.getText().toString());
                preferences.setTimerScaleFactor(timerScaleFactor);
            } catch (NumberFormatException e) {
                Log.w(getClass().getSimpleName(), "Could not parse scale factor", e);
                timerScaleFactor.setText(formatTimerScaleFactor(preferences.getTimerScaleFactor()));
            }

            return true;
        }

        return false;
    }

    //endregion


    //region Actions

    @OnClick(R.id.activity_settings_high_scores)
    public void onHighScoresClicked(@NonNull Button sender) {
        startActivity(new Intent(this, ScoresActivity.class));
    }

    @OnClick(R.id.activity_settings_trophy_room)
    public void onTrophyRoomClicked(@NonNull Button sender) {
        startActivity(new Intent(this, TrophyRoomActivity.class));
    }

    @OnClick(R.id.activity_settings_restore_defaults)
    public void onRestoreDefaultsClicked(@NonNull Button sender) {
        preferences.restoreDefaults();
    }

    //endregion


    //region Events

    @Subscribe public void onSkipOnUpcomingClickChanged(@NonNull Preferences.SkipOnUpcomingClickChanged event) {
        tapToSkipPiece.setChecked(event.value);
    }

    @Subscribe public void onPreventDuplicatePiecesChanged(@NonNull Preferences.PreventDuplicatePiecesChanged event) {
        preventDuplicatePieces.setChecked(event.value);
    }

    @Subscribe public void onTimerScaleFactorChanged(@NonNull Preferences.TimerScaleFactorChanged event) {
        timerScaleFactor.setText(formatTimerScaleFactor(event.value));
    }

    //endregion


    private static String formatTimerScaleFactor(float scaleFactor) {
        return String.format("%.2f", scaleFactor);
    }
}
