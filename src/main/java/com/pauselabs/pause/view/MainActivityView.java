package com.pauselabs.pause.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/27/15.
 */
public class MainActivityView extends LinearLayout {

    @InjectView(R.id.pager)
    public ViewPager viewPager;
    @InjectView(R.id.toolbar)
    public Toolbar toolbar;

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
