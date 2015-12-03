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

import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.achievements.Achievement;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AchievementsAdapterTests extends FonzTestCase implements AchievementsAdapter.OnShareClickListener {
    @Test
    public void achievementRendering() {
        final List<Achievement> achievements = new ArrayList<>();
        achievements.add(Achievement.ALL_POWER_UPS_UNLOCKED);
        final AchievementsAdapter adapter = new AchievementsAdapter(getContext(), this, achievements);

        final FrameLayout fakeParent = new FrameLayout(getContext());

        final AchievementsAdapter.ViewHolder holder1 =
                adapter.onCreateViewHolder(fakeParent,
                                           adapter.getItemViewType(0));
        assertThat(holder1, is(notNullValue()));

        adapter.bindViewHolder(holder1, 0);

        assertThat(holder1.name.getText().toString(), is(equalTo("Power mad!")));
        assertThat(holder1.description.getText().toString(),
                   is(equalTo("You've unlocked all the power ups at once in a single game.")));
    }

    @Override
    public void onItemShareClick(@NonNull Achievement achievement) {
        // Do nothing.
    }
}
