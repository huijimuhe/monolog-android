package com.huijimuhe.monolog.network;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BaseClient {

    public static final String URL_USER_ABOUT = "http://monolog.huijimuhe.com";
    public static final String URL_USER_AGREEMENT = "http://monolog.huijimuhe.com/agreement";

    public static final String URL_BASE = "http://monolog.huijimuhe.com/api/";
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
    public static final String URL_POST_OPEN = "open";
    public static final String URL_POST_SIGN_OUT = "signout";
    public static final String URL_POST_AVATAR = "avatar";
    public static final String URL_POST_CHANGE_PROFILE = "changeprofile";
    public static final String URL_POST_CHANGE_PWD = "changepwd";
    public static final String URL_POST_REPORT = "report";

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
