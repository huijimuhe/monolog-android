package com.huijimuhe.monolog.domain;

public interface OnBaiduLBSListener {
    void onComplete(int code, double lng, double lat, String addr);
    void onStart();
    void onFailed(int code);
}
