package com.huijimuhe.monolog.data.statue.source;

import com.huijimuhe.monolog.data.statue.GuessResponse;
import com.huijimuhe.monolog.data.statue.IndexResponse;
import com.huijimuhe.monolog.data.statue.Statue;

import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class IStatueDataSource {

    public interface LoadCallback{
        void onStart();
        void onSuccess(IndexResponse data);
        void onError(String msg);
        void onOverDue();
    }

    public interface LoadPageCallback{
        void onSuccess(List<Statue> data);
        void onError(String msg);
    }
    public interface ReportCallback{
        void onSuccess();
        void onFailed(String msg);
    }

    public  interface GuessCallback{
        void onRight(GuessResponse response);
        void onWrong();
        void onError(String msg);
    }
}
