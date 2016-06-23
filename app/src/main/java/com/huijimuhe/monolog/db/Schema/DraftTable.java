package com.huijimuhe.monolog.db.schema;


public class DraftTable extends BaseSchema {
    public static final String TABLE_NAME =PREFIX+ "drafts";

    public static final String IMG_PATH = "img_path";
    public static final String TEXT = "text";
    public static final String ISBANNED = "isbanned";
    public static final String CREATED_AT = "created_at";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    public static final String TYPE = "type";

    public static final int TYPE_STATUE= 1;
    public static final int TYPE_COMMENT = 2;
}
