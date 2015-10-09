package com.kevinmacwhinnie.fonz.state;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScoreTests {
    @Test
    public void addDifferentColors() {
        final Score score = new Score();

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(false);
        assertThat(score.getValue(), is(equalTo(30)));

        score.addPie( false);
        assertThat(score.getValue(), is(equalTo(60)));
    }

    @Test
    public void addSameColor() {
        final Score score = new Score();

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(true);
        assertThat(score.getValue(), is(equalTo(60)));

        score.addPie(false);
        assertThat(score.getValue(), is(equalTo(90)));
    }

    @Test
    public void multiplier() {
        final Score score = new Score();

        assertThat(score.getValue(), is(equalTo(0)));

        score.setMultiplier(3f);
        score.addPie(false);
        assertThat(score.getValue(), is(equalTo(90)));

        score.addPie(true);
        assertThat(score.getValue(), is(equalTo(270)));
    }

    @Test
    public void reset() {
        final Score score = new Score();

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(false);
        assertThat(score.getValue(), is(equalTo(30)));

        score.reset();
        assertThat(score.getValue(), is(equalTo(0)));
    }
}
