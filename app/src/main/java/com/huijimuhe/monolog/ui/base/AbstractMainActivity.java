package com.huijimuhe.monolog.ui.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.data.statue.IndexResponse;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.MsgReceiver;
import com.huijimuhe.monolog.domain.PrefManager;
import com.huijimuhe.monolog.ui.main.DrawerFragment;
import com.huijimuhe.monolog.ui.statue.PublishActivity;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.qq.e.ads.interstitial.InterstitialAD;

public abstract class AbstractMainActivity extends AbstractActivity implements DrawerFragment.NavigationDrawerCallbacks, MsgReceiver.newMsgReceiveListener {

    protected DrawerFragment drawerFragment;
    protected Toolbar toolbar;
    protected int mCurTag;
    protected long mExitTime = 0;
    protected int mRefreshCount;
    protected InterstitialAD iad;
    private MsgReceiver newMsgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up the drawer.
        drawerFragment = (DrawerFragment)                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerFragment.setUp(R.id.navigation_drawer, toolbar, (DrawerLayout) findViewById(R.id.drawer_layout));

        mRefreshCount = 0;
        initUI();

        iad = new InterstitialAD(this, "1104878885", "4020417016750632");

        newMsgReceiver.setNewMsgReceiveListener(this);
        registerNewMsgReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", mCurTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PublishActivity.REQUEST_PUBLISH) {
            if (resultCode == RESULT_OK) {
                drawerFragment.updateNums();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerFragment.isVisible()) {
                drawerFragment.hideDrawer();
            } else {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出",
                            Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initUI() {
    }
    private void registerNewMsgReceiver() {
        newMsgReceiver = new MsgReceiver();
        newMsgReceiver.setNewMsgReceiveListener(new MsgReceiver.newMsgReceiveListener() {
            @Override
            public void onNewMsg() {
                }
        });
        IntentFilter cmdFilter = new IntentFilter(MsgReceiver.NEW_MSG_BROADCAST);
        registerReceiver(newMsgReceiver, cmdFilter);
    }

    private void unRegisterNewMsgReceiver() {
        unregisterReceiver(newMsgReceiver);
    }

    @Override
    public void onDrawerItemSelected(int position) {
        switch (position) {
            case 1:
                break;
        }
    }

}
