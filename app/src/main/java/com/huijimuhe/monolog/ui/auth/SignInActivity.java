package com.huijimuhe.monolog.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.AuthApi;
import com.huijimuhe.monolog.api.BaseClient;
import com.huijimuhe.monolog.bean.AuthResponseBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.domain.EaseMobService;
import com.huijimuhe.monolog.ui.main.GuideActivity;
import com.huijimuhe.monolog.ui.main.WebActivity;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.ui.main.MainActivity;
import com.huijimuhe.monolog.domain.PrefService;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.util.Map;

public class SignInActivity extends AbstractActivity implements View.OnClickListener {
    public static final int AUTH_SUCCESS = 0;
    public static final int AUTH_FAILED = 1;
    public static final int OPEN_WEIXIN = 3;
    private TextView mTvAgreement;
    private Button mBtnWeixin;
    private MyHandler handler;
    private UMSocialService mLoginController = UMServiceFactory.getUMSocialService("com.umeng.login");

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                SignInActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //handler init
        handler=new MyHandler(this);
        mBtnWeixin = (Button) findViewById(R.id.btn_weixin);
        mTvAgreement = (TextView) findViewById(R.id.tv_user_agreement);
        mBtnWeixin.setOnClickListener(this);

        //agreement
        SpannableString agreement = new SpannableString("使用即表示同意用户协议");
        agreement.setSpan(new NoLineClickSpan(), 7, agreement.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTvAgreement.setText(agreement);
        mTvAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        //set up umeng
        mLoginController.getConfig().setSsoHandler(new SinaSsoHandler());
        String appId = AppContext.getInstance().getMetaData("WEIXIN_ID");
        String appSecret = AppContext.getInstance().getMetaData("WEIXIN_SECRET");

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(SignInActivity.this, appId, appSecret);
        wxHandler.addToSocialSDK();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_background:
                hiddenKeyBoard(v);
                break;
            case R.id.btn_weixin:
                openAuth(SHARE_MEDIA.WEIXIN);
                break;
        }
    }

    private void disableViews() {
        mBtnWeixin.setEnabled(false);
    }

    private void enableViews() {
        mBtnWeixin.setEnabled(true);
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void openAuth(final SHARE_MEDIA platform) {
        mLoginController.doOauthVerify(SignInActivity.this, platform, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                disableViews();
                ToastUtils.show(SignInActivity.this, "授权开始...");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                handler.sendEmptyMessage(AUTH_FAILED);
                Log.d("Monolog-signin", e.getMessage());
            }

            @Override
            public void onComplete(Bundle value, final SHARE_MEDIA platform) {
                String uid = value.getString("uid");
                if (TextUtils.isEmpty(uid)) {
                    handler.sendEmptyMessage(AUTH_FAILED);
                    Log.d("Monolog-signin", "umeng uid empty");
                    return;
                }
                mLoginController.getPlatformInfo(SignInActivity.this, platform, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (status == StatusCode.ST_CODE_SUCCESSED) {
                            Message msg = new Message();
                            msg.what = OPEN_WEIXIN;
                            Bundle b = new Bundle();
                            b.putString("nickname", info.get("nickname").toString());
                            b.putString("openid", info.get("openid").toString());
                            b.putString("unionid", info.get("unionid").toString());
                            b.putString("sex", info.get("sex").toString());
                            b.putString("headimgurl", info.get("headimgurl").toString());
                            msg.setData(b);
                            handler.sendMessage(msg);
                        } else {
                            handler.sendEmptyMessage(AUTH_FAILED);
                            Log.d("Monolog-signin", "get Auth code" + String.valueOf(status));
                        }
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                enableViews();
            }
        });
    }

    private void postWeixin(Bundle result) {
        /**
         *sex         * nickname         * unionid         * openid         * province         * headimgurl         *
         */
        String name = result.getString("nickname");
        String openid = result.getString("openid");
        String token = result.getString("unionid");
        String gender = result.getString("sex").equals("1") ? "m" : "f";
        String avatar = result.getString("headimgurl");
        String type = "weixin";
        AuthApi.open(name, openid, gender, avatar, token, type, new TextHttpResponseHandler() {
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(AUTH_FAILED);
                Log.d("Monolog-signin", "post error" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Gson gson = new Gson();
                    AuthResponseBean response = gson.fromJson(responseString, AuthResponseBean.class);
                    PrefService.getInstance(SignInActivity.this).setToken(response.getToken());
                    PrefService.getInstance(SignInActivity.this).setUser(response.getUser());
                    handler.sendEmptyMessage(AUTH_SUCCESS);
                } catch (Exception ex) {
                    handler.sendEmptyMessage(AUTH_FAILED);
                    Log.d("Monolog-signin", "SUCEESEE ERROR"+ex.getMessage());
                }
            }
        });
    }

    private class MyHandler extends Handler {

       WeakReference<SignInActivity> mAct;

       public  MyHandler(SignInActivity act){
           mAct=new WeakReference<>(act);
       }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_WEIXIN:
                    mAct.get().postWeixin(msg.getData());
                    break;
                case AUTH_FAILED:
                    mAct.get().enableViews();
                    ToastUtils.show(mAct.get(), "授权失败...");
                    break;
                case AUTH_SUCCESS:
                    mAct.get(). enableViews();
                    EaseMobService.getInstance().easeMobLogin(new Handler());
                    EaseMobService.getInstance().getContacts(new Handler());
                    if(PrefService.getInstance(mAct.get().getApplicationContext()).isInstalled())  {
                        mAct.get().startActivity(GuideActivity.newIntent());
                        PrefService.getInstance(mAct.get().getApplicationContext()).setInstalled();
                    } else {
                        mAct.get().startActivity(MainActivity.newIntent());
                    }
                    mAct.get(). finish();
                    break;
                default:
                    break;
            }
        }
    };

    private class NoLineClickSpan extends ClickableSpan {
        public NoLineClickSpan() {
            super();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            startActivity(WebActivity.newIntent(BaseClient.URL_USER_AGREEMENT, "用户协议"));
        }
    }
}
