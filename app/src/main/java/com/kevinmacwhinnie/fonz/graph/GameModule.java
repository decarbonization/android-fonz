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

package com.kevinmacwhinnie.fonz.graph;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.MainActivity;
import com.kevinmacwhinnie.fonz.ScoresActivity;
import com.kevinmacwhinnie.fonz.data.Scores;
import com.kevinmacwhinnie.fonz.game.Game;
import com.kevinmacwhinnie.fonz.game.Sounds;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false,
        injects = {
                MainActivity.class,
                ScoresActivity.class,
        })
public class GameModule {
    private final Context context;

    public GameModule(@NonNull Context context) {
        this.context = context;
    }

    @Singleton @Provides Bus provideBus() {
        return new Bus(ThreadEnforcer.MAIN, "Fonz Bus");
    }

    @Singleton @Provides Game provideGame(@NonNull Bus bus) {
        return new Game(bus);
    }

    @Singleton @Provides Scores provideScores(@NonNull Bus bus) {
        return new Scores(context, bus);
    }

    @Singleton @Provides Sounds provideSounds() {
        return new Sounds(context);
    }
}
