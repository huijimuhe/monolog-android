package com.huijimuhe.monolog.ui.statue;

import android.content.Intent;
import android.os.Bundle;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.StatueListAdapter;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractListActivity;

public class AccountStatueListActivity extends AbstractListActivity {

    public static final String RENDER_TYPE= "Type";
    public static final String ACCOUNT= "Account";
    private int mType;
    private Account mUser;

    public static Intent newIntent(int type,Account user) {
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
        toolbar.setTitle("独白");
    }
}
