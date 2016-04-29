package com.huijimuhe.monolog.ui.base;

public interface OnBaiduLBSListener {
    void onComplete(int code, double lng, double lat, String addr);
    void onStart();
    void onFailed(int code);
}
