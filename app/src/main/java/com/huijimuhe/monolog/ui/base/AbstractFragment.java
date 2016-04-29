package com.huijimuhe.monolog.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class AbstractFragment extends Fragment {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static int mState = STATE_NORMAL;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
}
