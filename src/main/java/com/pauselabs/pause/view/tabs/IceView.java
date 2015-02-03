package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

;

/**
 * Created by Admin on 12/13/14.
 */
public class IceView extends RelativeLayout {

    @InjectView(R.id.ice_contacts_list)
    public ListView contactListView;
//    @InjectView(R.id.add_ice_button)
//    public Button addIceBtn;

    public IceView(Context context) {
        super(context);
    }

    public IceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
