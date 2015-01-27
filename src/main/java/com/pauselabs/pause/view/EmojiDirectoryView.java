package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.Views;

/**
 * Created by Passa on 1/27/15.
 */
public class EmojiDirectoryView extends RelativeLayout {

    public EmojiDirectoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiDirectoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EmojiDirectoryView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
