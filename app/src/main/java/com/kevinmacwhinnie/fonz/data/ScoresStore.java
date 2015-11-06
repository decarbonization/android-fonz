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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.kevinmacwhinnie.fonz.events.BaseEvent;
import com.squareup.otto.Bus;

public class ScoresStore extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "HighScoresDB";
    public static final int VERSION = 1;

    public static final int SCORE_LIMIT = 12;
    public static final String TABLE_SCORES = "Scores";

    //region Columns

    public static final String COLUMN_ID = "_id";
    public static final int COLUMN_ID_INDEX = 0;

    public static final String COLUMN_NAME = "name";
    public static final int COLUMN_NAME_INDEX = 1;

    public static final String COLUMN_SCORE = "score";
    public static final int COLUMN_SCORE_INDEX = 2;

    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final int COLUMN_TIMESTAMP_INDEX = 3;

    public static final String NAME_DEFAULT = "--";
    public static final int SCORE_DEFAULT = 0;

    //endregion


    private final Bus bus;


    //region Lifecycle

    public ScoresStore(@NonNull Context context, @NonNull Bus bus) {
        super(context, DATABASE_NAME, null, VERSION);

        this.bus = bus;
    }

    public Bus getBus() {
        return bus;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String createTables =
                ("CREATE TABLE IF NOT EXISTS " + TABLE_SCORES + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_SCORE + " INTEGER, " +
                        COLUMN_TIMESTAMP + " INTEGER" +
                 ")");
        database.execSQL(createTables);

        insertDefaults(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    private void insertDefaults(@NonNull SQLiteDatabase database) {
        final long now = System.currentTimeMillis();
        for (int i = 0; i < SCORE_LIMIT; i++) {
            final ContentValues values = new ContentValues(4);
            values.put(COLUMN_NAME, NAME_DEFAULT);
            values.put(COLUMN_SCORE, SCORE_DEFAULT);
            values.put(COLUMN_TIMESTAMP, now);
            database.insert(TABLE_SCORES, null, values);
        }
    }

    @VisibleForTesting
    void removeOutdatedEntries() {
        final SQLiteDatabase database = getWritableDatabase();
        final String delete =
                ("DELETE FROM " + TABLE_SCORES + " WHERE " + COLUMN_ID + " NOT IN " +
                 "(SELECT " + COLUMN_ID + " FROM " + TABLE_SCORES + " ORDER BY " +
                  COLUMN_ID + " DESC LIMIT " + SCORE_LIMIT + ")");
        database.execSQL(delete);
    }

    //endregion


    public boolean isNewHighScore(int newScore) {
        final SQLiteDatabase database = getReadableDatabase();
        final String[] selectArguments = { Integer.toString(newScore, 10) };
        final long count = DatabaseUtils.queryNumEntries(database,
                                                         TABLE_SCORES,
                                                         COLUMN_SCORE + " < ?",
                                                         selectArguments);
        return (count > 0);
    }

    public boolean insert(@NonNull String name, int score) {
        final SQLiteDatabase database = getWritableDatabase();
        final ContentValues values = new ContentValues(3);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        if (database.insert(TABLE_SCORES, null, values) != -1) {
            removeOutdatedEntries();
            bus.post(Changed.INSTANCE);
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_SCORES, null, null);
        insertDefaults(database);

        bus.post(Changed.INSTANCE);
    }

    public Cursor queryHighScores() {
        final SQLiteDatabase database = getReadableDatabase();
        final String[] columns = { COLUMN_ID, COLUMN_NAME, COLUMN_SCORE, COLUMN_TIMESTAMP };
        final String selection = null;
        final String orderBy = COLUMN_SCORE  + " DESC";
        return database.query(TABLE_SCORES,
                              columns,
                              selection,
                              null, null, null,
                              orderBy,
                              Integer.toString(SCORE_LIMIT, 10));
    }


    public static final class Changed extends BaseEvent {
        static final Changed INSTANCE = new Changed();
    }
}
