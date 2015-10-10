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
