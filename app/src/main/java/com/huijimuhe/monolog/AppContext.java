package com.huijimuhe.monolog;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.PrefManager;
import com.tencent.bugly.crashreport.CrashReport;

public class AppContext extends Application {

    // singleton
    private static AppContext AppContext = null;


    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = this;
        //baidu
        BaiduService.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CrashReport.closeCrashReport();
    }

    public static AppContext getInstance() {
        return AppContext;
    }

    public String getToken() {
        return PrefManager.getInstance().getToken();
    }

    public Account getUser() {
        return PrefManager.getInstance().getUser();
    }

    public void loadImg(ImageView v, String url) {
        String absoluteUrl = url;
        Glide.with(this)
                .load(absoluteUrl)
                .dontAnimate()
                .placeholder(R.drawable.img_bg)
                .error(R.drawable.ic_action_picture)
                .into(v);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}