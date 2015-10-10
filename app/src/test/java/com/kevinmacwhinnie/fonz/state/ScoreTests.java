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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScoreTests extends FonzTestCase {
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

    @Subscribe public void onScoreChanged(@NonNull Score.Changed event) {
        events.add(event);
    }


    @Test
    public void addDifferentColors() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(30)));
        assertThat(score.getValue(), is(equalTo(30)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(60)));
        assertThat(score.getValue(), is(equalTo(60)));
    }

    @Test
    public void addSameColor() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(true);
        assertThat(events, hasItem(new Score.Changed(60)));
        assertThat(score.getValue(), is(equalTo(60)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(90)));
        assertThat(score.getValue(), is(equalTo(90)));
    }

    @Test
    public void multiplier() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.setMultiplier(3f);
        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(90)));
        assertThat(score.getValue(), is(equalTo(90)));

        score.addPie(true);
        assertThat(events, hasItem(new Score.Changed(270)));
        assertThat(score.getValue(), is(equalTo(270)));
    }

    @Test
    public void reset() {
        final Score score = new Score(bus);

        assertThat(score.getValue(), is(equalTo(0)));

        score.addPie(false);
        assertThat(events, hasItem(new Score.Changed(30)));
        assertThat(score.getValue(), is(equalTo(30)));

        score.reset();
        assertThat(events, hasItem(new Score.Changed(0)));
        assertThat(score.getValue(), is(equalTo(0)));
    }
}
