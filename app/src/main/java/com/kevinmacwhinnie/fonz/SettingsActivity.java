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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
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

public class SettingsActivity extends GraphActivity {
    @Bind(R.id.activity_settings_version_footer) TextView versionFooter;
    @Bind(R.id.activity_settings_tap_to_skip_piece) Switch tapToSkipPiece;
    @Bind(R.id.activity_settings_prevent_duplicate_pieces) Switch preventDuplicatePieces;

    @Inject Bus bus;
    @Inject Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        bus.register(this);

        versionFooter.setText(getString(R.string.version_fmt,
                                        BuildConfig.VERSION_NAME,
                                        BuildConfig.BUILD_TYPE,
                                        BuildConfig.VERSION_CODE));

        tapToSkipPiece.setChecked(preferences.getSkipOnUpcomingClick());
        preventDuplicatePieces.setChecked(preferences.getPreventDuplicatePieces());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bus.unregister(this);
    }

    @OnClick(R.id.activity_settings_tap_to_skip_piece)
    public void onTapToSkipClicked(@NonNull Switch sender) {
        preferences.setSkipOnUpcomingClick(sender.isChecked());
    }

    @OnClick(R.id.activity_settings_prevent_duplicate_pieces)
    public void onPreventDuplicatePiecesClicked(@NonNull Switch sender) {
        preferences.setPreventDuplicatePieces(sender.isChecked());
    }

    @OnClick(R.id.activity_settings_high_scores)
    public void onHighScoresClicked(@NonNull Button sender) {
        startActivity(new Intent(this, ScoresActivity.class));
    }


    @Subscribe public void onSkipOnUpcomingClickChanged(@NonNull Preferences.SkipOnUpcomingClickChanged event) {
        tapToSkipPiece.setChecked(event.value);
    }

    @Subscribe public void onPreventDuplicatePiecesChanged(@NonNull Preferences.PreventDuplicatePiecesChanged event) {
        preventDuplicatePieces.setChecked(event.value);
    }
}
