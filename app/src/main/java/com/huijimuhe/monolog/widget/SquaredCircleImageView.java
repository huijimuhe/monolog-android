package com.huijimuhe.monolog.widget;

import android.content.Context;
import android.util.AttributeSet;

/** An image view which always remains square with respect to its width. */
public class SquaredCircleImageView extends CircularImageView {
  public SquaredCircleImageView(Context context) {
    super(context);
  }

  public SquaredCircleImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
  }
}
