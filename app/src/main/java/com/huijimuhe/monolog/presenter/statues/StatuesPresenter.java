package com.huijimuhe.monolog.presenter.statues;

import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.account.source.AccountDataRepository;
import com.huijimuhe.monolog.data.account.source.AccountLocalDataSource;
import com.huijimuhe.monolog.data.statue.Statue;
import com.huijimuhe.monolog.data.statue.source.IStatueDataSource;
import com.huijimuhe.monolog.data.statue.source.StatueDataRepository;
import com.huijimuhe.monolog.data.statue.source.StatueLocalDataSource;
import com.huijimuhe.monolog.data.statue.source.StatueRemoteDataSource;

import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class StatuesPresenter implements StatueContract.Presenter {

    private StatueContract.View mView;
    private StatueDataRepository mStatueRepo;
    private AccountDataRepository mAccountRepo;
    private int mPage = 0;

    public StatuesPresenter(StatueContract.View view) {
        this.mView = view;
        this.mStatueRepo = new StatueDataRepository(StatueLocalDataSource.getInstance(), StatueRemoteDataSource.getInstance());
        this.mAccountRepo = new AccountDataRepository(AccountLocalDataSource.getInstance());
    }

    @Override
    public void loadPage(final boolean isNew, int listType, Account account) {
        if (isNew) {
            mPage = 0;
        }
        mView.showLoading(true);
        switch (listType) {
            case StatueContract.RENDER_TYPE_MISS:
                mStatueRepo.loadMissPage(String.valueOf(mPage), new IStatueDataSource.LoadPageCallback() {
                    @Override
                    public void onSuccess(List<Statue> data) {
                        mView.showLoading(false);
                        mView.showContent(data, isNew);
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showLoading(false);
                        mView.showToast(msg);
                    }
                });
                break;
            case StatueContract.RENDER_TYPE_RIGHT:
                mStatueRepo.loadRightPage(String.valueOf(mPage), new IStatueDataSource.LoadPageCallback() {
                    @Override
                    public void onSuccess(List<Statue> data) {
                        mView.showLoading(false);
                        mView.showContent(data, isNew);
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showLoading(false);
                        mView.showToast(msg);
                    }
                });
                break;
            case StatueContract.RENDER_TYPE_MY_PROFILE:
                mStatueRepo.loadMyPage(String.valueOf(mPage), new IStatueDataSource.LoadPageCallback() {
                    @Override
                    public void onSuccess(List<Statue> data) {
                        mView.showLoading(false);
                        mView.showContent(data, isNew);
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showLoading(false);
                        mView.showToast(msg);
                    }
                });
                break;
            case StatueContract.RENDER_TYPE_OTHER_PROFILE:
                mStatueRepo.loadOtherPage(account.getId(), String.valueOf(mPage), new IStatueDataSource.LoadPageCallback() {
                    @Override
                    public void onSuccess(List<Statue> data) {
                        mView.showLoading(false);
                        mView.showContent(data, isNew);
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showLoading(false);
                        mView.showToast(msg);
                    }
                });
        }
    }

    @Override
    public void delete(final Statue item) {
        mStatueRepo.delete(item.getId(), new IStatueDataSource.ReportCallback() {
            @Override
            public void onSuccess() {
                mView.showToast("删除成功");
                mView.removeItem(item);
            }

            @Override
            public void onFailed(String msg) {
                mView.showToast("网络错误");
            }
        });
    }

    @Override
    public void report(String sid) {
        mStatueRepo.report(sid, new IStatueDataSource.ReportCallback() {
            @Override
            public void onSuccess() {
                mView.showToast("举报成功,我们将即时处理!\n谢谢您的支持");
            }

            @Override
            public void onFailed(String msg) {
                mView.showToast("网络错误");
            }
        });
    }

    @Override
    public void start() {
        mView.showEmpty();
    }
}
