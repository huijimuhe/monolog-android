package com.huijimuhe.monolog.db;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    private static DatabaseManager singleton = null;

    private SQLiteDatabase wsd = null;
    private SQLiteDatabase rsd = null;
    private DatabaseHelper databaseHelper = null;

    private DatabaseManager() {

    }

    public synchronized static DatabaseManager getInstance() {
        if (singleton == null) {
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
            SQLiteDatabase wsd = databaseHelper.getWritableDatabase();
            SQLiteDatabase rsd = databaseHelper.getReadableDatabase();

            singleton = new DatabaseManager();
            singleton.wsd = wsd;
            singleton.rsd = rsd;
            singleton.databaseHelper = databaseHelper;
        }

        return singleton;
    }

    public static void close() {
        if (singleton != null) {
            singleton.databaseHelper.close();
        }
    }

}
