package com.kevinmacwhinnie.fonz.state;

import java.util.Observable;

public class Life extends Observable {
    public static int INITIAL_COUNT = 3;

    private int count = INITIAL_COUNT;

    @Override
    public boolean hasChanged() {
        return true;
    }

    public int getCount() {
        return count;
    }

    public void death() {
        if (count > 0) {
            this.count--;
            notifyObservers();
        }
    }

    public void plusOne() {
        this.count++;
        notifyObservers();
    }

    public boolean isAlive() {
        return (count > 0);
    }

    public void reset() {
        this.count = INITIAL_COUNT;
        notifyObservers();
    }

    @Override
    public String toString() {
        return "Life{" +
                "count=" + count +
                '}';
    }
}
