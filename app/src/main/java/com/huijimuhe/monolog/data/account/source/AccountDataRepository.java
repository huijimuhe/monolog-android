package com.huijimuhe.monolog.data.account.source;

import com.easemob.EMCallBack;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.data.account.AuthResponse;
import com.huijimuhe.monolog.domain.EaseMobService;
import com.huijimuhe.monolog.domain.PrefManager;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * Created by Huijimuhe on 2016/6/3.
 * This is a part of Monolog
 * enjoy
 */
public class AccountDataRepository {
    private AccountRemoteDataSource mAccountRemoteDataSource;
    private AccountLocalDataSource mAccountLocalDataSource;

    public AccountDataRepository(AccountRemoteDataSource remoteDataSource, AccountLocalDataSource localDataSource) {
        mAccountRemoteDataSource = remoteDataSource;
        mAccountLocalDataSource = localDataSource;
    }

    public AccountDataRepository(AccountLocalDataSource localDataSource) {
        mAccountRemoteDataSource = null;
        mAccountLocalDataSource = localDataSource;
    }

    /**
     * 登录
     * @param platform
     * @param callback
     */
    public void signIn(final SHARE_MEDIA platform, final IAccountDataSource.SignInCallback callback) {
        mAccountRemoteDataSource.signIn(platform, new IAccountDataSource.SignInCallback() {
            @Override
            public void onSuccess(final AuthResponse response) {
                //保存用户信息
                mAccountLocalDataSource.saveAccount(response);

                //获取联系人资料
                mAccountRemoteDataSource.getContacts(new IAccountDataSource.LoadContactCallback() {
                    @Override
                    public void onLoaded(List<Account> accounts) {
                        //更新数据库
                        asyncReplaceAll(accounts, response.getUser().getId());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

                //easemob 登录
                EaseMobService.getInstance().easeMobLogin(response.getUser(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }
                });

                callback.onSuccess(response);
            }

            @Override
            public void onError(String msg) {
                callback.onError(msg);
            }
        });
    }

    /**
     * 更新联系人数据库
     * @param list
     * @param ownerid
     */
    private void asyncReplaceAll(final List<Account> list, final String ownerid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAccountLocalDataSource.deleteAll(ownerid);
                    mAccountLocalDataSource.insertList(list, ownerid);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }


    public void increaseGuessCount(boolean isRight){
        //计数
        Account owner = PrefManager.getInstance().getUser();
        if(isRight) {
            owner.setRight_count(owner.getRight_count() + 1);
        }else{
            owner.setMiss_count(owner.getMiss_count() + 1);
        }
        PrefManager.getInstance().setUser(owner);

        //添加联系人
        //  ContactDao.addContact(guessedUser, owner.getUId());
    }
}
