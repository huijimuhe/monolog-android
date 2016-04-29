package com.huijimuhe.monolog.db;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.easemob.easeui.domain.EaseUser;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.db.Schema.ContactTable;

import java.util.ArrayList;

public class ContactDao {

    private static SQLiteDatabase getWsd() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getWritableDatabase();
    }

    private static SQLiteDatabase getRsd() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getReadableDatabase();
    }

    public static ArrayList<UserBean> getList(String ownerId, String page) {
        ArrayList<UserBean> list = new ArrayList<>();
        String sql = "select * from " + ContactTable.TABLE_NAME + " where "
                + ContactTable.OWNERID + "  = '"
                + ownerId + "' order by " + ContactTable.ID + " asc limit " + page;
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            UserBean user = new UserBean();
            user.setName(c.getString(c.getColumnIndex(ContactTable.NAME)));
            user.setId(c.getString(c.getColumnIndex(ContactTable.UID)));
            user.setAvatar(c.getString(c.getColumnIndex(ContactTable.AVATAR)));
            user.setGender(c.getString(c.getColumnIndex(ContactTable.GENDER)));
            list.add(user);
        }
        return list;
    }

    public static ArrayList<EaseUser> getContacts(String ownerId) {
        ArrayList<EaseUser> list = new ArrayList<>();
        String sql = "select * from " + ContactTable.TABLE_NAME + " where "
                + ContactTable.OWNERID + "  = '"
                + ownerId+"'";
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(ContactTable.NAME));
            String id = c.getString(c.getColumnIndex(ContactTable.UID));
            String avatar = c.getString(c.getColumnIndex(ContactTable.AVATAR));
            EaseUser u = new EaseUser(id);
            u.setAvatar(avatar);
            u.setNick(name);
            list.add(u);
        }
        return list;
    }

    public static EaseUser getEaseMobUser(String username) {
        EaseUser user = new EaseUser(username);
        String id=username;
        String sql = "select * from " + ContactTable.TABLE_NAME + " where " + ContactTable.UID + " = '" + id+"'";
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            user.setAvatar(c.getString(c.getColumnIndex(ContactTable.AVATAR)));
            user.setNick(c.getString(c.getColumnIndex(ContactTable.NAME)));
            user.setUsername(c.getString(c.getColumnIndex(ContactTable.ID)));
        }
        return user;
    }

    public static void asyncReplaceAll(final ArrayList<UserBean> list, final String ownerid) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                ContactDao.deleteAll(ownerid);
                ContactDao.insertList(list, ownerid);
            }
        }).start();
    }

    public static void asyncReplace(final UserBean user, final String ownerid) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                ContactDao.delete(user.getId(),ownerid);
                ContactDao.insert(user, ownerid);
            }
        }).start();
    }

    public static void addContact(UserBean msg, String ownerid) {
        String sql = "select * from " + ContactTable.TABLE_NAME
                + " where " + ContactTable.UID + " = '" + msg.getId()
                + "' and " + ContactTable.OWNERID + "='" + ownerid+"'";
        Cursor c = getRsd().rawQuery(sql, null);
        if (c.getCount() == 0) {
            insert(msg, ownerid);
        }
    }

    public static void insert(UserBean msg, String ownerid) {

        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(getWsd(),
                ContactTable.TABLE_NAME);
        final int nameCol = ih.getColumnIndex(ContactTable.NAME);
        final int avatarCol = ih.getColumnIndex(ContactTable.AVATAR);
        final int uidCol = ih.getColumnIndex(ContactTable.UID);
        final int genderCol = ih.getColumnIndex(ContactTable.GENDER);
        final int ownerCol = ih.getColumnIndex(ContactTable.OWNERID);
        final int rightCountCol = ih.getColumnIndex(ContactTable.RIGHT_COUNT);
        final int missCountCol = ih.getColumnIndex(ContactTable.MISS_COUNT);
        final int statueCountCol = ih.getColumnIndex(ContactTable.STATUE_COUNT);
        try {
            getWsd().beginTransaction();
            ih.prepareForInsert();
            ih.bind(nameCol, msg.getName());
            ih.bind(avatarCol, msg.getAvatar());
            ih.bind(uidCol, msg.getId());
            ih.bind(genderCol, msg.getGender());
            ih.bind(rightCountCol, msg.getRight_count());
            ih.bind(missCountCol, msg.getMiss_count());
            ih.bind(statueCountCol, msg.getStatue_count());
            ih.bind(ownerCol, ownerid);

            ih.execute();
            getWsd().setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            getWsd().endTransaction();
            ih.close();
        }
    }

    public static void insertList(ArrayList<UserBean> list, String ownerid) {

        if (list == null || list.size() == 0) {
            return;
        }
        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(getWsd(),
                ContactTable.TABLE_NAME);
        final int nameCol = ih.getColumnIndex(ContactTable.NAME);
        final int avatarCol = ih.getColumnIndex(ContactTable.AVATAR);
        final int uidCol = ih.getColumnIndex(ContactTable.UID);
        final int genderCol = ih.getColumnIndex(ContactTable.GENDER);
        final int ownerCol = ih.getColumnIndex(ContactTable.OWNERID);
        final int rightCountCol = ih.getColumnIndex(ContactTable.RIGHT_COUNT);
        final int missCountCol = ih.getColumnIndex(ContactTable.MISS_COUNT);
        final int statueCountCol = ih.getColumnIndex(ContactTable.STATUE_COUNT);
        try {
            getWsd().beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                UserBean msg = list.get(i);
                ih.prepareForInsert();
                ih.bind(nameCol, msg.getName());
                ih.bind(avatarCol, msg.getAvatar());
                ih.bind(uidCol, msg.getId());
                ih.bind(genderCol, msg.getGender());
                ih.bind(rightCountCol, msg.getRight_count());
                ih.bind(missCountCol, msg.getMiss_count());
                ih.bind(statueCountCol, msg.getStatue_count());
                ih.bind(ownerCol, ownerid);
                ih.execute();
            }
            getWsd().setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            getWsd().endTransaction();
            ih.close();
        }
    }
    public static void delete(String uid,String ownerId) {
        String sql = "delete from " + ContactTable.TABLE_NAME
                + " where " + ContactTable.UID + " ='" + uid
                + "' and " + ContactTable.OWNERID + "='" + ownerId+"'";
        getWsd().execSQL(sql);
    }

    public static void deleteAll(String ownerId) {
        String sql = "delete from " + ContactTable.TABLE_NAME
                + " where " + ContactTable.OWNERID + " ='" + ownerId+"'";
        getWsd().execSQL(sql);
    }
}
