package com.huijimuhe.monolog.domain;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huijimuhe.monolog.network.AuthApi;
import com.huijimuhe.monolog.network.TextHttpResponseLoopHandler;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.db.ContactDao;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Huijimuhe on 2016/3/20.
 * This is a part of Monolog
 * enjoy
 */
public class EaseMobService {
    protected static final String TAG =EaseMobService.class.getName();

    /**
     * 单例
     */
    private static EaseMobService instance = null;

    /**
     * 消息监听
     */
    private EMEventListener msgListener;

    /**
     * application context
     */
    private Context mContext = null;

    /**
     * 登录用户
     */
    private Account mOwner;

    /**
     * Looper线程
     */
    HandlerThread thread = new HandlerThread("async");

    private EaseMobService() {

    }

    public synchronized static EaseMobService getInstance() {
        if (instance == null) {
            instance = new EaseMobService();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;

        if (!EaseUI.getInstance().init(mContext)) {
            Log.d("easemob", "easemob init failed");
        }

        registerEaseMobUserInfo();
        registerEaseMobEventListener();
    }

    /**
     * 全局事件监听
     * 这里是拿来获取用户资料和未读计数的
     */
    protected void registerEaseMobEventListener() {

        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent event) {
                final EMMessage msg = (EMMessage) event.getData();
                if (event.getEvent() == EMNotifierEvent.Event.EventNewMessage) {

                    //未在聊天的任何界面就计数
                    if (!EaseUI.getInstance().hasForegroundActivies()) {
                        PrefManager.getInstance().increatUnread();
                        Log.d(TAG, "new Msg Count");
                    }

                    //更新联系人
                    asyncContact(msg);

                    //广播通知，主界面拿来更新的
                    Intent broadcastIntent = new Intent(MsgReceiver.NEW_MSG_BROADCAST);
                    mContext.sendBroadcast(broadcastIntent, null);
                }
            }
        });
    }

    /**
     * EASEUI的用户资料
     */
    protected void registerEaseMobUserInfo() {
        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
                                                        @Override
                                                        public EaseUser getUser(String username) {
                                                            //是否是本人
                                                            Account owner = AppContext.getInstance().getUser();
                                                            if (username.equals(owner.getId())) {
                                                                EaseUser eu = new EaseUser(username);
                                                                eu.setNick(owner.getName());
                                                                eu.setAvatar(owner.getAvatar());
                                                                return eu;
                                                            }
                                                            //不是本人
                                                            EaseUser eu = ContactDao.getEaseMobUser(username);
                                                            return eu;
                                                        }
                                                    }
        );
    }

    /**
     * 用户登录
     */
    public void easeMobLogin(Account account,final EMCallBack callback) {
        String name = account.getId();
        String pwd = "pwd" + account.getId();
        EMChatManager.getInstance().login(name, pwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String error) {
                callback.onError(code,error);
            }
        });
    }

    /**
     * 获取新消息后更新联系人
     *
     * @param msg
     */
    private void asyncContact(EMMessage msg) {
        final String uid = msg.getUserName();

        thread.start();

        AuthApi.contact(uid, new TextHttpResponseLoopHandler(thread.getLooper()) {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG,String.valueOf( statusCode));
                throwable.printStackTrace();
                thread.quit();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                Account user = gson.fromJson(responseString, Account.class);
                Log.d(TAG, user.toString());
                ContactDao.asyncReplace(user, AppContext.getInstance().getUser().getId());
                thread.quit();
            }
        });
    }
}
