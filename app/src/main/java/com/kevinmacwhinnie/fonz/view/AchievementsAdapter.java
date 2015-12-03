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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.achievements.Achievement;
import com.kevinmacwhinnie.fonz.view.util.Drawing;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {
    private final Resources resources;
    private final LayoutInflater inflater;
    private final OnShareClickListener onShareClickListener;
    private final List<Achievement> achievements;

    public AchievementsAdapter(@NonNull Context context,
                               @NonNull OnShareClickListener onShareClickListener,
                               @NonNull List<Achievement> achievements) {
        this.resources = context.getResources();
        this.inflater = LayoutInflater.from(context);
        this.onShareClickListener = onShareClickListener;
        this.achievements = achievements;
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Achievement achievement = achievements.get(position);
        final String name = resources.getString(achievement.name);
        holder.name.setText(name);
        holder.description.setText(achievement.description);
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        final TextView name;
        final TextView description;
        final ImageButton shareButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.item_achievement_name);
            this.description = (TextView) itemView.findViewById(R.id.item_achievement_description);
            this.shareButton = (ImageButton) itemView.findViewById(R.id.item_achievement_share);
            final Drawable shareIcon = DrawableCompat.wrap(shareButton.getDrawable().mutate());
            DrawableCompat.setTint(shareIcon, Drawing.getColor(resources, R.color.color_accent));
            shareButton.setImageDrawable(shareIcon);
            shareButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View sender) {
            final int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                final Achievement achievement = achievements.get(adapterPosition);
                onShareClickListener.onItemShareClick(achievement);
            }
        }
    }


    public interface OnShareClickListener {
        void onItemShareClick(@NonNull Achievement achievement);
    }
}
