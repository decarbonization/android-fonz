package com.kevinmacwhinnie.fonz.state;

public class Life {
    public static int INITIAL_COUNT = 3;

    private int count = INITIAL_COUNT;

    public void death() {
        if (count > 0) {
            this.count--;
        }
    }

    public void plusOne() {
        this.count++;
    }

    public boolean isAlive() {
        return (count > 0);
    }

    @Override
    public String toString() {
        return "Life{" +
                "count=" + count +
                '}';
    }
}
