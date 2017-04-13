package com.pauselabs.pause.view.start.onboaring.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.IconButton;
import butterknife.InjectView;
import com.pauselabs.R;
import com.pauselabs.pause.view.start.onboaring.OnboardingContentView;

/** Created by Passa on 3/12/15. */
public class OB3 extends OnboardingContentView {

  @InjectView(R.id.ob_final_start_main_activity_btn)
  public IconButton startAppBtn;

  public OB3(Context context) {
    super(context);
  }

  public OB3(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public OB3(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
