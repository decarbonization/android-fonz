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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.kevinmacwhinnie.fonz.data.Formatting;
import com.kevinmacwhinnie.fonz.data.ScoresStore;
import com.kevinmacwhinnie.fonz.graph.GraphActivity;
import com.kevinmacwhinnie.fonz.view.ScoresAdapter;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScoresActivity extends GraphActivity {
    public static final String EXTRA_SCORE = ScoresActivity.class.getName() + ".EXTRA_SCORE";

    @Inject Formatting formatting;
    @Inject ScoresStore scoreStore;

    @Bind(R.id.activity_scores_recycler) RecyclerView recyclerView;

    private ScoresAdapter adapter;


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

        recyclerView.setHasFixedSize(true);

        this.adapter = new ScoresAdapter(this, scoreStore, formatting);
        recyclerView.setAdapter(adapter);

        final IntentFilter intentFilter = new IntentFilter(HighScoreDialogFragment.ACTION_SUBMITTED);
        LocalBroadcastManager.getInstance(this)
                             .registerReceiver(ON_SCORE_SUBMITTED, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recyclerView.setAdapter(null);
        adapter.destroy();

        LocalBroadcastManager.getInstance(this)
                             .unregisterReceiver(ON_SCORE_SUBMITTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scores, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear: {
                clearScores();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    //endregion


    //region Scores

    private final BroadcastReceiver ON_SCORE_SUBMITTED = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String name = intent.getStringExtra(HighScoreDialogFragment.EXTRA_NAME);
            final int score = getIntent().getIntExtra(EXTRA_SCORE, 0);
            scoreStore.insert(name, score);
        }
    };

    private void showNewScoreDialog() {
        final int newScore = getIntent().getIntExtra(EXTRA_SCORE, 0);
        final HighScoreDialogFragment dialogFragment = HighScoreDialogFragment.newInstance(newScore);
        dialogFragment.show(getSupportFragmentManager(), HighScoreDialogFragment.TAG);
    }

    private void clearScores() {
        final AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(R.string.alert_clear_scores_confirm_title);
        confirm.setMessage(R.string.alert_clear_scores_confirm_message);
        confirm.setNegativeButton(android.R.string.cancel, null);
        confirm.setPositiveButton(R.string.action_clear_scores, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scoreStore.clear();
            }
        });
        confirm.show();
    }

    @OnClick(R.id.activity_scores_done)
    public void done(@NonNull Button sender) {
        finish();
    }

    //endregion
}
