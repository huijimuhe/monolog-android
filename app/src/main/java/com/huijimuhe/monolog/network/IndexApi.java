package com.huijimuhe.monolog.network;

import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.network.BaseClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class IndexApi {

    public static final String GUESS_RIGHT = "right";
    public static final String GUESS_MISS = "miss";

    public static void getStatue(double lng, double lat, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_GET_STATUE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("lng", lng);
        params.put("lat", lat);
        BaseClient.get(url, params, responseHandler);
    }


    public static void postGuess(String sid, String uid, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_POST_GUESS;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("sid", sid);
        params.put("uid", uid);
        BaseClient.post(url, params, responseHandler);
    }

    public static void postCreateStatue(String text, String img_path, String lng, String lat, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_POST_CREATE_STATUE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("text", text);
        params.put("img_path", img_path);
        params.put("lng", lng);
        params.put("lat", lat);
        BaseClient.post(url, params, responseHandler);
    }
    public static void postReport(String id, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_POST_REPORT;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("sid", id);
        BaseClient.post(url, params, responseHandler);
    }
    public static void postDeleteStatue(String id, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_POST_DELETE_STATUE;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("id", id);
        BaseClient.post(url, params, responseHandler);
    }

    public static void getOssToken(TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_GET_OSS;
        RequestParams params = new RequestParams();
        params.put("token", token);
        BaseClient.get(url, params, responseHandler);
    }

    public static void getMyGuess(String type, String page, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_GET_MY_GUESS + type + "/" + page;
        RequestParams params = new RequestParams();
        params.put("token", token);
        BaseClient.get(url, params, responseHandler);
    }
    public static void getUserStatues(String id,String page, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_GET_USER_STATUES + page;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("user_id",id);
        BaseClient.get(url, params, responseHandler);
    }
    public static void getMyStatues(String page, TextHttpResponseHandler responseHandler) {
        String token = AppContext.getInstance().getToken();
        String url = BaseClient.URL_GET_MY_STATUES + page;
        RequestParams params = new RequestParams();
        params.put("token", token);
        BaseClient.get(url, params, responseHandler);
    }
}
