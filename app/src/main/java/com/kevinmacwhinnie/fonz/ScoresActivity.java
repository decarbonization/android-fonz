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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.kevinmacwhinnie.fonz.data.Scores;
import com.kevinmacwhinnie.fonz.view.ScoresAdapter;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScoresActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = ScoresActivity.class.getName() + ".EXTRA_SCORE";

    private Scores scores;

    @Bind(R.id.activity_scores_recycler) RecyclerView recyclerView;


    //region Lifecycle

    public static Bundle getArguments(int score) {
        final Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_SCORE, score);
        return arguments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        ButterKnife.bind(this);

        if (savedInstanceState == null && getIntent().hasExtra(EXTRA_SCORE)) {
            showNewScoreDialog();
        }

        // TODO: make this less ghetto
        this.scores = new Scores(this, new Bus());
        scores.initialize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ScoresAdapter adapter = new ScoresAdapter(this, scores);
        recyclerView.setAdapter(adapter);

        final IntentFilter intentFilter = new IntentFilter(HighScoreDialogFragment.ACTION_SUBMITTED);
        LocalBroadcastManager.getInstance(this)
                             .registerReceiver(ON_SCORE_SUBMITTED, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recyclerView.setAdapter(null);

        LocalBroadcastManager.getInstance(this)
                             .unregisterReceiver(ON_SCORE_SUBMITTED);
    }

    //endregion


    //region Scores

    private final BroadcastReceiver ON_SCORE_SUBMITTED = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String name = intent.getStringExtra(HighScoreDialogFragment.EXTRA_NAME);
            final int score = getIntent().getIntExtra(EXTRA_SCORE, 0);
            scores.trackScore(name, score);
        }
    };

    private void showNewScoreDialog() {
        final HighScoreDialogFragment dialogFragment = new HighScoreDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), HighScoreDialogFragment.TAG);
    }

    @OnClick(R.id.activity_scores_clear)
    public void clearScores(@NonNull Button sender) {
        scores.clear();
    }

    //endregion
}
