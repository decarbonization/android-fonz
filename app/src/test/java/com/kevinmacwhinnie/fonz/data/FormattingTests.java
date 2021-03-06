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

package com.kevinmacwhinnie.fonz.data;

import com.kevinmacwhinnie.fonz.FonzTestCase;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FormattingTests extends FonzTestCase {
    private final Formatting formatting = new Formatting(getContext());

    @Test
    public void formatLives() {
        assertThat(formatting.formatLives(0), is(equalTo("0")));
        assertThat(formatting.formatLives(10), is(equalTo("10")));
        assertThat(formatting.formatLives(100), is(equalTo("100")));
        assertThat(formatting.formatLives(1000), is(equalTo("1,000")));
        assertThat(formatting.formatLives(10000), is(equalTo("10,000")));
        assertThat(formatting.formatLives(100000), is(equalTo("100,000")));
        assertThat(formatting.formatLives(1000000), is(equalTo("1,000,000")));
    }

    @Test
    public void formatScore() {
        assertThat(formatting.formatScore(0), is(equalTo("0")));
        assertThat(formatting.formatScore(10), is(equalTo("10")));
        assertThat(formatting.formatScore(100), is(equalTo("100")));
        assertThat(formatting.formatScore(1000), is(equalTo("1,000")));
        assertThat(formatting.formatScore(10000), is(equalTo("10,000")));
        assertThat(formatting.formatScore(100000), is(equalTo("100,000")));
        assertThat(formatting.formatScore(1000000), is(equalTo("1,000,000")));
    }
}
