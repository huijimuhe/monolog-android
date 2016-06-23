package com.huijimuhe.monolog.data.statue.source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huijimuhe.monolog.data.statue.GuessResponse;
import com.huijimuhe.monolog.data.statue.IndexResponse;
import com.huijimuhe.monolog.data.statue.StatueListResponse;
import com.huijimuhe.monolog.network.StatueApi;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class StatueRemoteDataSource {

    private static StatueRemoteDataSource INSTANCE;

    // Prevent direct instantiation.
    private StatueRemoteDataSource() {
    }

    public static StatueRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatueRemoteDataSource();
        }
        return INSTANCE;
    }

    public void loadOtherPage(String uid, String page, final IStatueDataSource.LoadPageCallback callback) {
        StatueApi.getUserStatues(uid, page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                StatueListResponse response = gson.fromJson(responseString, StatueListResponse.class);
                callback.onSuccess(response.getItems());
            }
        });
    }

    public void loadMyPage(String page, final IStatueDataSource.LoadPageCallback callback) {
        StatueApi.getMyStatues(page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                StatueListResponse response = gson.fromJson(responseString, StatueListResponse.class);
                callback.onSuccess(response.getItems());
            }
        });
    }

    public void loadMissPage(String page, final IStatueDataSource.LoadPageCallback callback) {
        StatueApi.getMyGuess(StatueApi.GUESS_MISS, page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                StatueListResponse response = gson.fromJson(responseString, StatueListResponse.class);
                callback.onSuccess(response.getItems());
            }
        });
    }

    public void loadRightPage(String page, final IStatueDataSource.LoadPageCallback callback) {
        StatueApi.getMyGuess(StatueApi.GUESS_RIGHT, page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                StatueListResponse response = gson.fromJson(responseString, StatueListResponse.class);
                callback.onSuccess(response.getItems());
            }
        });
    }

    public void loadGuess(double lng, double lat, final IStatueDataSource.LoadCallback callback) {
        StatueApi.getStatue(lng, lat, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Gson gson = new GsonBuilder().create();
                IndexResponse response = gson.fromJson(responseBody, IndexResponse.class);
                 callback.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                if (responseBody != null && responseBody.trim().equals("403")) {
                    callback.onOverDue();
                } else {
                    callback.onError("网络错误1");
                }
            }
        });
    }

    public void report(String sid, final IStatueDataSource.ReportCallback callback) {
        StatueApi.postReport(sid, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailed("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.onSuccess();
            }
        });
    }

    public void delete(String sid, final IStatueDataSource.ReportCallback callback) {
        StatueApi.postDeleteStatue(sid, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailed("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.onSuccess();
            }
        });
    }

    public void guess(String sid, String uid, final IStatueDataSource.GuessCallback callback) {
        StatueApi.postGuess(sid, uid, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new GsonBuilder().create();
                GuessResponse response = gson.fromJson(responseString, GuessResponse.class);
                if (response.getResult() == 1) {
                    callback.onRight(response);
                } else {
                    callback.onWrong();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onError("网络错误");
            }
        });
    }
}
