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

import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.data.ScoresStore;
import com.squareup.otto.Bus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ScoresAdapterTests extends FonzTestCase {
    private final Bus bus = new Bus("ScoresAdapterTests#bus");
    private final ScoresStore scoresStore = new ScoresStore(getContext(), bus);
    private ScoresAdapter2 adapter;

    @Before
    public void setUp() {
        assertThat(scoresStore.insert("Dina", 9000), is(true));

        this.adapter = new ScoresAdapter2(getContext(), scoresStore);
    }

    @After
    public void tearDown() {
        adapter.destroy();
        scoresStore.clear();
    }


    @Test
    public void changes() {
        final AtomicBoolean changed = new AtomicBoolean(false);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                changed.set(true);
            }
        });

        scoresStore.clear();

        assertThat(changed.get(), is(true));
    }

    @Test
    public void entryRendering() {
        final FrameLayout fakeParent = new FrameLayout(getContext());

        final ScoresAdapter2.ViewHolder holder1 =
                adapter.onCreateViewHolder(fakeParent,
                                           adapter.getItemViewType(0));
        assertThat(holder1, is(notNullValue()));

        adapter.bindViewHolder(holder1, 0);

        assertThat(holder1.name.getText().toString(), is(equalTo("Dina")));
        assertThat(holder1.score.getText().toString(), is(equalTo("9,000")));


        final ScoresAdapter2.ViewHolder holder2 =
                adapter.onCreateViewHolder(fakeParent,
                                           adapter.getItemViewType(1));
        assertThat(holder2, is(notNullValue()));

        adapter.bindViewHolder(holder2, 1);

        assertThat(holder2.name.getText().toString(), is(equalTo("--")));
        assertThat(holder2.score.getText().toString(), is(equalTo("0")));
    }
}
