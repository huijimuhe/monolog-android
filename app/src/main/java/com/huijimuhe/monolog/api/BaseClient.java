package com.huijimuhe.monolog.api;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BaseClient {

    public static final String URL_DNS ="[YOURS]";
    public static final String URL_BASE = URL_DNS + "api/";
    public static final String URL_BASE_SINA = " https://api.weibo.com/2/";

    public static final String URL_GET_STATUE = "statue";
    public static final String URL_GET_MY_STATUES = "mystatues/";
    public static final String URL_GET_USER_STATUES = "userstatues/";
    public static final String URL_GET_MY_GUESS = "myguess/";
    public static final String URL_GET_OSS = "token";
    public static final String URL_GET_CONTACT = "contact";
    public static final String URL_GET_CONTACTS = "contacts";
    public static final String URL_POST_CREATE_STATUE = "statue/create";
    public static final String URL_POST_DELETE_STATUE = "statue/destory";
    public static final String URL_POST_GUESS = "guess";
//    public static final String URL_POST_SIGN_IN = "signin";
//    public static final String URL_POST_SIGN_UP = "signup";
    public static final String URL_POST_OPEN = "open";
    public static final String URL_POST_SIGN_OUT = "signout";
    public static final String URL_POST_AVATAR = "avatar";
    public static final String URL_POST_CHANGE_PROFILE = "changeprofile";
    public static final String URL_POST_CHANGE_PWD = "changepwd";
    public static final String URL_POST_REPORT = "report";

    public static final String URL_USER_ABOUT = URL_DNS;
    public static final String URL_USER_AGREEMENT = URL_BASE + "agreement";
    public static final String URL_GET_SINA_UID = URL_BASE_SINA + "account/get_uid.json";
    public static final String URL_GET_SINA_PROFILE = URL_BASE_SINA + "users/show.json";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (!AppContext.getInstance().isConnected()) {
            ToastUtils.show(AppContext.getInstance(), AppContext.getInstance().getString(R.string.error_no_network));
            return;
        }
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (!AppContext.getInstance().isConnected()) {
            ToastUtils.show(AppContext.getInstance(), AppContext.getInstance().getString(R.string.error_no_network));
            return;
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return URL_BASE + relativeUrl;
    }
}
