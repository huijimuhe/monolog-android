package com.huijimuhe.monolog.data.account.source;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.account.AuthResponse;
import com.huijimuhe.monolog.db.ContactDao;
import com.huijimuhe.monolog.network.AuthApi;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class AccountRemoteDataSource {

    private UMShareAPI mShareAPI = null;
    private static AccountRemoteDataSource INSTANCE;
    private Activity mContext;

    // Prevent direct instantiation.
    private AccountRemoteDataSource(Activity activity, UMShareAPI umShareAPI) {
        mContext = activity;
        mShareAPI = umShareAPI;
    }

    public static AccountRemoteDataSource getInstance(Activity activity, UMShareAPI umShareAPI) {
        if (INSTANCE == null) {
            INSTANCE = new AccountRemoteDataSource(activity, umShareAPI);
        }
        return INSTANCE;
    }

    public void signIn(final SHARE_MEDIA platform, final IAccountDataSource.SignInCallback callback) {
        mShareAPI.doOauthVerify(mContext, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int status, Map<String, String> info) {
                getSocialInfo(platform, callback);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                callback.onError("授权错误");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                callback.onError("取消授权");
            }
        });
    }

    /**
     * 获取社交网络用户资料
     *
     * @param platform
     */
    private void getSocialInfo(final SHARE_MEDIA platform, final IAccountDataSource.SignInCallback callback) {
        mShareAPI.getPlatformInfo(mContext, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int status, Map<String, String> info) {
                postSocialInfo(info, callback);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                callback.onError("获取资料错误");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                callback.onError("取消获取资料");
            }
        });
    }

    /**
     * 登录服务器
     *
     * @param info
     * @param callback
     */
    private void postSocialInfo(Map<String, String> info, final IAccountDataSource.SignInCallback callback) {
        String name = info.get("nickname");
        String openid = info.get("openid");
        String token = info.get("unionid");
        String gender = info.get("sex").equals("1") ? "m" : "f";
        String avatar = info.get("headimgurl");
        String type = "weixin";

        AuthApi.open(name, openid, gender, avatar, token, type, new TextHttpResponseHandler() {
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError("登录服务器错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Gson gson = new Gson();
                    AuthResponse response = gson.fromJson(responseString, AuthResponse.class);
                    callback.onSuccess(response);
                } catch (Exception ex) {
                    callback.onError("登录服务器错误2");
                }
            }
        });
    }

    /**
     * 获取联系人列表
     */
    public void getContacts(IAccountDataSource.LoadContactCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AuthApi.contacts(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Gson gson = new Gson();
                        //获取用户列表
                        ArrayList<Account> contacts = gson.fromJson(responseString, new TypeToken<ArrayList<Account>>() {
                        }.getType());
                        ContactDao.asyncReplaceAll(contacts, AppContext.getInstance().getUser().getId());
                    }
                });
            }
        }).run();
    }
}
