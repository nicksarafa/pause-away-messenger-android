package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.IconTextView;
import android.widget.LinearLayout;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Passa on 3/20/15. */
public class UpgradeListItem extends LinearLayout {

  @InjectView(R.id.upgrade_list_item_icon_text)
  public IconTextView iconText;

  public UpgradeListItem(Context context) {
    super(context);
  }

  public UpgradeListItem(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public UpgradeListItem(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
