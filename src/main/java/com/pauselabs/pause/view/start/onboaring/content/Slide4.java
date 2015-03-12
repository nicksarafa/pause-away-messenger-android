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
public class Slide4 extends OnboardingContentView {

    @InjectView(R.id.slide4_start_app_btn)
    public Button startAppBtn;

    public Slide4(Context context) {
        super(context);
    }

    public Slide4(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Slide4(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
}
