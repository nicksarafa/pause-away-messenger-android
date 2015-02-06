package com.pauselabs.pause.view.tabs;

import android.app.SearchableInfo;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

;

/**
 * Created by Admin on 12/13/14.
 */
public class PrivacyView extends LinearLayout {

    @InjectView(R.id.contact_search_field)
    public SearchView contactSearchField;
    @InjectView(R.id.privacySelectAll)
    public Button selectAllBtn;
    @InjectView(R.id.contact_list)
    public ListView contactList;

    public PrivacyView(Context context) {
        super(context);
    }

    public PrivacyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrivacyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
