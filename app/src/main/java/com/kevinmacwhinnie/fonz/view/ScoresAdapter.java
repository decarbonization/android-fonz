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

package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Formatting;
import com.kevinmacwhinnie.fonz.data.ScoresStore;
import com.squareup.otto.Subscribe;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final Formatting formatting;
    private final ScoresStore scoresStore;
    private Cursor cursor;

    public ScoresAdapter(@NonNull Context context,
                         @NonNull ScoresStore scoresStore,
                         @NonNull Formatting formatting) {
        this.inflater = LayoutInflater.from(context);
        this.scoresStore = scoresStore;
        this.formatting = formatting;

        scoresStore.getBus().register(this);
        query();
    }

    public void query() {
        this.cursor = scoresStore.queryHighScores();
        notifyDataSetChanged();
    }

    @Subscribe public void onScoresStoreChanged(@NonNull ScoresStore.Changed event) {
        query();
    }

    public void destroy() {
        scoresStore.getBus().unregister(this);
        cursor.close();
    }

    @Override
    public int getItemCount() {
        if (cursor.isClosed()) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_score_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);

        final String name = cursor.getString(ScoresStore.COLUMN_NAME_INDEX);
        holder.name.setText(name);

        final int score = cursor.getInt(ScoresStore.COLUMN_SCORE_INDEX);
        holder.score.setText(formatting.formatScore(score));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView score;

        ViewHolder(@NonNull View view) {
            super(view);

            this.name = (TextView) view.findViewById(R.id.item_score_entry_name);
            this.score = (TextView) view.findViewById(R.id.item_score_entry_score);
        }
    }
}
