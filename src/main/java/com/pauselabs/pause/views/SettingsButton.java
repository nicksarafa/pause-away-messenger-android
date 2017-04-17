package com.pauselabs.pause.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pauselabs.R;

/** Created by tyndallm on 9/29/14. */
public class SettingsButton extends LinearLayout {

  private TextView btnLabel;
  private TextView btnContent;
  private String mLabel = "";
  private String mContent = "";

  public SettingsButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a =
        context.getTheme().obtainStyledAttributes(attrs, R.styleable.SettingsButton, 0, 0);

    try {
      mLabel = a.getString(R.styleable.SettingsButton_label);
      mContent = a.getString(R.styleable.SettingsButton_content);
    } finally {
      a.recycle();
    }

    setOrientation(LinearLayout.VERTICAL);
    int defaultPadding = (int) getResources().getDimension(R.dimen.default_padding);
    setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.setting_button_view, this, true);

    btnLabel = (TextView) findViewById(R.id.label);
    btnContent = (TextView) findViewById(R.id.content);
    btnLabel.setText(mLabel);
    btnContent.setText(mContent);
  }

  public void setLabel(String label) {
    btnLabel.setText(label);
    invalidate();
    requestLayout();
  }

  public void setContent(String content) {
    btnContent.setText(content);
    invalidate();
    requestLayout();
  }
}
