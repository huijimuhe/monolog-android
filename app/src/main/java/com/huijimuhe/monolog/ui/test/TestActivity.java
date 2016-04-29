package com.huijimuhe.monolog.ui.test;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.huijimuhe.monolog.R;

public class TestActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                task t = new task(mSwipeRefreshLayout);
                t.execute();
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        Log.d("monolog", "刷新完成");
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 2000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
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

    private class task extends AsyncTask {
        SwipeRefreshLayout s;

        public task(SwipeRefreshLayout s) {
            super();
            this.s = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            TypedValue typed_value = new TypedValue();
//            TestActivity.this.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
//            s.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
s.setRefreshing(true);
//            s.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    s.setRefreshing(true);
//                }
//            }, 1000);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            s.setRefreshing(false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
