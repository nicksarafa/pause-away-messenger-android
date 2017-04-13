package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Passa on 3/13/15. */
public class PrivacyListItemView extends LinearLayout {

  @InjectView(R.id.privacy_list_item)
  public TextView privacyListItemContactName;

  @InjectView(R.id.privacy_contact_initials)
  public ImageView privacyContactInitials;

  public String contactId;

  public PrivacyListItemView(Context context) {
    super(context);
  }

  public PrivacyListItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PrivacyListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
