package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pauselabs.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/26/14.
 */
public class NoSessionView extends RelativeLayout {

    @InjectView(R.id.home_content)
    LinearLayout cotentView;
    @InjectView(R.id.settings_view)
    LinearLayout settingsView;

    public NoSessionView(Context context) {
        super(context);
    }

    public NoSessionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoSessionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
