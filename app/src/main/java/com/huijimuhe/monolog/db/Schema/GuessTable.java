package com.huijimuhe.monolog.db.Schema;


public class GuessTable extends BaseSchema {
    public static final String TABLE_NAME =PREFIX+ "guesses";
    public static final String UID = "user_id";
    public static final String SID = "statue_id";

    public static final String TYPE = "type";

    public static final int TYPE_RIGHT= 1;
    public static final int TYPE_WRONG = 0;
}
