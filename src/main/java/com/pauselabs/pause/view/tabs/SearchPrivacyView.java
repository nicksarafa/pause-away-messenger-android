package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Admin on 12/13/14. */
public class SearchPrivacyView extends RelativeLayout {

  @InjectView(R.id.contact_search_field)
  public SearchView contactSearchField;

  @InjectView(R.id.privacySelectAll)
  public Button selectAllBtn;

  @InjectView(R.id.contact_list)
  public ListView contactList;

  public SearchPrivacyView(Context context) {
    super(context);
  }

  public SearchPrivacyView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SearchPrivacyView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
