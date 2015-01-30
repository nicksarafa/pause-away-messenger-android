package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/24/14.
 */
public class SummaryView extends RelativeLayout {

    @InjectView(R.id.summary_card_list_view)
    public ListView listView;
    @InjectView(R.id.summary_no_messages)
    public TextView noMessages;
    @InjectView(R.id.summary_view_container)
    public RelativeLayout fabContainer;
    @InjectView(R.id.fab_image_button)
    public ImageButton startPauseButton;

    private final String TAG = SummaryView.class.getSimpleName();

    public SummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SummaryView(Context context) {
        super(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
