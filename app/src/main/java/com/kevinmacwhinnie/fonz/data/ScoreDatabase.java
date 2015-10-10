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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class ScoreDatabase extends SQLiteOpenHelper {
    public static final String NAME = "ScoresDB";
    public static final int VERSION_0 = 0;

    public static final String TABLE_SCORES = "Scores";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SCORE = "Score";
    public static final String COLUMN_DATE = "Date";

    public ScoreDatabase(@NonNull Context context) {
        super(context, NAME, null, VERSION_0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create = "CREATE TABLE IF NOT EXISTS " + TABLE_SCORES + "("
                + COLUMN_NAME + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_DATE + " INTEGER"
                + ")";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public boolean insertScore(@NonNull String name, int score) {
        // TODO: limit this

        final SQLiteDatabase database = getWritableDatabase();
        final ContentValues values = new ContentValues(3);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_DATE, System.currentTimeMillis());
        return (database.insert(TABLE_SCORES, null, values) != -1);
    }

    public int clearScores() {
        final SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_SCORES, null, null);
    }

    public Cursor fetchScores() {
        final SQLiteDatabase database = getReadableDatabase();
        final String[] columns = { COLUMN_NAME, COLUMN_SCORE, COLUMN_DATE };
        return database.query(TABLE_SCORES, columns, null, null, null, null, COLUMN_DATE);
    }
}
