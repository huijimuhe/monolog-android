package com.huijimuhe.monolog.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.db.schema.StatueTable;

import java.util.ArrayList;

public class StatueDao {

    private static SQLiteDatabase getWsd() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getWritableDatabase();
    }

    private static SQLiteDatabase getRsd() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getReadableDatabase();
    }

    public static ArrayList<Statue> getList(String uid, String page) {
        ArrayList<Statue> statues = new ArrayList<>();
        String sql = "select * from " + StatueTable.TABLE_NAME + " where "
                + StatueTable.OWNERID + " = '"
                + uid + "' order by " + StatueTable.ID + " asc limit " + page;
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            Statue statue = new Statue();
            statue.setId(c.getString(c.getColumnIndex(StatueTable.SID)));
            statue.setText(c.getString(c.getColumnIndex(StatueTable.TEXT)));
            statue.setImg_path(c.getString(c.getColumnIndex(StatueTable.IMG_PATH)));
            statue.setCreated_at(c.getString(c.getColumnIndex(StatueTable.CREATED_AT)));
            statue.setLat(c.getDouble(c.getColumnIndex(StatueTable.LAT)));
            statue.setLng(c.getDouble(c.getColumnIndex(StatueTable.LNG)));
            statue.setMiss_count(c.getString(c.getColumnIndex(StatueTable.MISS_COUNT)));
            statue.setRight_count(c.getString(c.getColumnIndex(StatueTable.RIGHT_COUNT)));
            statues.add(statue);
        }
        return statues;
    }

    public static void asyncReplace(final ArrayList<Statue> list, final String ownerid) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StatueDao.deleteAll(ownerid);
                StatueDao.insertList(list, ownerid);
            }
        };

        new Thread(runnable).start();
    }

    public static void updateCount(Statue statue) {
        String sql = "select * from " + StatueTable.TABLE_NAME + " where "
                + StatueTable.SID + "  = '"
                + statue.getId() + "'";
        Cursor c = getRsd().rawQuery(sql, null);
        if (c.moveToNext()) {
            String[] args = {String.valueOf(statue.getId())};
            ContentValues cv = new ContentValues();
            cv.put(StatueTable.MISS_COUNT, statue.getMiss_count());
            cv.put(StatueTable.RIGHT_COUNT, statue.getRight_count());
            getWsd().update(StatueTable.TABLE_NAME, cv, StatueTable.SID + "=?",
                    args);
        }
    }

    public static void insertList(ArrayList<Statue> msgList, String uid) {

        if (msgList == null || msgList.size() == 0) {
            return;
        }
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(getWsd(),
                StatueTable.TABLE_NAME);
        final int textCol = ih.getColumnIndex(StatueTable.TEXT);
        final int img_pathCol = ih.getColumnIndex(StatueTable.IMG_PATH);
        final int create_atCol = ih.getColumnIndex(StatueTable.CREATED_AT);
        final int latCol = ih.getColumnIndex(StatueTable.LAT);
        final int lngCol = ih.getColumnIndex(StatueTable.LNG);
        final int miss_countCol = ih.getColumnIndex(StatueTable.MISS_COUNT);
        final int right_countCol = ih.getColumnIndex(StatueTable.RIGHT_COUNT);
        final int uidCol = ih.getColumnIndex(StatueTable.UID);
        final int sidCol = ih.getColumnIndex(StatueTable.SID);
        final int ownerIdCol = ih.getColumnIndex(StatueTable.OWNERID);
        try {
            getWsd().beginTransaction();
            for (int i = 0; i < msgList.size(); i++) {
                Statue msg = msgList.get(i);
                ih.prepareForInsert();
                ih.bind(textCol, msg.getText());
                ih.bind(img_pathCol, msg.getImg_path());
                ih.bind(create_atCol, msg.getCreated_at());
                ih.bind(latCol, msg.getLat());
                ih.bind(lngCol, msg.getLat());
                ih.bind(miss_countCol, msg.getMiss_count());
                ih.bind(right_countCol, msg.getRight_count());
                ih.bind(uidCol, msg.getUser().getId());
                ih.bind(sidCol, msg.getId());
                ih.bind(ownerIdCol, uid);
                ih.execute();
            }
            getWsd().setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            getWsd().endTransaction();
            ih.close();
        }
    }

    public static void deleteAll(String ownerid) {
        String sql = "delete from " + StatueTable.TABLE_NAME + " where " + StatueTable.OWNERID + " = '" + ownerid + "'";
        getWsd().execSQL(sql);
    }

}
