package com.huijimuhe.monolog.utils;

import java.text.NumberFormat;

public class NumUtils {

    public static String converNumToString(String numberStr) {
        int thousandInt = 1000;
        int number = Integer.valueOf(numberStr);
        if (number > thousandInt) {
            if (number > thousandInt * 100) {
                return new java.text.DecimalFormat("#.00").format((number / thousandInt * 10)) + "万";
            } else {
                return new java.text.DecimalFormat("#.0").format((number / thousandInt)) + "k";
            }
        }
        return String.valueOf(number);
    }
}
