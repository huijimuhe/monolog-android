package com.huijimuhe.monolog.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/2.
 */
public class GuidePagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;

    public GuidePagerAdapter(FragmentManager fm, ArrayList<Fragment> fs) {
        super(fm);
        fragments = fs;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
