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

package com.kevinmacwhinnie.fonz.async;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;

import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TaskTests extends FonzTestCase {
    private final Executor executor = new ImmediateExecutor();


    @Test(expected = IllegalStateException.class)
    public void consistency() {
        new MeaningOfLifeTask().execute(executor);
    }

    @Test
    public void completed() {
        final AtomicBoolean reached = new AtomicBoolean(false);
        final MeaningOfLifeTask task = new MeaningOfLifeTask();
        task.withConsumer(new Task.Consumer<Integer>() {
            @Override
            public void onCompleted(Integer value) {
                assertThat(value, is(equalTo(42)));
                reached.set(true);
            }

            @Override
            public void onError(Throwable e) {
                fail(e.getMessage());
            }
        })
                .execute(executor);

        assertThat(reached.get(), is(true));
    }

    @Test
    public void error() {
        final AtomicBoolean reached = new AtomicBoolean(false);
        final ThrowingTask task = new ThrowingTask();
        task.withConsumer(new Task.Consumer<Void>() {
            @Override
            public void onCompleted(Void ignored) {
                fail();
            }

            @Override
            public void onError(Throwable e) {
                assertThat(e, is(instanceOf(Exception.class)));
                reached.set(true);
            }
        })
            .execute(executor);

        assertThat(reached.get(), is(true));
    }


    static class ThrowingTask extends Task<Void> {
        public ThrowingTask() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected Void onRun() throws Throwable {
            throw new Exception();
        }
    }

    static class MeaningOfLifeTask extends Task<Integer> {
        public MeaningOfLifeTask() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected Integer onRun() throws Throwable {
            return 42;
        }
    }

    static class ImmediateExecutor implements Executor {
        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    }
}
