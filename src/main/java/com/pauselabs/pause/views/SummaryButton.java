package com.pauselabs.pause.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;

/**
 * Created by Passa on 12/23/14.
 */
public class SummaryButton extends RelativeLayout {

    private ImageView icon;
    private TextView nameLabel;

    private Image image;
    private String name;

    public SummaryButton(Context context) {
        super(context);

        int defaultPadding = (int) getResources().getDimension(R.dimen.default_padding);
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_button_view, this, true);


    }

    public void setNameLabel(String name) {
        this.name = name;
        nameLabel.setText(name);
    }

    public void setIcon(Image image) {
        this.image = image;

    }

}
