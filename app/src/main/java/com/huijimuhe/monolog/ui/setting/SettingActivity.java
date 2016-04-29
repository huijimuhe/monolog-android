package com.huijimuhe.monolog.ui.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.AuthApi;
import com.huijimuhe.monolog.api.BaseClient;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.ui.auth.SignInActivity;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.ui.main.GuideActivity;
import com.huijimuhe.monolog.ui.main.WebActivity;
import com.huijimuhe.monolog.utils.MarketUtils;
import com.huijimuhe.monolog.domain.PrefService;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public class SettingActivity extends AbstractActivity implements View.OnClickListener {
    private Toolbar toolbar;
    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                SettingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUI();
    }

    private void initUI(){
        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);

        findViewById(R.id.btn_editProfile).setOnClickListener(this);
        findViewById(R.id.btn_cleanCache).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_think).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_aggrement).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.btn_editProfile:
                  startActivity(EditProfileActivity.newIntent(EditProfileActivity.FROM_EDIT));
                  break;
              case R.id.btn_cleanCache:
                  cleanCache();
                  break;
              case R.id.btn_share:
                  openShare();
                  break;
              case R.id.btn_think:
                  think();
                  break;
              case R.id.btn_about:
                  startActivity(GuideActivity.newIntent());
                  break;
              case R.id.btn_aggrement:
                  startActivity(WebActivity.newIntent(BaseClient.URL_USER_AGREEMENT, "用户协议"));
                  break;
              case R.id.btn_logout:
                  logOut();
                  break;
          }
    }

    private  void cleanCache(){

    }

    private  void think(){
        MarketUtils.launchAppDetail("com.huijimuhe.monolog","");
    }

    private  void logOut(){
            PrefService.getInstance(getApplicationContext()).cleanUser();
            startActivity(SignInActivity.newIntent());
            AuthApi.signOut(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                   startActivity(SignInActivity.newIntent());
                   finish();
                }
            });
    }
}
