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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.data.Scores;
import com.squareup.otto.Subscribe;

import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.EntryViewHolder> {
    private final LayoutInflater inflater;
    private final NumberFormat numberFormat;
    private final Scores scores;

    public ScoresAdapter(@NonNull Context context, @NonNull Scores scores) {
        this.inflater = LayoutInflater.from(context);
        this.numberFormat = NumberFormat.getNumberInstance();
        this.scores = scores;
    }


    //region Data

    @Override
    public int getItemCount() {
        return Scores.NUMBER_SCORES;
    }

    public Scores.Entry getItem(int position) {
        return scores.getEntry(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        scores.bus.register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        scores.bus.unregister(this);
    }

    @Subscribe public void onScoresCleared(@NonNull Scores.Cleared event) {
        notifyItemRangeChanged(0, getItemCount());
    }

    @Subscribe public void onScoreTracked(@NonNull Scores.TrackedAtPosition event) {
        notifyItemRangeChanged(event.value, getItemCount() - event.value);
    }

    //endregion


    //region Binding

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_score_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        final Scores.Entry entry = getItem(position);
        holder.name.setText(entry.name);
        holder.score.setText(numberFormat.format(entry.score));
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_score_entry_name) TextView name;
        @Bind(R.id.item_score_entry_score) TextView score;

        EntryViewHolder(@NonNull View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    //endregion
}
