package com.huijimuhe.monolog.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.domain.EaseMobService;
import com.huijimuhe.monolog.ui.auth.SignInActivity;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

public class SplashActivity extends AbstractActivity {
    private static final String TAG = "SplashActivity";
    private static final int sleepTime = 4 * 1000;//等4秒
    public boolean canJump = false;
    private ViewGroup container;
    private SplashAD splashAD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //显示广告
                runAd();
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /**
     * 跑广告
     */
    private void runAd() {
        setupSplashAd();
        //设置开屏
        initEnvir();
    }

    /**
     * 跑应用的逻辑
     * 延迟一下
     */
    @SuppressWarnings("unused")
    private void runApp() {
        long start = System.currentTimeMillis();
        initEnvir();
        long costTime = System.currentTimeMillis() - start;
        if (sleepTime - costTime > 0) {
            try {
                Thread.sleep(sleepTime - costTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String token = AppContext.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            startActivity(SignInActivity.newIntent());
        } else {
            EaseMobService.getInstance().easeMobLogin(new Handler());
            startActivity(MainActivity.newIntent());
        }
        finish();
    }

    /**
     * 初始化环境
     */
    private void initEnvir() {
        //初始化SDK
        AppContext.getInstance().initEnvir();
        canJump = true;
    }

    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        container = (ViewGroup) this.findViewById(R.id.splash_container);
        //创建开屏广告，广告拉取成功后会自动展示在container中。Container会首先被清空
        // "1104878885", "7040915046057449",
        splashAD = new SplashAD(this, container, "1104878885", "7040915046057449", new SplashADListener() {
            @Override
            public void onADDismissed() {
                Log.d(TAG,"dismiss");
                next();
            }

            @Override
            public void onNoAD(int i) {
                Log.d(TAG,"onNoAD");
                next();
//                String token = AppContext.getInstance().getToken();
//                if (TextUtils.isEmpty(token)) {
//                    startActivity(SignInActivity.newIntent());
//                } else {
//                    startActivity(MainActivity.newIntent());
//                }
//                finish();
            }

            @Override
            public void onADPresent() {

            }

            @Override
            public void onADClicked() {

            }
        },sleepTime);
    }

    private void next() {
        if (canJump) {
            String token = AppContext.getInstance().getToken();
            if (TextUtils.isEmpty(token)) {
                startActivity(SignInActivity.newIntent());
            } else {
                startActivity(MainActivity.newIntent());
            }
            finish();
        } else {
            canJump = true;
        }
    }

    /** 开屏页最好禁止用户对返回按钮的控制 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
