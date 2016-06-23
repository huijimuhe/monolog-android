package com.huijimuhe.monolog.data.account.source;

import android.support.annotation.NonNull;

import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.account.AuthResponse;

import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public interface IAccountDataSource {

    interface SignInCallback {
        void onSuccess(AuthResponse response);
        void onError(String msg);
    }

    interface LoadContactCallback {
        void onLoaded(List<Account> accounts);
        void onError(String msg);
    }

}
