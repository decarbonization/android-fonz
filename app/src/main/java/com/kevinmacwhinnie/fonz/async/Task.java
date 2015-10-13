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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;

public abstract class Task<T> implements Runnable {
    private final Handler callbackHandler;
    private @Nullable Consumer<T> consumer;

    protected Task(@NonNull Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void run() {
        if (consumer == null) {
            throw new IllegalStateException("Consumer required before running a Task");
        }

        try {
            final T result = onRun();
            callbackHandler.post(new Runnable() {
                @Override
                public void run() {
                    consumer.onCompleted(result);
                }
            });
        } catch (final Throwable e) {
            callbackHandler.post(new Runnable() {
                @Override
                public void run() {
                    consumer.onError(e);
                }
            });
        }
    }

    protected abstract T onRun() throws Throwable;

    public Task<T> withConsumer(@NonNull Consumer<T> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void execute(@NonNull Executor executor) {
        executor.execute(this);
    }

    public interface Consumer<T> {
        void onCompleted(T value);
        void onError(Throwable e);
    }
}
