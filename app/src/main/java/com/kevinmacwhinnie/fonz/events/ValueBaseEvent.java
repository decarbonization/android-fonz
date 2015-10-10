package com.kevinmacwhinnie.fonz.events;

public abstract class ValueBaseEvent<T> extends BaseEvent {
    public final T value;

    public ValueBaseEvent(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueBaseEvent<?> that = (ValueBaseEvent<?>) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value=" + value +
                '}';
    }
}
