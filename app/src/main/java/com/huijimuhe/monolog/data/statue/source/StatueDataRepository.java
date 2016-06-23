package com.huijimuhe.monolog.data.statue.source;

import com.huijimuhe.monolog.data.statue.GuessResponse;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.OnBaiduLBSListener;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class StatueDataRepository {
    private StatueLocalDataSource mLocal;
    private StatueRemoteDataSource mRemote;

    public StatueDataRepository(StatueLocalDataSource local, StatueRemoteDataSource remote) {
        this.mLocal = local;
        this.mRemote = remote;
    }

    public void loadRightPage(String page, final IStatueDataSource.LoadPageCallback callback) {
        mRemote.loadRightPage(page, callback);
    }

    public void loadMissPage(String page, final IStatueDataSource.LoadPageCallback callback) {
        mRemote.loadMissPage(page, callback);
    }

    public void loadMyPage(String page, final IStatueDataSource.LoadPageCallback callback) {
        mRemote.loadMyPage(page, callback);
    }

    public void loadOtherPage(String uid, String page, final IStatueDataSource.LoadPageCallback callback) {
        mRemote.loadOtherPage(uid, page, callback);
    }

    public void report(String sid, final IStatueDataSource.ReportCallback callback) {
        mRemote.report(sid, callback);
    }

    public void delete(String sid, final IStatueDataSource.ReportCallback callback) {
        mRemote.delete(sid, callback);
    }

    public void guess(String sid, String uid, final IStatueDataSource.GuessCallback callback) {
        mRemote.guess(sid, uid, new IStatueDataSource.GuessCallback() {
            @Override
            public void onRight(GuessResponse response) {
                callback.onRight(response);
            }

            @Override
            public void onWrong() {
                callback.onWrong();
            }

            @Override
            public void onError(String msg) {
                callback.onError(msg);
            }
        });
    }
}
