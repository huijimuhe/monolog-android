package com.huijimuhe.monolog.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.adapter.GuidePagerAdapter;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractActivity;

import java.util.ArrayList;

public class GuideActivity extends AbstractActivity {
    static final int NUM_PAGES = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    boolean isOpaque = true;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                GuideActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new SceneAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pager!=null){
            pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles(){
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i = 0 ; i < NUM_PAGES ; i++){
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_circle_off);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index){
        if(index < NUM_PAGES){
            for(int i = 0 ; i < NUM_PAGES  ; i++){
                ImageView circle = (ImageView) circles.getChildAt(i);
                if(i == index){
                    circle.setImageResource(R.drawable.ic_circle_on);
                }else {
                    circle.setImageResource(R.drawable.ic_circle_off);
                }
            }
        }
    }
    private class SceneAdapter extends FragmentStatePagerAdapter {

        public SceneAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GuideFragment tp = null;
            switch(position){
                case 0:
                    tp = GuideFragment.newInstance(R.drawable.guide1,GuideFragment.TYPE_IMAGE_ONLY);
                    break;
                case 1:
                    tp = GuideFragment.newInstance(R.drawable.guide2,GuideFragment.TYPE_IMAGE_ONLY);
                    break;
                case 2:
                    tp = GuideFragment.newInstance(R.drawable.guide3,GuideFragment.TYPE_IMAGE_ONLY);
                    break;
                case 3:
                    tp = GuideFragment.newInstance(R.drawable.guide4,GuideFragment.TYPE_IMAGE_ONLY);
                    break;
                case 4:
                    tp = GuideFragment.newInstance(R.drawable.guide5,GuideFragment.TYPE_WITH_BUTTON);
                    break;
            }
            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }
}
