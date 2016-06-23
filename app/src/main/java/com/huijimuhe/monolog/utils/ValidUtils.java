package com.huijimuhe.monolog.utils;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by Huijimuhe on 2016/6/13.
 * This is a part of Monolog
 * enjoy
 */
public class ValidUtils {

    public static boolean isNull(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    public static boolean isNullOrOverLength(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            if (text.length() > 120) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean matchPhone(String text) {
        if (Pattern.compile("(\\d{11})").matcher(text).matches()) {
            return true;
        }
        return false;
    }

}
