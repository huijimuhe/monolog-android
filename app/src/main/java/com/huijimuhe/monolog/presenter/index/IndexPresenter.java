package com.huijimuhe.monolog.presenter.index;

import com.huijimuhe.monolog.data.account.source.AccountDataRepository;
import com.huijimuhe.monolog.data.account.source.AccountLocalDataSource;
import com.huijimuhe.monolog.data.statue.GuessResponse;
import com.huijimuhe.monolog.data.statue.IndexResponse;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.data.statue.source.IStatueDataSource;
import com.huijimuhe.monolog.data.statue.source.IndexDataRepository;
import com.huijimuhe.monolog.data.statue.source.StatueLocalDataSource;
import com.huijimuhe.monolog.data.statue.source.StatueRemoteDataSource;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class IndexPresenter implements IndexContract.Presenter {


    private IndexContract.View mView;
    private IndexDataRepository mStatueRepo;
    private AccountDataRepository mAccountRepo;
    private Statue mStatue;

    public IndexPresenter(IndexContract.View view) {
        this.mView = view;
        mStatueRepo=new IndexDataRepository(StatueLocalDataSource.getInstance(), StatueRemoteDataSource.getInstance());
        mAccountRepo=new AccountDataRepository(AccountLocalDataSource.getInstance());
    }

    @Override
    public void guess(String uid) {
        mView.showLoading(true);

        mStatueRepo.guess(mStatue.getId(), uid, new IStatueDataSource.GuessCallback() {
            @Override
            public void onRight(GuessResponse response) {
                mAccountRepo.increaseGuessCount(true);
                mView.showLoading(false);
                mView.showResultPage(IndexContract.GUESS_RESULT_SUCCESS, response.getUser());
                mView.showSplashAd();
            }

            @Override
            public void onWrong() {
                mAccountRepo.increaseGuessCount(false);
                mView.showLoading(false);
                mView.showResultPage(IndexContract.GUESS_RESULT_FAILED, null);
            }

            @Override
            public void onError(String msg) {
                mView.showLoading(false);
                mView.showResultPage(IndexContract.GUESS_RESULT_NONE, null);
            }
        });
    }

    @Override
    public void addStatue() {
        mView.showAddStatusUI();
    }

    @Override
    public void showChatList() {
        mView.showChatListUI();
    }

    @Override
    public void loadStatue() {
        mStatueRepo.load(new IStatueDataSource.LoadCallback() {
            @Override
            public void onStart() {
                mView.showLoading(true);
            }

            @Override
            public void onSuccess(IndexResponse response) {

                mStatue=response.getStatue();

                mView.showLoading(false);
                mView.showStatue(response);
                mView.showGuessPage(response.getUsers());
            }

            @Override
            public void onError(String msg) {
                mView.showLoading(false);
            }

            @Override
            public void onOverDue() {
                mView.showLoading(false);
            }
        });
    }

    @Override
    public void share() {

    }

    @Override
    public void showImageGallery() {
        if(mStatue!=null) {
            mView.showImgGallery(mStatue.getImg_path());
        }
    }

    @Override
    public void report() {

    }

    @Override
    public void signOut() {

    }

    @Override
    public void chat(String uid) {

    }

    @Override
    public void start() {
        mView.initFragments();
        mView.showResultPage(IndexContract.GUESS_PAGE, null);
    }
}
