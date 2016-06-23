package com.huijimuhe.monolog.data.account.source;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.easemob.easeui.domain.EaseUser;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.account.AuthResponse;
import com.huijimuhe.monolog.db.DatabaseHelper;
import com.huijimuhe.monolog.db.schema.ContactTable;
import com.huijimuhe.monolog.domain.PrefManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class AccountLocalDataSource {

    private static AccountLocalDataSource INSTANCE;

    private SQLiteDatabase getWsd() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getWritableDatabase();
    }

    private SQLiteDatabase getRsd() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getReadableDatabase();
    }

    private AccountLocalDataSource() {
    }

    public static AccountLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AccountLocalDataSource();
        }
        return INSTANCE;
    }

    public void saveAccount(@NonNull AuthResponse response) {
        PrefManager.getInstance().setToken(response.getToken());
        PrefManager.getInstance().setUser(response.getUser());
    }

    public void getAccount() {

    }

    public void addContact(Account msg, String ownerid) {
        String sql = "select * from " + ContactTable.TABLE_NAME
                + " where " + ContactTable.UID + " = '" + msg.getId()
                + "' and " + ContactTable.OWNERID + "='" + ownerid + "'";
        Cursor c = getRsd().rawQuery(sql, null);
        if (c.getCount() == 0) {
            insert(msg, ownerid);
        }
    }

    public ArrayList<Account> getContacts(String ownerId, String page, @NonNull List<Account> accounts) {
        ArrayList<Account> list = new ArrayList<>();
        String sql = "select * from " + ContactTable.TABLE_NAME + " where "
                + ContactTable.OWNERID + "  = '"
                + ownerId + "' order by " + ContactTable.ID + " asc limit " + page;
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            Account user = new Account();
            user.setName(c.getString(c.getColumnIndex(ContactTable.NAME)));
            user.setId(c.getString(c.getColumnIndex(ContactTable.UID)));
            user.setAvatar(c.getString(c.getColumnIndex(ContactTable.AVATAR)));
            user.setGender(c.getString(c.getColumnIndex(ContactTable.GENDER)));
            list.add(user);
        }
        c.close();
        return list;
    }

    public EaseUser getEaseMobUser(String username) {
        EaseUser user = new EaseUser(username);
        String sql = "select * from " + ContactTable.TABLE_NAME + " where " + ContactTable.UID + " = '" + username + "'";
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            user.setAvatar(c.getString(c.getColumnIndex(ContactTable.AVATAR)));
            user.setNick(c.getString(c.getColumnIndex(ContactTable.NAME)));
            user.setUsername(c.getString(c.getColumnIndex(ContactTable.ID)));
        }
        c.close();
        return user;
    }

    public void insert(Account msg, String ownerid) {

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

    public void insertList(List<Account> list, String ownerid) {

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
                Account msg = list.get(i);
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

    public void delete(String uid, String ownerId) {
        String sql = "delete from " + ContactTable.TABLE_NAME
                + " where " + ContactTable.UID + " ='" + uid
                + "' and " + ContactTable.OWNERID + "='" + ownerId + "'";
        getWsd().execSQL(sql);
    }

    public void deleteAll(String ownerId) {
        String sql = "delete from " + ContactTable.TABLE_NAME
                + " where " + ContactTable.OWNERID + " ='" + ownerId + "'";
        getWsd().execSQL(sql);
    }

}
