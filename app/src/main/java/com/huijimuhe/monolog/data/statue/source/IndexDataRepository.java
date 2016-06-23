package com.huijimuhe.monolog.data.statue.source;

import com.huijimuhe.monolog.data.statue.GuessResponse;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.domain.OnBaiduLBSListener;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class IndexDataRepository {
    private StatueLocalDataSource mLocal;
    private StatueRemoteDataSource mRemote;

    public IndexDataRepository(StatueLocalDataSource local, StatueRemoteDataSource remote) {
        this.mLocal = local;
        this.mRemote = remote;
    }

    public void load(final IStatueDataSource.LoadCallback callback) {
        //mRemote.loadGuess(new Random().nextInt(80)+50,new Random().nextInt(45)+ 5, callback);
        BaiduService.getInstance().StartLocation(new OnBaiduLBSListener() {
            @Override
            public void onStart() {
                callback.onStart();
            }

            @Override
            public void onFailed(int code) {
                callback.onError("定位错误");
            }

            @Override
            public void onComplete(int code, double lng, double lat, String addr) {
                mRemote.loadGuess(lng, lat, callback);
            }
        });
    }

    public void report(String sid, final IStatueDataSource.ReportCallback callback) {
        mRemote.report(sid, callback);
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
