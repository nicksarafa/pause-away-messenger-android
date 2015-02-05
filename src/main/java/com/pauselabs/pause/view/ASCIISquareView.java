package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/28/15.
 */
public class ASCIISquareView extends FrameLayout {

    @InjectView(R.id.emoji)
    public TextView ascii;

    @InjectView(R.id.emojiText)
    public TextView asciiText;

    public ASCIISquareView(Context context) {
        super(context);
    }

    public ASCIISquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ASCIISquareView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}