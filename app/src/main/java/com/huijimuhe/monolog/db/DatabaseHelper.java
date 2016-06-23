package com.huijimuhe.monolog.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.db.schema.DraftTable;
import com.huijimuhe.monolog.db.schema.GuessTable;
import com.huijimuhe.monolog.db.schema.StatueTable;
import com.huijimuhe.monolog.db.schema.ContactTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper singleton = null;

    private static final String DATABASE_NAME = "monolog.db";

    private static final int DATABASE_VERSION = 5;

    static final String CREATE_CONTACT_TABLE_SQL = "create table " + ContactTable.TABLE_NAME
            + "("
            + ContactTable.ID + " integer primary key autoincrement,"
            + ContactTable.OWNERID + " integer,"
            + ContactTable.NAME + " text,"
            + ContactTable.AVATAR + " text,"
            + ContactTable.GENDER + " text,"
            + ContactTable.UID + " text,"
            + ContactTable.MISS_COUNT + " integer DEFAULT  0,"
            + ContactTable.RIGHT_COUNT + " integer DEFAULT 0,"
            + ContactTable.STATUE_COUNT + " integer DEFAULT 0"
            + ");";

    static final String CREATE_STATUE_TABLE_SQL = "create table " + StatueTable.TABLE_NAME
            + "("
            + StatueTable.ID + " integer primary key autoincrement,"
            + StatueTable.OWNERID + " integer,"
            + StatueTable.SID + " integer,"
            + StatueTable.IMG_PATH + " text,"
            + StatueTable.TEXT + " text,"
            + StatueTable.ISBANNED + " text,"
            + StatueTable.CREATED_AT + " timestamp,"
            + StatueTable.LAT + " float,"
            + StatueTable.LNG + " float,"
            + StatueTable.RIGHT_COUNT + " integer default 0,"
            + StatueTable.MISS_COUNT + " integer default 0,"
            + StatueTable.UID + " integer"
            + ");";

    static final String CREATE_GUESS_TABLE_SQL = "create table " + GuessTable.TABLE_NAME
            + "("
            + GuessTable.ID + " integer primary key autoincrement,"
            + GuessTable.OWNERID + " integer,"
            + GuessTable.TYPE + " integer,"
            + GuessTable.SID + " integer,"
            + GuessTable.UID + " integer"
            + ");";

    static final String CREATE_DRAFT_TABLE_SQL = "create table " + DraftTable.TABLE_NAME
            + "("
            + DraftTable.ID + " integer primary key autoincrement,"
            + DraftTable.OWNERID + " integer,"
            + DraftTable.IMG_PATH + " text,"
            + DraftTable.TEXT + " text,"
            + DraftTable.ISBANNED + " text,"
            + DraftTable.CREATED_AT + " timestamp,"
            + DraftTable.LAT + " float,"
            + DraftTable.LNG + " float"
            + ");";
    
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static synchronized DatabaseHelper getInstance() {
        if (singleton == null) {
            singleton = new DatabaseHelper(AppContext.getInstance());
        }
        return singleton;
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT_TABLE_SQL);
        db.execSQL(CREATE_STATUE_TABLE_SQL);
        db.execSQL(CREATE_DRAFT_TABLE_SQL);
        db.execSQL(CREATE_GUESS_TABLE_SQL);
    }

    private void deleteAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StatueTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GuessTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DraftTable.TABLE_NAME);
    }

}
