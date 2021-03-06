package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.IconButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Admin on 3/8/15. */
public class PrivacyView extends RelativeLayout {

  @InjectView(R.id.privacy_contacts_list)
  public ListView contactsList;

  @InjectView(R.id.emergency_tab_btn)
  public IconButton emergencyTabBtn;

  @InjectView(R.id.blacklist_tab_btn)
  public IconButton blacklistTabBtn;

  public PrivacyView(Context context) {
    super(context);
  }

  public PrivacyView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PrivacyView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
