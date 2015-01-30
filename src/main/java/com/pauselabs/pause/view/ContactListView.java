package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/30/15.
 */
public class ContactListView extends LinearLayout {

    @InjectView(R.id.contact_list_view)
    public ListView listView;

    public ContactListView(Context context) {
        super(context);
    }

    public ContactListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
