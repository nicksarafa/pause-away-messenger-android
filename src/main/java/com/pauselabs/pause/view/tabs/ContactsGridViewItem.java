package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 3/13/15.
 */
public class ContactsGridViewItem extends RelativeLayout {

    @InjectView(R.id.grid_name_field)
    public TextView nameField;

    public String contactId;

    public ContactsGridViewItem(Context context) {
        super(context);
    }

    public ContactsGridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsGridViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
