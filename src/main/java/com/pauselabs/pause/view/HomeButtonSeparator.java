package com.pauselabs.pause.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.pauselabs.R;

/**
 * Created by Admin on 12/20/14.
 */
public class HomeButtonSeparator extends RelativeLayout {

    View separator;

    public HomeButtonSeparator(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ob_button_separator, this, true);

        separator = (View) findViewById(R.id.home_button_separator);
    }

}
