package com.huijimuhe.monolog.core;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.EaseMobService;
import com.huijimuhe.monolog.domain.PrefService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AppContext extends Application {

    // singleton
    private static AppContext AppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static AppContext getInstance() {
        return AppContext;
    }

    public void restartApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public String getToken() {
        return PrefService.getInstance(this).getToken();
    }

    public UserBean getUser() {
        return PrefService.getInstance(this).getUser();
    }

    public void loadImg(ImageView v, String url) {
        String absoluteUrl = url;
        try {
            Picasso.with(this)
                    .load(absoluteUrl)
                    .placeholder(R.drawable.img_bg)
                    .error(R.drawable.ic_action_picture)
                    .transform(new CropSquareTransformation())
                    .into(v);
        } catch (Exception ex) {
            Picasso.with(this)
                    .load(R.drawable.img_bg)
                    .transform(new CropSquareTransformation())
                    .into(v);
        }
    }

    public void loadImg(ImageView v, String url, int width) {
        String absoluteUrl = url;
        try {
            Picasso.with(this)
                    .load(absoluteUrl)
                    .placeholder(R.drawable.img_bg)
                    .error(R.drawable.ic_action_picture)
                    .transform(new CropSquareTransformation())
                    .resize(width, width)
                    .into(v);
        } catch (Exception ex) {
            Picasso.with(this)
                    .load(R.drawable.img_bg)
                    .transform(new CropSquareTransformation())
                    .into(v);
        }
    }

    public class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "monolog";
        }
    }

    public void initEnvir() {

        //easeMob
        EaseMobService.getInstance().init(this);
        //baidu
        BaiduService.getInstance().init(this);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取META-INF的标签
     * @param key
     * @return
     */
    public String getMetaData(String key) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo.metaData.getString(key);
    }
}