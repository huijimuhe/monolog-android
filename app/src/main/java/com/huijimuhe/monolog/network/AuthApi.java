package com.huijimuhe.monolog.network;

import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.network.BaseClient;
import com.huijimuhe.monolog.network.TextHttpResponseLoopHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class AuthApi {

    public static void open(String name, String openid, String gender, String avatar, String token, String type, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_OPEN;
        RequestParams params = new RequestParams();
        params.put("open_id", openid);
        params.put("token", token);
        params.put("type", type);
        params.put("name", name);
        params.put("gender", gender);
        params.put("avatar", avatar);
        BaseClient.post(url, params, responseHandler);
    }

    public static void signOut(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_SIGN_OUT;
        String token = AppContext.getInstance().getToken();
        RequestParams params = new RequestParams();
        params.put("token", token);
        BaseClient.post(url, params, responseHandler);
    }

    public static void changePorfile(String name, String avatar, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_CHANGE_PROFILE;
        String token = AppContext.getInstance().getToken();
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("name", name);
        params.put("avatar", avatar);
        BaseClient.post(url, params, responseHandler);
    }

    public static void changePwd(String password, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_CHANGE_PWD;
        String token = AppContext.getInstance().getToken();
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("password", password);
        BaseClient.post(url, params, responseHandler);
    }

    public static void postAvatar(String avatar, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_AVATAR;
        String token = AppContext.getInstance().getToken();
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("postAvatar", avatar);
        BaseClient.post(url, params, responseHandler);
    }

    public static void contact(String easeName,TextHttpResponseLoopHandler responseHandler) {
        String url = BaseClient.URL_GET_CONTACT;
        String token = AppContext.getInstance().getToken();
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("name", easeName);
        BaseClient.get(url, params, responseHandler);
    }

    public static void contacts(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_GET_CONTACTS;
        String token = AppContext.getInstance().getToken();
        RequestParams params = new RequestParams();
        params.put("token", token);
        BaseClient.get(url, params, responseHandler);
    }
}
