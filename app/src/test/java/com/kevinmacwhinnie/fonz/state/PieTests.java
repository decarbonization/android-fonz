package com.kevinmacwhinnie.fonz.state;

import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.FonzTestCase;
import com.kevinmacwhinnie.fonz.data.Piece;
import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.kevinmacwhinnie.fonz.Testing.occurrencesOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PieTests extends FonzTestCase {
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

    @Subscribe public void onPieChanged(@NonNull Pie.Changed event) {
        events.add(event);
    }


    @Test
    public void constructor() {
        final Pie pie = new Pie(bus);
        for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
            assertThat(pie.getPiece(i), is(equalTo(Piece.EMPTY)));
        }
    }

    @Test
    public void tryPlacePiece() {
        final Pie pie = new Pie(bus);

        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.GREEN), is(false));

        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE), is(true));
        assertThat(pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE), is(false));

        assertThat(occurrencesOf(events, Pie.Changed.INSTANCE), is(equalTo(2)));
    }

    @Test
    public void isFull() {
        final Pie pie = new Pie(bus);

        assertThat(pie.isFull(), is(false));

        pie.tryPlacePiece(0, Piece.ORANGE);
        assertThat(pie.isFull(), is(false));

        for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
            pie.tryPlacePiece(i, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));
    }

    @Test
    public void reset() {
        final Pie pie = new Pie(bus);

        assertThat(pie.isFull(), is(false));

        for (int i = 0; i < Pie.NUMBER_SLOTS; i++) {
            pie.tryPlacePiece(i, Piece.ORANGE);
        }

        assertThat(pie.isFull(), is(true));

        pie.reset();

        assertThat(pie.isFull(), is(false));

        assertThat(occurrencesOf(events, Pie.Changed.INSTANCE), is(equalTo(7)));
    }

    @Test
    public void isSingleColor() {
        final Pie pie = new Pie(bus);

        assertThat(pie.isSingleColor(), is(false));

        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.PURPLE);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.PURPLE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.GREEN);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.PURPLE);

        assertThat(pie.isSingleColor(), is(false));

        pie.reset();

        pie.tryPlacePiece(Pie.SLOT_TOP_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_TOP_RIGHT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_LEFT, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_CENTER, Piece.ORANGE);
        pie.tryPlacePiece(Pie.SLOT_BOTTOM_RIGHT, Piece.ORANGE);

        assertThat(pie.isSingleColor(), is(true));
    }
}
