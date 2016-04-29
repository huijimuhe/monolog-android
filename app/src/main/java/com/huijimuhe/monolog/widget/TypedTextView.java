package com.huijimuhe.monolog.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/3/1.
 */
public class TypedTextView extends TextView {

    public TypedTextView(Context context) {
        super(context);
    }

    public TypedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/lisung.ttf"), style);
    }


}
