package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import butterknife.Views;

/** Created by Admin on 3/8/15. */
public class TimeBankView extends RelativeLayout {

  public TimeBankView(Context context) {
    super(context);
  }

  public TimeBankView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TimeBankView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
