package com.pauselabs.pause.view.start.onboaring.content;

import android.content.Context;
import android.util.AttributeSet;
import butterknife.InjectView;
import com.gc.materialdesign.views.ButtonFloat;
import com.pauselabs.R;
import com.pauselabs.pause.view.start.onboaring.OnboardingContentView;

/** Created by Passa on 3/12/15. */
public class OB2 extends OnboardingContentView {

  @InjectView(R.id.slide2_deactivate_btn)
  public ButtonFloat deactivateBtn;

  public OB2(Context context) {
    super(context);
  }

  public OB2(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public OB2(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
