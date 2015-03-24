package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.IconButton;
import android.widget.RelativeLayout;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 2/4/15.
 */
public class TimeBankActionBtnView extends RelativeLayout {

    @InjectView(R.id.time_bank_action_btn)
    public IconButton timeBankActionBtn;

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);

    }

    public TimeBankActionBtnView (Context context) {
        super(context);
    }

    public TimeBankActionBtnView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeBankActionBtnView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}