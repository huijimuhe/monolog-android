package com.huijimuhe.monolog.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class MetaUtils {

    /**
     * 获取META-INF的标签
     *
     * @param key
     * @return
     */
    public String getMetaData(String key, Context c) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = c.getPackageManager()
                    .getApplicationInfo(c.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo.metaData.getString(key);
    }
}
