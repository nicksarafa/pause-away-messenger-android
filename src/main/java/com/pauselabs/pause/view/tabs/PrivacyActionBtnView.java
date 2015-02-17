package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 2/4/15.
 */
public class PrivacyActionBtnView extends RelativeLayout {

    @InjectView(R.id.ice_atn_1)
    public SearchView atnBtn1;

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);

    }

    public PrivacyActionBtnView(Context context) {
        super(context);
    }

    public PrivacyActionBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrivacyActionBtnView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}