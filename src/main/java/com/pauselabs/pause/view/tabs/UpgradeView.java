package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.IconButton;
import android.widget.RelativeLayout;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Admin on 3/8/15.
 */
public class UpgradeView extends RelativeLayout {

    @InjectView(R.id.upgrade_btn)
    public IconButton upgradeBtn;

    public UpgradeView(Context context) {
        super(context);

    }

    public UpgradeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpgradeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
