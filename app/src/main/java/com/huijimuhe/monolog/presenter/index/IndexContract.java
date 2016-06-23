package com.huijimuhe.monolog.presenter.index;

import android.view.MenuItem;

import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.statue.IndexResponse;
import com.huijimuhe.monolog.presenter.BasePresenter;
import com.huijimuhe.monolog.presenter.BaseView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class IndexContract {

    public static final int GUESS_RESULT_SUCCESS = 0x1;
    public static final int GUESS_RESULT_FAILED = 0x1 << 1;
    public static final int GUESS_RESULT_NONE = 0x1 << 2;

    public static final int GUESS_PAGE = 0x2;
    public static final int SUCCESS_PAGE = 0x2<<1;
    public static final int FAILED_PAGE =  0x2<<2;
    public static final int NONE_PAGE =  0x2<<3;

    public interface View extends BaseView<Presenter> {

        void showStatue(IndexResponse response);

        void showLoading(boolean active);

        void showImgGallery(String imgPath);

        void showError(String msg);

        void showReport();

        void showChatListUI();

        void showChatUI(String uid);

        void showOverDue();

        void showAddStatusUI();

        void initFragments();

        void showResultPage(int tag, Account account);

        void showGuessPage(List<Account> accounts);

        void showSplashAd();

        void showUnRead(MenuItem item);
    }

    public interface Presenter extends BasePresenter {

        void addStatue();

        void showChatList();

        void guess(String uid);

        void loadStatue();

        void showImageGallery();

        void report();

        void share();

        void chat(String uid);

        void signOut();
    }

    public class GuessEvent{

        public final String sid;
        public final String uid;

        public GuessEvent(String sid,String uid){
            this.sid=sid;
            this.uid=uid;
        }
    }
}
