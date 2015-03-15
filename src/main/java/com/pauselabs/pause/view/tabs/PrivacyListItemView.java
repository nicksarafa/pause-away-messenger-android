package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 3/13/15.
 */
public class PrivacyListItemView extends LinearLayout {

    @InjectView(R.id.privacy_list_item)
    public TextView privacyListItemContactName;

    public String contactId;

    public PrivacyListItemView(Context context) {
        super(context);
    }

    public PrivacyListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrivacyListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
