package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.pauselabs.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/27/15.
 */
public class ASCIIDirectoryView extends LinearLayout {

    @InjectView(R.id.emojiDirectoryGrid)
    public GridView asciiGrid;

    public ASCIIDirectoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ASCIIDirectoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ASCIIDirectoryView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
