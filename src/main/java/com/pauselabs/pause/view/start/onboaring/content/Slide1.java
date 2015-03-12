package com.pauselabs.pause.view.start.onboaring.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.pauselabs.R;
import com.pauselabs.pause.view.start.onboaring.OnboardingContentView;

import butterknife.InjectView;

/**
 * Created by Passa on 3/12/15.
 */
public class Slide1 extends OnboardingContentView {

    @InjectView(R.id.slide1_activate_btn)
    public Button activateBtn;

    public Slide1(Context context) {
        super(context);
    }

    public Slide1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Slide1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
