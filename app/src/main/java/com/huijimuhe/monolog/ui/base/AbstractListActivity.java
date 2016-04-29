package com.huijimuhe.monolog.ui.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.Toast;

import com.huijimuhe.monolog.R;

public abstract class AbstractListActivity extends AbstractActivity {
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_container);
        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarTitle();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);
    }

    private long mExitTime = 0;

    protected abstract void setToolbarTitle();
}
