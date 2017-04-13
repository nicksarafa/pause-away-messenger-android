package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Admin on 3/8/15. */
public class UpgradeView extends RelativeLayout {

  //    @InjectView(R.id.suggestion_btn)
  //    public IconButton suggestionBtn;
  @InjectView(R.id.upgrade_list)
  public ListView updateList;

  public UpgradeView(Context context) {
    super(context);
  }

  public UpgradeView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public UpgradeView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
