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
