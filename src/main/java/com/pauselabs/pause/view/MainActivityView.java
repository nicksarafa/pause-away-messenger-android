package com.pauselabs.pause.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gc.materialdesign.views.Button;
import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/27/15.
 */
public class MainActivityView extends RelativeLayout {

    @InjectView(R.id.pager)
    public ViewPager viewPager;
    @InjectView(R.id.toolbar)
    public Toolbar toolbar;
    @InjectView(R.id.fab_image_button)
    public Button startPauseButton;

    public MainActivityView(Context context) {
        super(context);
    }

    public MainActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
