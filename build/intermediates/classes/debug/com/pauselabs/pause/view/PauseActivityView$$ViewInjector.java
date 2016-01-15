// Generated code from Butter Knife. Do not modify!
package com.pauselabs.pause.view;

import android.view.View;
import butterknife.Views.Finder;

public class PauseActivityView$$ViewInjector {
  public static void inject(Finder finder, com.pauselabs.pause.view.PauseActivityView target, Object source) {
    View view;
    view = finder.findById(source, 2131689661);
    target.mainContentContainer = (android.widget.RelativeLayout) view;
    view = finder.findById(source, 2131689741);
    target.viewPager = (android.support.v4.view.ViewPager) view;
    view = finder.findById(source, 2131689740);
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findById(source, 2131689662);
    target.startPauseButton = (com.gc.materialdesign.views.ButtonFloat) view;
  }
}
