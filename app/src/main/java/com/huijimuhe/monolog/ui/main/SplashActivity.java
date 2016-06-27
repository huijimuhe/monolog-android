package com.huijimuhe.monolog.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.easemob.EMCallBack;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.EaseMobService;
import com.huijimuhe.monolog.domain.PrefManager;
import com.huijimuhe.monolog.ui.auth.SignInActivity;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.PlatformConfig;

public class SplashActivity extends AbstractActivity {
    private final String TAG = SplashActivity.class.getName();
    private final int sleepTime = 4 * 1000;//等4秒
    private ViewGroup container;
    private boolean mInited = false;
    private Object lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_splash);
        //初始化
        lock = new Object();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                initEnvir();
            }
        }).start();
        //显示广告
        setupSplashAd();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化环境
     */
    private void initEnvir() {
        //初始化SDK
        CrashReport.initCrashReport(getApplicationContext(), "[YOURS]", false);
        PrefManager.init(AppContext.getInstance());
        PlatformConfig.setWeixin("[YOURS]", "[YOURS]");
        //easeMob
        EaseMobService.getInstance().init(AppContext.getInstance());

        String token = AppContext.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
            Account account = PrefManager.getInstance().getUser();
            EaseMobService.getInstance().easeMobLogin(account, new EMCallBack() {
                @Override
                public void onSuccess() {
                    synchronized (lock) {
                        mInited = true;
                        lock.notify();
                    }
                }

                @Override
                public void onError(int i, String s) {
                    synchronized (lock) {
                        mInited = true;
                        lock.notify();
                    }
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

    /**
     * 设置开屏广告
     */

    private void setupSplashAd() {
        container = (ViewGroup) findViewById(R.id.splash_container);
        new SplashAD(this, container, "[YOURS]", "[YOURS]", new SplashADListener() {
            @Override
            public void onADDismissed() {
                next();
            }

            @Override
            public void onNoAD(int i) {
                next();
            }

            @Override
            public void onADPresent() {
            }

            @Override
            public void onADClicked() {
            }
        });
    }

    private void next() {
        synchronized (lock) {
            while (!mInited) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String token = PrefManager.getInstance().getToken();
            if (TextUtils.isEmpty(token)) {
                startActivity(SignInActivity.newIntent());
            } else {
                startActivity(MainActivity.newIntent());
            }
            finish();
        }
    }

    /**
     * 开屏页最好禁止用户对返回按钮的控制
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
