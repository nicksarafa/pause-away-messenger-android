package com.pauselabs.pause.view.start.onboaring;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.Views;

/**
 * Created by Passa on 3/12/15.
 */
public class OnboardingContentView extends RelativeLayout {

    public OnboardingContentView(Context context) {
        super(context);
    }

    public OnboardingContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnboardingContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
