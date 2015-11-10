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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.kevinmacwhinnie.fonz.achievements.Achievement;
import com.kevinmacwhinnie.fonz.achievements.TrophyRoom;
import com.kevinmacwhinnie.fonz.graph.GraphActivity;
import com.kevinmacwhinnie.fonz.view.AchievementsAdapter;
import com.kevinmacwhinnie.fonz.view.DividerItemDecoration;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrophyRoomActivity extends GraphActivity implements AchievementsAdapter.OnShareClickListener {
    @Inject TrophyRoom trophyRoom;

    @Bind(R.id.activity_trophy_room_recycler) RecyclerView recyclerView;


    //region Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_room);
        ButterKnife.bind(this);

        final AchievementsAdapter adapter =
                new AchievementsAdapter(this, this, trophyRoom.getUnlockedAchievements());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources()));
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
                clearAchievements();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    //endregion


    //region Actions

    public void clearAchievements() {
        final AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(R.string.alert_clear_achievements_confirm_title);
        confirm.setMessage(R.string.alert_clear_achievements_confirm_message);
        confirm.setNegativeButton(android.R.string.cancel, null);
        confirm.setPositiveButton(R.string.action_clear_scores, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                trophyRoom.reset();
                finish();
            }
        });
        confirm.show();
    }

    @Override
    public void onItemShareClick(@NonNull Achievement achievement) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.achievement_share_title));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(achievement.name));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)));
    }

    //endregion
}
