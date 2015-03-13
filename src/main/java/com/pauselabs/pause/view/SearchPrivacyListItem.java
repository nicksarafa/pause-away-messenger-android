package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 3/12/15.
 */
public class SearchPrivacyListItem extends LinearLayout {

    @InjectView(R.id.checkbox_ice)
    public CheckBox iceCheckbox;
    @InjectView(R.id.contact_name_field)
    public TextView contactNameField;
    @InjectView(R.id.checkbox_added)
    public CheckBox blackCheckbox;

    public String contactId;

    public SearchPrivacyListItem(Context context) {
        super(context);
    }

    public SearchPrivacyListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchPrivacyListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}