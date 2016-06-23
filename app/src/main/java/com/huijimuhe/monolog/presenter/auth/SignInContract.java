package com.huijimuhe.monolog.presenter.auth;

import com.huijimuhe.monolog.presenter.BasePresenter;
import com.huijimuhe.monolog.presenter.BaseView;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Huijimuhe on 2016/6/2.
 * This is a part of Monolog
 * enjoy
 */
public class SignInContract {

    public interface View extends BaseView<Presenter> {

        void disableViews(boolean active);

        void showIndexOrGuide(boolean isFirstTime);

        void showProgressDialog(String msg);

        void showError(String msg);
    }

    public interface Presenter extends BasePresenter {

        void postSignIn(final SHARE_MEDIA platform);
    }

}
