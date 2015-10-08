package com.kevinmacwhinnie.fonz.state;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LifeTests {
    @Test
    public void isAlive() {
        final Life life = new Life();
        assertThat(life.isAlive(), is(true));

        life.death();
        life.death();
        life.death();

        assertThat(life.isAlive(), is(false));

        life.plusOne();

        assertThat(life.isAlive(), is(true));
    }
}
