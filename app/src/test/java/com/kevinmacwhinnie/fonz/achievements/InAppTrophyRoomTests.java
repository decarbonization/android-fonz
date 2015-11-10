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

package com.kevinmacwhinnie.fonz.achievements;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InAppTrophyRoomTests extends FonzTestCase {
    private final Bus bus = new Bus("InAppTrophyRoomTests#bus");
    private final InAppTrophyRoom trophyRoom = new InAppTrophyRoom(bus, getContext());
    private final List<BaseEvent> events = new ArrayList<>();

    @Before
    public void setUp() {
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);
        trophyRoom.reset();
        events.clear();
    }

    @Subscribe public void onAchievementUnlocked(@NonNull AchievementUnlocked event) {
        events.add(event);
    }

    @Test
    public void isAchievementUnlocked() {
        assertThat(trophyRoom.isAchievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE), is(false));
        trophyRoom.achievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE);
        assertThat(trophyRoom.isAchievementUnlocked(Achievement.FIRST_HIGH_SCORE), is(false));
        assertThat(trophyRoom.isAchievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE), is(true));
    }

    @Test
    public void getUnlockedAchievements() {
        trophyRoom.achievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE);
        trophyRoom.achievementUnlocked(Achievement.FIRST_HIGH_SCORE);
        trophyRoom.achievementUnlocked(Achievement.SCORE_OVER_1_000);

        assertThat(events.size(), is(equalTo(3)));
        assertThat(events, hasItem(new AchievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE)));
        assertThat(events, hasItem(new AchievementUnlocked(Achievement.FIRST_HIGH_SCORE)));
        assertThat(events, hasItem(new AchievementUnlocked(Achievement.SCORE_OVER_1_000)));
    }

    @Test
    public void achievementUnlocked() {
        trophyRoom.achievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE);
        assertThat(events.size(), is(equalTo(1)));
        assertThat(events, hasItem(new AchievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE)));

        trophyRoom.achievementUnlocked(Achievement.FIRST_SINGLE_COLOR_PIE);
        assertThat(events.size(), is(equalTo(1)));
    }
}
