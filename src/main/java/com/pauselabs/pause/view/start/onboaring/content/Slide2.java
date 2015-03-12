package com.pauselabs.pause.view.start.onboaring.content;

import android.content.Context;
import android.util.AttributeSet;

import com.gc.materialdesign.views.ButtonFloat;
import com.pauselabs.R;
import com.pauselabs.pause.view.start.onboaring.OnboardingContentView;

import butterknife.InjectView;

/**
 * Created by Passa on 3/12/15.
 */
public class Slide2 extends OnboardingContentView {

    @InjectView(R.id.slide2_deactivate_btn)
    public ButtonFloat deactivateBtn;

    public Slide2(Context context) {
        super(context);
    }

    public Slide2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Slide2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
}
