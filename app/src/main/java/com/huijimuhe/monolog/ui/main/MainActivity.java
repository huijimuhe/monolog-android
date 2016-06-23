package com.huijimuhe.monolog.ui.main;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.MsgReceiver;
import com.huijimuhe.monolog.data.statue.IndexResponse;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.presenter.index.IndexContract;
import com.huijimuhe.monolog.presenter.index.IndexPresenter;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.domain.PrefManager;
import com.huijimuhe.monolog.ui.chat.ChatListActivity;
import com.huijimuhe.monolog.ui.statue.PublishActivity;
import com.huijimuhe.monolog.utils.ViewUtility;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class MainActivity extends AbstractActivity implements DrawerFragment.NavigationDrawerCallbacks, GuessFragment.GuessListener, IndexContract.View {

    private ImageView mIvStatueImg;
    private TextView mTvStatueText;
    private TextView mTvRightCount;
    private TextView mTvMissCount;
    private TextView mTvReport;
    private LinearLayout mLayoutFunction;

    private DrawerFragment drawerFragment;
    private Toolbar toolbar;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private InterstitialAD iad;
    private IndexPresenter mPresenter;
    private MsgReceiver newMsgReceiver;

    private long mExitTime = 0;
    private int mRefreshCount;
    protected SparseArray<Fragment> mFragments;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

        }
        //presenter
        mPresenter = new IndexPresenter(this);
        mPresenter.start();
        //ui
        initUi();
        //Receiver
        registerNewMsgReceiver();
        //ad
        iad = new InterstitialAD(this, "1104878885", "4020417016750632");
        mPresenter.loadStatue();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaiduService.getInstance().StopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiduService.getInstance().StopLocation();
        unRegisterNewMsgReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
    public void onBackPressed() {
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
                mPresenter.addStatue();
                break;
            case R.id.action_chat:
                ActionItemBadge.update(this, item, FontAwesome.Icon.faw_comment_o, Color.GRAY, ActionItemBadge.BadgeStyles.GREY, Integer.MIN_VALUE);
                PrefManager.getInstance().cleanUnread();
                mPresenter.showChatList();
                break;
        }
        return true;
    }

    @Override
    public void onGuessClick(final Account user) {
        mPresenter.guess(user.getId());
    }

    @Override
    public void onDrawerItemSelected(int position) {

    }

    @Override
    public void showUnRead(MenuItem item) {

    }


    @Override
    public void showImgGallery(String imgPath) {
        Intent intent = PhotoViewActivity.newIntent(imgPath);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, mIvStatueImg, PhotoViewActivity.TRANSIT_PIC);
        ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
    }

    @Override
    public void showError(String msg) {

    }

    private void initUi() {
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Drawer
        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerFragment.setUp(R.id.navigation_drawer, toolbar, (DrawerLayout) findViewById(R.id.drawer_layout));

        //swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadStatue();
            }
        });

        //statue box
        mIvStatueImg = ViewUtility.findViewById(this, R.id.iv_img);
        mTvStatueText = ViewUtility.findViewById(this, R.id.tv_text);
        mLayoutFunction = ViewUtility.findViewById(this, R.id.layout_function);
        mTvRightCount = ViewUtility.findViewById(this, R.id.tv_right_count);
        mTvMissCount = ViewUtility.findViewById(this, R.id.tv_miss_count);
        mTvReport = (TextView) findViewById(R.id.tv_report);

        mIvStatueImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showImageGallery();
            }
        });

        mTvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.report();
            }
        });
    }

    @Override
    public void showReport() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("举报")
                .setMessage("该信息有违反国家法律法规的内容")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.report();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    @Override
    public void showStatue(IndexResponse response) {
        AppContext.getInstance().loadImg(mIvStatueImg, response.getStatue().getImg_path());
        mTvStatueText.setText(response.getStatue().getText());
        mTvRightCount.setText(response.getStatue().getRight_count());
        mTvMissCount.setText(response.getStatue().getMiss_count());
    }

    @Override
    public void showGuessPage(List<Account> accounts) {
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.valueAt(i);
            if (f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(f)
                        .commit();
        }
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down).show(mFragments.get(IndexContract.GUESS_PAGE)).commit();
        ((GuessFragment) mFragments.get(IndexContract.GUESS_PAGE)).notifyChange(accounts);
    }

    @Override
    public void showChatUI(String uid) {

    }

    @Override
    public void showOverDue() {

    }

    @Override
    public void showLoading(final boolean active) {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
                mSwipeRefreshLayout.setEnabled(!active);
            }
        }, 500);
    }

    @Override
    public void initFragments() {
        //set up mFragments
        mFragments = new SparseArray<>();

        //Guess Fragment
        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(IndexContract.GUESS_PAGE));
        if (mainFragment == null) {
            mainFragment = GuessFragment.newInstance();
        }
        mFragments.append(IndexContract.GUESS_PAGE, mainFragment);

        //Success Fragment
        Fragment succcessFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(IndexContract.SUCCESS_PAGE));
        if (succcessFragment == null) {
            succcessFragment = SuccessFragment.newInstance();
        }
        mFragments.append(IndexContract.SUCCESS_PAGE, succcessFragment);

        //Failed Fragment
        Fragment failedFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(IndexContract.FAILED_PAGE));
        if (failedFragment == null) {
            failedFragment = FailedFragment.newInstance();
        }
        mFragments.append(IndexContract.FAILED_PAGE, failedFragment);

        //none Fragment
        Fragment noneFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(IndexContract.NONE_PAGE));
        if (noneFragment == null) {
            noneFragment = NoneFragment.newInstance();
        }
        mFragments.append(IndexContract.NONE_PAGE, noneFragment);

        //add to activity
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.valueAt(i);
            if (!f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, f, String.valueOf(mFragments.keyAt(i)))
                        .hide(f)
                        .commit();
        }
    }

    @Override
    public void showChatListUI() {
        startActivity(ChatListActivity.newIntent());
    }

    @Override
    public void showAddStatusUI() {
        startActivityForResult(PublishActivity.newIntent(), PublishActivity.REQUEST_PUBLISH);
    }

    @Override
    public void showResultPage(int tag, Account account) {
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.valueAt(i);
            if (f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(f)
                        .commit();
        }

        switch (tag) {
            case IndexContract.GUESS_RESULT_SUCCESS:
                getSupportFragmentManager().beginTransaction().show(mFragments.get(IndexContract.SUCCESS_PAGE)).commit();
                ((SuccessFragment) mFragments.get(IndexContract.SUCCESS_PAGE)).notifyChange(account);
                break;
            case IndexContract.GUESS_RESULT_FAILED:
                getSupportFragmentManager().beginTransaction().show(mFragments.get(IndexContract.FAILED_PAGE)).commit();
                break;
            case IndexContract.GUESS_RESULT_NONE:
                getSupportFragmentManager().beginTransaction().show(mFragments.get(IndexContract.NONE_PAGE)).commit();
                break;
        }
    }

    @Override
    public void showSplashAd() {
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

    private void registerNewMsgReceiver() {
        newMsgReceiver = new MsgReceiver();
        newMsgReceiver.setNewMsgReceiveListener(new MsgReceiver.newMsgReceiveListener() {
            @Override
            public void onNewMsg() {
                int count = PrefManager.getInstance().getUnread();

                ActionItemBadge.update(MainActivity.this, toolbar.getMenu().findItem(R.id.action_chat), FontAwesome.Icon.faw_comment_o, Color.GRAY, ActionItemBadge.BadgeStyles.RED, PrefManager.getInstance().getUnread());
            }
        });
        IntentFilter cmdFilter = new IntentFilter(MsgReceiver.NEW_MSG_BROADCAST);
        registerReceiver(newMsgReceiver, cmdFilter);
    }

    private void unRegisterNewMsgReceiver() {
        unregisterReceiver(newMsgReceiver);
    }

}
