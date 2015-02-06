package com.pauselabs.pause.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.pauselabs.R;

/**
 * Created by Passa on 12/18/14.
 */
public class HomeButton extends RelativeLayout {

    private Button button;

    public HomeButton(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_button_view, this, true);

        button = (Button) findViewById(R.id.home_button);
    }

    public Button getButton() {
        return button;
    }

}

