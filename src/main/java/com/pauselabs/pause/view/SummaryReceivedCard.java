package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/26/14.
 */
public class SummaryReceivedCard extends LinearLayout {

    @InjectView(R.id.messageText)
    TextView messageText;
    @InjectView(R.id.messageType)
    ImageView messageType;
    @InjectView(R.id.respondReceipt)
    ImageView respondReceipt;

    public SummaryReceivedCard(Context context) {
        super(context);
    }

    public SummaryReceivedCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryReceivedCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

    public void setMessageText(String text) {
        messageText.setText(text);
    }

}
