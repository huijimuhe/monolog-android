package com.huijimuhe.monolog.ui.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.AuthApi;
import com.huijimuhe.monolog.api.StatueApi;
import com.huijimuhe.monolog.bean.IndexResponseBean;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.MsgReceiver;
import com.huijimuhe.monolog.domain.PrefService;
import com.huijimuhe.monolog.ui.auth.SignInActivity;
import com.huijimuhe.monolog.ui.chat.ChatListActivity;
import com.huijimuhe.monolog.ui.main.DrawerFragment;
import com.huijimuhe.monolog.ui.main.FailedFragment;
import com.huijimuhe.monolog.ui.main.MainFragment;
import com.huijimuhe.monolog.ui.main.NoneFragment;
import com.huijimuhe.monolog.ui.main.SuccessFragment;
import com.huijimuhe.monolog.ui.statue.PublishActivity;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import org.apache.http.Header;

public abstract class AbstractMainActivity extends AbstractActivity implements DrawerFragment.NavigationDrawerCallbacks,MsgReceiver.newMsgReceiveListener{

    public static final int MAIN_INDEX = 0;
    public static final int SUCCESS_INDEX = 1;
    public static final int FAILED_INDEX = 2;
    public static final int NONE_INDEX = 3;
    public static final int HANDLER_LOAD_STATUE= 30;
    public static final int HANDLER_GUESS_RIGHT = 31;
    public static final int HANDLER_GUESS_MISS = 32;
    public static final int HANDLER_NETWORK_ERROR = 33;

    protected DrawerFragment drawerFragment;
    protected SparseArray<Fragment> fragments;
    protected Toolbar toolbar;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected int mCurFragment;
    public IndexResponseBean mGuessStatue;
    protected boolean isHidden = false;
    protected boolean isRefresh = false;
    protected long mExitTime = 0;
    //refresh count =>for ad
    protected int mRefreshCount;
    protected InterstitialAD iad;

    /**
     * 新消息通知
     */
    private MsgReceiver newMsgReceiver=new MsgReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurFragment = MAIN_INDEX;
        mGuessStatue = new IndexResponseBean();
        mRefreshCount=0;
        initFragment();
        initBaseLayout();

        iad = new InterstitialAD(this, "1104878885","4020417016750632");

        newMsgReceiver.setNewMsgReceiveListener(this);
        registerNewMsgReceiver();
    }

    @Override
    protected void onResume() {
        isHidden = false;
        super.onResume();
        if (mActivityState == STATE_FIRST_INIT) {
            mActivityState = STATE_RESUME;
            loadStatue();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isHidden = true;
        if (isRefresh) {
            BaiduService.getInstance().StopLocation();
            isRefresh = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRefresh) {
            BaiduService.getInstance().StopLocation();
        }
        unRegisterNewMsgReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("statue", mGuessStatue);
        outState.putInt("index", mCurFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==PublishActivity.REQUEST_PUBLISH) {
            if (resultCode == RESULT_OK) {
                drawerFragment.updateNums();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //重载菜单图标
        ActionItemBadge.update(this, menu.findItem(R.id.action_publish), FontAwesome.Icon.faw_plus, Color.GRAY, ActionItemBadge.BadgeStyles.GREY, Integer.MIN_VALUE);
        ActionItemBadge.update(this, menu.findItem(R.id.action_chat), FontAwesome.Icon.faw_comment_o, Color.GRAY, ActionItemBadge.BadgeStyles.GREY, Integer.MIN_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_publish:
                startActivityForResult(PublishActivity.newIntent(), PublishActivity.REQUEST_PUBLISH);
                break;
            case R.id.action_chat:
                ActionItemBadge.update(this, item, FontAwesome.Icon.faw_comment_o,Color.GRAY, ActionItemBadge.BadgeStyles.GREY,  Integer.MIN_VALUE);
                PrefService.getInstance(getApplicationContext()).cleanUnread();
                startActivity(ChatListActivity.newIntent());
                break;
        }
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isHidden) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerFragment.isVisible()) {
                drawerFragment.hideDrawer();
            } else {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按退出",
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

    private void initBaseLayout() {
        drawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up the drawer.
        drawerFragment.setUp(R.id.navigation_drawer, toolbar, (DrawerLayout) findViewById(R.id.drawer_layout));
        //swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadStatue();
            }
        });
        //fragments
        switchFragment(mCurFragment);
    }

    private void initFragment() {
        //set up fragments
        fragments = new SparseArray<>();
        //Main Fragment
        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(MAIN_INDEX));
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }
        fragments.append(MAIN_INDEX, mainFragment);
        //Success Fragment
        Fragment succcessFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(SUCCESS_INDEX));
        if (succcessFragment == null) {
            succcessFragment = SuccessFragment.newInstance();
        }
        fragments.append(SUCCESS_INDEX, succcessFragment);
        //Failed Fragment
        Fragment failedFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(FAILED_INDEX));
        if (failedFragment == null) {
            failedFragment = FailedFragment.newInstance();
        }
        fragments.append(FAILED_INDEX, failedFragment);
        //none Fragment
        Fragment noneFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(NONE_INDEX));
        if (noneFragment == null) {
            noneFragment = NoneFragment.newInstance();
        }
        fragments.append(NONE_INDEX, noneFragment);
        //add to activity
        for (int i = 0; i < fragments.size(); i++) {
            Fragment f = fragments.valueAt(i);
            if (!f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, f, String.valueOf(fragments.keyAt(i)))
                        .hide(f)
                        .commit();
        }
    }

    protected abstract void loadStatue();

    protected void setSwipeRefreshLoading() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mSwipeRefreshLayout.setEnabled(false);
                }
            },500);
        }
    }

    protected void setSwipeRefreshNormal() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                }
            },1000);
        }
    }

    protected void switchFragment(int index) {
        if (isHidden) {
            return;
        }
        for (int i = 0; i < fragments.size(); i++) {
            Fragment f = fragments.valueAt(i);
            if (f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(f)
                        .commit();
        }

        switch (index) {
            case MAIN_INDEX:
                mCurFragment = MAIN_INDEX;
                getSupportFragmentManager().beginTransaction().show(fragments.get(MAIN_INDEX)).commit();
                break;
            case SUCCESS_INDEX:
                mCurFragment = SUCCESS_INDEX;
                getSupportFragmentManager().beginTransaction().show(fragments.get(SUCCESS_INDEX)).commit();
                break;
            case FAILED_INDEX:
                mCurFragment = FAILED_INDEX;
                getSupportFragmentManager().beginTransaction().show(fragments.get(FAILED_INDEX)).commit();
                break;
            case NONE_INDEX:
                mCurFragment = NONE_INDEX;
                getSupportFragmentManager().beginTransaction().show(fragments.get(NONE_INDEX)).commit();
                break;
        }
    }

    public boolean isMainFragment() {
        return mCurFragment == MAIN_INDEX ? true : false;
    }

    /**
     * 新消息提示
     */
    @Override
    public void onNewMsg() {
        int count=PrefService.getInstance(getApplicationContext()).getUnread();
        Log.d("MainActivity", "Recieve msg:" + count);
        ActionItemBadge.update(this, toolbar.getMenu().findItem(R.id.action_chat), FontAwesome.Icon.faw_comment_o,Color.GRAY, ActionItemBadge.BadgeStyles.RED, PrefService.getInstance(getApplicationContext()).getUnread());
    }

    private void registerNewMsgReceiver(){
        IntentFilter cmdFilter = new IntentFilter(MsgReceiver.NEW_MSG_BROADCAST);
        registerReceiver(newMsgReceiver, cmdFilter);
    }

    private void unRegisterNewMsgReceiver(){
        unregisterReceiver(newMsgReceiver);
    }

    @Override
    public void onDrawerItemSelected(int position) {
        switch (position) {
            case 1:
                break;
        }
    }

    protected void postSignOut() {
        PrefService.getInstance(getApplicationContext()).cleanUser();
        startActivity(SignInActivity.newIntent());
        AuthApi.signOut(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                finish();
            }
        });
    }

    protected void postReport() {
        final String id = mGuessStatue.getStatue().getId();
        StatueApi.postReport(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(getApplicationContext(), "网络错误请重试");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ToastUtils.show(getApplicationContext(), "举报成功,我们将即时处理!\n谢谢您的支持");
            }
        });
    }
    /**
     * 设置广告条广告
     */
    private void setupBannerAd() {
    }

     /**
        * 展示插屏广告
        * 仅在adreceive事件发生后调用才有效。
        * IntersititialAd.show 方法会开启一个透明的activity
        *如广告情景不合适，也可考虑InterstitialAd.showAsPopupWindow
        *配套的关闭方法为closePopupWindow
      * 优先建议调用show
      */
     protected void showAd() {
         iad.setADListener(new AbstractInterstitialADListener() {

             @Override
             public void onNoAD(int arg0) {
                 Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
             }

             @Override
             public void onADReceive() {
                 Log.i("AD_DEMO", "onADReceive");
                iad.show();
            }
        });
        iad.loadAD();
    }
}
