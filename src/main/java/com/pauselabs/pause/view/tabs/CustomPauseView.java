package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/23/14.
 */
public class CustomPauseView extends RelativeLayout {

    @InjectView(R.id.custom_text)
    public TextView customTxtView;

    public CustomPauseView(Context context) {
        super(context);
    }

    public CustomPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPauseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
