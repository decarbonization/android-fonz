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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

public class InAppTrophyRoom implements TrophyRoom {
    static final String PREFERENCES_NAME = "trophy_room";

    private final Bus bus;
    private final SharedPreferences preferences;

    public InAppTrophyRoom(@NonNull Bus bus, @NonNull Context context) {
        this.bus = bus;
        this.preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    @NonNull
    @Override
    public List<Achievement> getUnlockedAchievements() {
        final List<Achievement> achievements = new ArrayList<>();
        for (final Achievement achievement : Achievement.values()) {
            if (isAchievementUnlocked(achievement)) {
                achievements.add(achievement);
            }
        }
        return achievements;
    }

    @Override
    public boolean isAchievementUnlocked(@NonNull Achievement achievement) {
        return preferences.getBoolean(achievement.name(), false);
    }

    @Override
    public void achievementUnlocked(@NonNull Achievement achievement) {
        final String name = achievement.name();
        if (!preferences.getBoolean(name, false)) {
            preferences.edit()
                       .putBoolean(name, true)
                       .apply();

            bus.post(new AchievementUnlocked(achievement));
        }
    }

    @Override
    public void reset() {
        preferences.edit()
                   .clear()
                   .apply();
    }
}
