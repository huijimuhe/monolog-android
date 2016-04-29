package com.huijimuhe.monolog.ui.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.BaseClient;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.utils.ImageUtils;
import com.huijimuhe.monolog.utils.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.regex.Pattern;

public abstract class AbstractActivity extends AppCompatActivity {
    public static final int STATE_FIRST_INIT = 0;
    public static final int STATE_ROTATE = 1;
    public static final int STATE_RESUME = 2;
    protected int mActivityState = STATE_FIRST_INIT;
    protected SystemBarTintManager mTintManager;

    //umeng share
    protected UMSocialService mShareController = UMServiceFactory.getUMSocialService("com.umeng.share");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSocial();
        //小米的沉浸控制
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//        }
//        mTintManager = new SystemBarTintManager(this);
        if (savedInstanceState != null) {
            mActivityState = STATE_ROTATE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mShareController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    protected  void initSocial(){
        mShareController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
                SHARE_MEDIA.DOUBAN, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA,
                SHARE_MEDIA.TENCENT);
        //设置新浪SSO handler
        mShareController.getConfig().setSsoHandler(new SinaSsoHandler());
        String appId = AppContext.getInstance().getMetaData("WEIXIN_ID");
        String appSecret = AppContext.getInstance().getMetaData("WEIXIN_SECRET");
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
        wxHandler.addToSocialSDK();
        // 设置微信朋友分享内容
        WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
        weiXinShareContent.setShareContent(AppContext.getInstance().getString(R.string.string_share_content));
        weiXinShareContent.setTitle(AppContext.getInstance().getString(R.string.string_share_content));
        weiXinShareContent.setShareImage(new UMImage(this, ImageUtils.drawableToBitamp(this, R.drawable.ic_logo)));
        weiXinShareContent.setTargetUrl(BaseClient.URL_USER_ABOUT);
        mShareController.setShareMedia(weiXinShareContent);
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(AppContext.getInstance().getString(R.string.string_share_content));
        circleMedia.setTitle(AppContext.getInstance().getString(R.string.string_share_content));
        circleMedia.setShareImage(new UMImage(this, ImageUtils.drawableToBitamp(this, R.drawable.ic_logo)));
        circleMedia.setTargetUrl(BaseClient.URL_USER_ABOUT);
        // 设置分享内容
        mShareController.setShareMedia(circleMedia);
    }

    public void openShare() {
        mShareController.openShare(this, false);
    }

    protected void hiddenKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected boolean isNull(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    protected boolean isNullOrOverLength(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            if (text.length() > 120) {
                return true;
            }
            return false;
        }
        return true;
    }

    protected boolean matchPhone(String text) {
        if (Pattern.compile("(\\d{11})").matcher(text).matches()) {
            return true;
        }
        return false;
    }

    /**
     * 小米的沉浸式控制
     * @param on
     */
    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public int getmActivityState() {
        return mActivityState;
    }
}
