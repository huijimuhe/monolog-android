package com.huijimuhe.monolog.data.statue.source;

import android.database.sqlite.SQLiteDatabase;

import com.huijimuhe.monolog.db.DatabaseHelper;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class StatueLocalDataSource {
    private static StatueLocalDataSource INSTANCE;

    private SQLiteDatabase getWsd() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getWritableDatabase();
    }

    private SQLiteDatabase getRsd() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getReadableDatabase();
    }

    private StatueLocalDataSource() {
    }

    public static StatueLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatueLocalDataSource();
        }
        return INSTANCE;
    }
}
