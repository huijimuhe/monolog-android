package com.huijimuhe.monolog.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.StatueApi;
import com.huijimuhe.monolog.bean.GuessResponseBean;
import com.huijimuhe.monolog.bean.IndexResponseBean;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.db.ContactDao;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.MsgReceiver;
import com.huijimuhe.monolog.ui.base.AbstractMainActivity;
import com.huijimuhe.monolog.ui.base.OnBaiduLBSListener;
import com.huijimuhe.monolog.domain.PrefService;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.lang.ref.WeakReference;

public class MainActivity extends AbstractMainActivity implements MainFragment.OnGuessListener {

    //Statue Layout
    private LinearLayoutManager mLayoutManager;
    private ImageView mIvStatueImg;
    private TextView mTvStatueText;
    private TextView mTvRightCount;
    private TextView mTvMissCount;
    private TextView mTvReport;
    private LinearLayout mLayoutFunction;

    //mHandler
    private  MyHandler mHandler;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mGuessStatue = savedInstanceState.getParcelable("statue");
            mCurFragment = savedInstanceState.getInt("index");
        }

        mHandler =new MyHandler(this);

        initStatueLayout();
    }

    @Override
    public void onGuessClick(final UserBean user) {
        setSwipeRefreshLoading();
        StatueApi.postGuess(String.valueOf(mGuessStatue.getStatue().getId()), user.getId(), new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                setSwipeRefreshNormal();
                Gson gson=new Gson();
                GuessResponseBean response=gson.fromJson(responseString,GuessResponseBean.class);
                if (response.getResult()==1) {
                    //猜对
                    Message msg=new Message();
                    Bundle b=new Bundle();
                    b.putParcelable("user", response.getUser());
                    msg.setData(b);
                    msg.what=HANDLER_GUESS_RIGHT;
                    mHandler.sendMessage(msg);
                } else {
                    //猜错
                    mHandler.sendEmptyMessage(HANDLER_GUESS_MISS);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                setSwipeRefreshNormal();
                //报错
                mHandler.sendEmptyMessage(HANDLER_NETWORK_ERROR);
            }
        });

    }

    private void initStatueLayout(){
        //statue box
        mIvStatueImg = (ImageView) findViewById(R.id.iv_img);
        mTvStatueText = (TextView) findViewById(R.id.tv_text);
        mLayoutFunction = (LinearLayout) findViewById(R.id.layout_function);
        mTvRightCount = (TextView) findViewById(R.id.tv_right_count);
        mTvMissCount = (TextView)findViewById(R.id.tv_miss_count);
        mTvReport = (TextView) findViewById(R.id.tv_report);

        mIvStatueImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGuessStatue != null)
                    startActivity(PhotoViewActivity.newIntent(mGuessStatue.getStatue().getImg_path()));
            }
        });

        mTvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("举报")
                        .setMessage("该信息有违反国家法律法规的内容")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postReport();
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
        });
    }

    @Override
    protected void loadStatue() {
        BaiduService.getInstance().StartLocation(baiduLBSListener);
    }

    private void getStatue(double lng,double lat){
        StatueApi.getStatue(lng, lat, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                setSwipeRefreshNormal();
                Gson gson = new Gson();
                try {
                    mGuessStatue = gson.fromJson(responseBody, IndexResponseBean.class);
                        for (UserBean u :
                                mGuessStatue.getUsers()) {
                            String strTemp = u.getId();
                        }

                } catch (Exception e) {
                   mHandler.sendEmptyMessage(HANDLER_NETWORK_ERROR);
                    return;
                }

                if (!isMainFragment()) {
                    switchFragment(MAIN_INDEX);
                }

                ((MainFragment)fragments.get(MAIN_INDEX)).notifyChange(mGuessStatue.getUsers());

                AppContext.getInstance().loadImg(mIvStatueImg,mGuessStatue.getStatue().getImg_path());
                mTvStatueText.setText(mGuessStatue.getStatue().getText());
                mTvRightCount.setText(mGuessStatue.getStatue().getRight_count());
                mTvMissCount.setText(mGuessStatue.getStatue().getMiss_count());

                if (!isMainFragment()) {
                    switchFragment(MAIN_INDEX);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                setSwipeRefreshNormal();
                if (responseBody != null && responseBody.trim().equals("403")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("登录过期")
                            .setMessage("你的帐号已过期，请重新登录")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    postSignOut();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                //报错
                mHandler.sendEmptyMessage(HANDLER_NETWORK_ERROR);
            }
        });
    }

    static class MyHandler extends Handler{
        WeakReference<MainActivity> mAct;

        public  MyHandler(MainActivity act){
            mAct=new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_LOAD_STATUE:
                   double lng= msg.getData().getDouble("lng");
                   double lat= msg.getData().getDouble("lat");
                   mAct.get().getStatue(lng,lat);
                    break;
                case HANDLER_GUESS_RIGHT:
                    //弹窗广告
                    mAct.get().showAd();
                    //计数
                    UserBean guessedUser=msg.getData().getParcelable("user");
                    UserBean owner= PrefService.getInstance(mAct.get()).getUser();
                    owner.setRight_count(owner.getRight_count() + 1);
                    PrefService.getInstance(mAct.get()).setUser(owner);
                    //添加联系人
                    ContactDao.addContact(guessedUser, owner.getId());
                    ((SuccessFragment)mAct.get(). fragments.get(SUCCESS_INDEX)).notifyChange(guessedUser);
                    mAct.get().switchFragment(SUCCESS_INDEX);
                    break;
                case HANDLER_GUESS_MISS:
                    //计数
                    UserBean owner2= PrefService.getInstance(mAct.get()).getUser();
                    owner2.setMiss_count(owner2.getMiss_count() + 1);
                    PrefService.getInstance(mAct.get()).setUser(owner2);
                    mAct.get().switchFragment(FAILED_INDEX);
                    break;
                case HANDLER_NETWORK_ERROR:
                    mAct.get().switchFragment(NONE_INDEX);
                    break;
                default:
                    break;
            }
        }
    }


    private OnBaiduLBSListener baiduLBSListener = new OnBaiduLBSListener() {
        @Override
        public void onStart() {
            setSwipeRefreshLoading();
            isRefresh = true;
        }

        @Override
        public void onFailed(int code) {
            setSwipeRefreshNormal();
            isRefresh = false;
            mHandler.sendEmptyMessage(NONE_INDEX);
        }

        @Override
        public void onComplete(int code, double lng, double lat, String addr) {
            isRefresh = false;
            if (isHidden)
                return;
            Message msg=new Message();
            Bundle b=new Bundle();
            b.putDouble("lng", lng);
            b.putDouble("lat", lat);
            msg.setData(b);
            msg.what=HANDLER_LOAD_STATUE;
            mHandler.sendMessage(msg);
        }
    };

}
