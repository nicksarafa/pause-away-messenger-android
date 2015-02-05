package com.pauselabs.pause.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pauselabs.R;

/**
 * Created by tyndallm on 9/29/14.
 */
public class SettingsButton extends LinearLayout {

    private TextView btnLabel;
    private TextView btnContent;
    private ImageView btnEndnail;
    private ImageView btnFrontnail;
    private String mLabel = "";
    private String mContent = "";
    private Integer mEndnail;
    private Integer mFrontnail;

    public SettingsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsButton, 0, 0);
        Drawable frontnail = a.getDrawable(R.styleable.SettingsButton_frontnail);
        Drawable endnail = a.getDrawable(R.styleable.SettingsButton_frontnail);

        if (frontnail != null || endnail != null) {
            btnEndnail.setBackground(endnail);
            btnFrontnail.setBackground(frontnail);
        }

        try {
            mLabel = a.getString(R.styleable.SettingsButton_label);
            mContent = a.getString(R.styleable.SettingsButton_content);
//            mEndnail = a.getResourceId(R.styleable.SettingsButton_endnail, 0);
//            mFrontnail = a.getResourceId(R.styleable.SettingsButton_frontnail, 0);
        } finally {
            a.recycle();
        }

       int minPadding = (int) getResources().getDimension(R.dimen.min_padding);
       setPadding(minPadding, minPadding, minPadding, minPadding);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_button_view, this, true);

        btnLabel = (TextView) findViewById(R.id.label);
        btnContent = (TextView) findViewById(R.id.content);
//        btnEndnail = (ImageView) findViewById(R.id.endnail);
//        btnFrontnail = (ImageView) findViewById(R.id.frontnail);
        btnLabel.setText(mLabel);
        btnContent.setText(mContent);
//        btnEndnail.setImageResource(mEndnail);
//        btnFrontnail.setImageResource(mFrontnail);
    }

    public void setLabel(String label) {
        btnLabel.setText(label);
        invalidate();
        requestLayout();
    }

    public void setContent(String content) {
        btnContent.setText(content);
        invalidate();
        requestLayout();
    }

    public void setEndnail(Drawable endnail) {
        btnEndnail.setImageDrawable(endnail);
        invalidate();
        requestLayout();
    }

    public void setFrontnail(Drawable frontnail) {
        btnEndnail.setImageDrawable(frontnail);
        invalidate();
        requestLayout();
    }

}
