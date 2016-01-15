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
public class OB1 extends OnboardingContentView {

    @InjectView(R.id.slide1_activate_btn)
    public ButtonFloat activateBtn;

    public OB1(Context context) {
        super(context);
    }

    public OB1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OB1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
