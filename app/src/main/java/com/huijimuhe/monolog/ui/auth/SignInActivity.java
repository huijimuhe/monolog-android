package com.huijimuhe.monolog.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.network.BaseClient;
import com.huijimuhe.monolog.presenter.auth.SignInContract;
import com.huijimuhe.monolog.presenter.auth.SignInPresenter;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.ui.base.LightProgressDialog;
import com.huijimuhe.monolog.ui.main.GuideActivity;
import com.huijimuhe.monolog.ui.main.MainActivity;
import com.huijimuhe.monolog.ui.main.WebActivity;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class SignInActivity extends AbstractActivity implements SignInContract.View, View.OnClickListener {

    public static final int AUTH_REQUEST_START = 2;
    public static final int AUTH_REQUEST_INFO = 3;
    public static final int AUTH_VERIFY_START = 4;
    public static final int AUTH_VERIFY_SUCCESS = 0;
    public static final int AUTH_VERIFY_FAILED = 1;

    private TextView mTvAgreement;
    private Button mBtnWeixin;
    private UMShareAPI mShareAPI = null;
    private SignInContract.Presenter mPresenter;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                SignInActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mBtnWeixin = (Button) findViewById(R.id.btn_weixin);
        mTvAgreement = (TextView) findViewById(R.id.tv_user_agreement);
        mBtnWeixin.setOnClickListener(this);

        //agreement
        SpannableString agreement = new SpannableString("使用即表示同意用户协议");
        agreement.setSpan(new NoLineClickSpan(), 7, agreement.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTvAgreement.setText(agreement);
        mTvAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        // 添加微信平台
        mShareAPI = UMShareAPI.get(this);

        //MVP
        this.mPresenter = new SignInPresenter(this,mShareAPI);
    }

    @Override
    public void showIndexOrGuide(boolean isFirstTime) {
        if(isFirstTime){
            startActivity(GuideActivity.newIntent());
        }else{
            startActivity(MainActivity.newIntent());
        }
        finish();
    }

    @Override
    public void showError(String msg) {
        ToastUtils.show(SignInActivity.this,msg);
    }

    @Override
    public void disableViews(boolean active) {
        mBtnWeixin.setEnabled(active);
    }


    @Override
    public void showProgressDialog(String msg) {
        ToastUtils.show(SignInActivity.this,msg);
       // LightProgressDialog.create(SignInActivity.this,msg).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //umeng social
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_background:
                hiddenKeyBoard(v);
                break;
            case R.id.btn_weixin:
                mPresenter.postSignIn(SHARE_MEDIA.WEIXIN);
                break;
        }
    }


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
