package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/28/15.
 */
public class InteractiveView extends RelativeLayout {

    @InjectView(R.id.interactive_pause_message)
    public TextView pauseMessage;
    @InjectView(R.id.interactive_button_layout)
    public LinearLayout buttonLayout;

    public InteractiveView(Context context) {
        super(context);
    }

    public InteractiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
