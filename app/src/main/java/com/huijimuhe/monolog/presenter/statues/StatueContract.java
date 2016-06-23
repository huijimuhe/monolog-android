package com.huijimuhe.monolog.presenter.statues;

import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.presenter.BasePresenter;
import com.huijimuhe.monolog.presenter.BaseView;

import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class StatueContract {

    public static final int RENDER_TYPE_MISS = 1 << 1;
    public static final int RENDER_TYPE_RIGHT = 1 << 2;
    public static final int RENDER_TYPE_MY_PROFILE = 1 << 3;
    public static final int RENDER_TYPE_OTHER_PROFILE = 1 << 4;

    public interface View extends BaseView<Presenter> {
        void showLoading(boolean active);

        void showToast(String msg);

        void showContent(List<Statue> statues, boolean isNew);

        void showEmpty();

        void removeItem(Statue item);
    }

    public interface Presenter extends BasePresenter {

        void loadPage(boolean isNew, int listType, Account account);

        void delete(Statue item);

        void report(String sid);
    }
}
