package com.pauselabs.pause.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;

/**
 * Created by tyndallm on 9/29/14.
 */
public class SettingsDialougeButton extends RelativeLayout {

    private TextView btnLabel;
    private TextView btnContent;
    private ImageView btnIcon;
    private ImageView btnThumbnail;
    private String mLabel = "";
    private String mContent = "";

    public SettingsDialougeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SettingsDialougeButton,
                0, 0);

        try {
            mLabel = a.getString(R.styleable.SettingsDialougeButton_label);
            mContent = a.getString(R.styleable.SettingsDialougeButton_content);
//            mIcon = a.getResources(R.styleable.SettingsButton_icon);
//            mThumbnail = a.getResources(R.styleable.SearchView);
        } finally {
            a.recycle();
        }

       int defaultPadding = (int) getResources().getDimension(R.dimen.default_padding);
       setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.setting_dialogue_button_view, this, true);

        btnLabel = (TextView) findViewById(R.id.label);
        btnContent = (TextView) findViewById(R.id.content);
        btnIcon = (ImageView) findViewById(R.id.icon);
        btnThumbnail = (ImageView) findViewById(R.id.thumbnail);
        btnLabel.setText(mLabel);
        btnContent.setText(mContent);
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

}
