package com.huijimuhe.monolog.ui.statue;

import android.content.Intent;
import android.os.Bundle;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractListActivity;

public class StatueListActivity extends AbstractListActivity {

    public static final String RENDER_TYPE= "Type";
    private int mType;

    public static Intent newIntent(int type) {
        Intent intent = new Intent(AppContext.getInstance(),
                StatueListActivity.class);
        intent.putExtra(RENDER_TYPE,type);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getIntExtra(RENDER_TYPE, 1);
        initUI();
    }

    private void initUI() {
        //fragment container
        if (getSupportFragmentManager().findFragmentByTag(
                StatueListFragment.class.getName()) == null) {
            StatueListFragment fragment = StatueListFragment
                    .newInstance(mType);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment,
                            StatueListFragment.class.getName())
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    protected void setToolbarTitle() {
        toolbar.setTitle("独白");
    }
}
