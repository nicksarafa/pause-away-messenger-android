package com.pauselabs.pause.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.pauselabs.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/27/15.
 */
public class PauseActivityView extends SlidingUpPanelLayout {

    @InjectView(R.id.main_content_container)
    public RelativeLayout mainContentContainer;
    @InjectView(R.id.toolbar_pager)
    public ViewPager viewPager;
    @InjectView(R.id.toolbar)
    public Toolbar toolbar;
    @InjectView(R.id.fab_image_button)
    public ButtonFloat startPauseButton;

    public PauseActivityView(Context context) {
        super(context);
    }

    public PauseActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PauseActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
