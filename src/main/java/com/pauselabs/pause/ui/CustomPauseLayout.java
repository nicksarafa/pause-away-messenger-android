package com.pauselabs.pause.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Passa on 12/23/14.
 */
public class CustomPauseLayout extends RelativeLayout {

    private Context context;

    public CustomPauseLayout(Context context) {
        super(context);

        this.context = context;
    }

    public CustomPauseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public CustomPauseLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
    }


    @Override
    protected void onFinishInflate() {

    }

}
