package com.pauselabs.pause.view.start.onboaring;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;

/** Created by Passa on 3/12/15. */
public class OnboardingContainerView extends LinearLayout {

  @InjectView(R.id.onboarding_container_pager)
  public ViewPager viewPager;

  public OnboardingContainerView(Context context) {
    super(context);
  }

  public OnboardingContainerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public OnboardingContainerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    Views.inject(this);
  }
}
