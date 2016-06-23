package com.huijimuhe.monolog.presenter.auth;

import android.app.Activity;

import com.huijimuhe.monolog.data.account.AuthResponse;
import com.huijimuhe.monolog.data.account.source.AccountDataRepository;
import com.huijimuhe.monolog.data.account.source.IAccountDataSource;
import com.huijimuhe.monolog.data.account.source.AccountLocalDataSource;
import com.huijimuhe.monolog.data.account.source.AccountRemoteDataSource;
import com.huijimuhe.monolog.domain.PrefManager;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Huijimuhe on 2016/6/2.
 * This is a part of Monolog
 * enjoy
 */
public class SignInPresenter implements SignInContract.Presenter {

    private SignInContract.View mView;
    private AccountDataRepository mRepo;

    public SignInPresenter(SignInContract.View view, UMShareAPI umShareAPI) {
        this.mView = view;
        this.mRepo = new AccountDataRepository(AccountRemoteDataSource.getInstance((Activity) view, umShareAPI), AccountLocalDataSource.getInstance());
    }

    @Override
    public void start() {

    }

    @Override
    public void postSignIn(final SHARE_MEDIA platform) {
        mView.showProgressDialog("开始登录");
        mRepo.signIn(platform, new IAccountDataSource.SignInCallback() {
            @Override
            public void onSuccess(AuthResponse account) {
                if(PrefManager.getInstance().isInstalled()) {
                    mView.showIndexOrGuide(false);
                }else{
                    mView.showIndexOrGuide(true);
                    PrefManager.getInstance().setInstalled();
                }
            }

            @Override
            public void onError(String msg) {
                mView.showError(msg);
            }
        });
    }

//    private class AuthHandler extends Handler {
//
//        WeakReference<AccountDataRepository> weakRepository;
//        WeakReference<SignInContract.View> weakView;
//
//        public AuthHandler(AccountDataRepository repository, SignInContract.View view) {
//            weakRepository = new WeakReference<>(repository);
//            weakView = new WeakReference<>(view);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case AUTH_REQUEST_START:
//                    weakDatasource.get().postSignIn(SHARE_MEDIA.WEIXIN);
//                    break;
//                case AUTH_REQUEST_INFO:
//                    weakDatasource.get().getSocialInfo(SHARE_MEDIA.WEIXIN);
//                    break;
//                case AUTH_VERIFY_START:
//                    weakDatasource.get().postWeixinVerify(msg.getData());
//                case AUTH_VERIFY_FAILED:
//                    weakView.get().showToast("网络错误");
//                    break;
//                case AUTH_VERIFY_SUCCESS:
//
//                    if (PrefManager.getInstance().isInstalled()) {
//                        weakView.get().showIndexOrGuide(true);
//                        PrefManager.getInstance().setInstalled();
//                    } else {
//                        weakView.startActivity(MainActivity.newIntent());
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
}
