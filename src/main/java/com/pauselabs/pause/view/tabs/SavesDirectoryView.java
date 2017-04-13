package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.IconButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Passa on 1/27/15. */
public class SavesDirectoryView extends RelativeLayout {

  @InjectView(R.id.save_pause_message_btn)
  public IconButton addCustomBtn;

  @InjectView(R.id.custom_text)
  public EditText customText;

  @InjectView(R.id.savesDirectoryGrid)
  public ListView savesGrid;

  public SavesDirectoryView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SavesDirectoryView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public SavesDirectoryView(Context context) {
    super(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
