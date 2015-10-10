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
package com.kevinmacwhinnie.fonz.state;

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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LifeTests extends FonzTestCase {
    private final Bus bus = new Bus("test-bus");
    private final List<BaseEvent> events = new ArrayList<>();

    @Before
    public void setUp() {
        bus.register(this);
    }

    @After
    public void tearDown() {
        bus.unregister(this);
        events.clear();
    }

    @Subscribe public void onLifeChanged(@NonNull Life.Changed event) {
        events.add(event);
    }


    @Test
    public void isAlive() {
        final Life life = new Life(bus);
        assertThat(life.isAlive(), is(true));

        life.decrement();
        assertThat(events, hasItem(new Life.Changed(4)));
        life.decrement();
        assertThat(events, hasItem(new Life.Changed(3)));
        life.decrement();
        assertThat(events, hasItem(new Life.Changed(2)));
        life.decrement();
        assertThat(events, hasItem(new Life.Changed(1)));
        life.decrement();
        assertThat(events, hasItem(new Life.Changed(0)));

        assertThat(life.isAlive(), is(false));

        events.clear();
        life.increment();
        assertThat(events, hasItem(new Life.Changed(1)));

        assertThat(life.isAlive(), is(true));
    }

    @Test
    public void reset() {
        final Life life = new Life(bus);

        life.decrement();
        life.decrement();
        life.decrement();
        life.decrement();
        life.decrement();

        assertThat(life.isAlive(), is(false));

        events.clear();
        life.reset();
        assertThat(events, hasItem(new Life.Changed(5)));

        assertThat(life.isAlive(), is(true));
    }
}
