package com.huijimuhe.monolog.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.GuidePagerAdapter;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class GuideActivity extends AbstractActivity {
    private CirclePageIndicator mIndicator;
    private ViewPager mPager;
    private GuidePagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                GuideActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);
        initFragments();
    }

    private void initFragments() {
        mFragments.add(GuideFragment.newInstance(R.drawable.guide1, GuideFragment.TYPE_IMAGE_ONLY));
        mFragments.add(GuideFragment.newInstance(R.drawable.guide2, GuideFragment.TYPE_IMAGE_ONLY));
        mFragments.add(GuideFragment.newInstance(R.drawable.guide3, GuideFragment.TYPE_IMAGE_ONLY));
        mFragments.add(GuideFragment.newInstance(R.drawable.guide4, GuideFragment.TYPE_IMAGE_ONLY));
        mFragments.add(GuideFragment.newInstance(R.drawable.guide5, GuideFragment.TYPE_WITH_BUTTON));
        //notify
        mAdapter = new GuidePagerAdapter(getSupportFragmentManager(), mFragments);
        mAdapter.notifyDataSetChanged();
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
