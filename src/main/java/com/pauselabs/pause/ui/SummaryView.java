package com.pauselabs.pause.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/24/14.
 */
public class SummaryView extends LinearLayout {

    @InjectView(R.id.summary_card_list_view)
    ListView listView;
    @InjectView(R.id.summary_no_messages)
    TextView noMessages;

    private final String TAG = SummaryView.class.getSimpleName();

    public SummaryView(Context context) {
        super(context);
    }

    public SummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
