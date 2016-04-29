package com.huijimuhe.monolog.ui.statue;

import android.content.Intent;
import android.os.Bundle;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractListActivity;

public class AccountStatueListActivity extends AbstractListActivity {

    public static final String RENDER_TYPE= "Type";
    public static final String ACCOUNT= "Account";
    private int mType;
    private  UserBean mUser;

    public static Intent newIntent(int type,UserBean user) {
        Intent intent = new Intent(AppContext.getInstance(),
                AccountStatueListActivity.class);
        intent.putExtra(RENDER_TYPE,type);
        intent.putExtra(ACCOUNT, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser=getIntent().getParcelableExtra(ACCOUNT);
        mType = getIntent().getIntExtra(RENDER_TYPE, 1);
        initUI();
    }

    private void initUI() {
        //fragment container
        if (getSupportFragmentManager().findFragmentByTag(
                AccountStatueListFragment.class.getName()) == null) {
            AccountStatueListFragment fragment = AccountStatueListFragment
                    .newInstance(mType,mUser);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment,
                            AccountStatueListFragment.class.getName())
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    protected void setToolbarTitle() {
        switch (mType){
           case  StatueListAdapter.RENDER_TYPE_MISS:
                toolbar.setTitle("独白");
                break;
            case  StatueListAdapter.RENDER_TYPE_RIGHT:
                toolbar.setTitle("独白");
                break;
            case  StatueListAdapter.RENDER_TYPE_MY_PROFILE:
                toolbar.setTitle("独白");
                break;
            case  StatueListAdapter.RENDER_TYPE_USER_PROFILE:
                toolbar.setTitle("独白");
                break;
        }

    }
}
