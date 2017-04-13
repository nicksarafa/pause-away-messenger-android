package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.IconTextView;
import android.widget.RelativeLayout;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Passa on 1/28/15. */
public class SavesView extends RelativeLayout {

  @InjectView(R.id.savesTextView)
  public IconTextView savesTextView;

  public SavesView(Context context) {
    super(context);
  }

  public SavesView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SavesView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
