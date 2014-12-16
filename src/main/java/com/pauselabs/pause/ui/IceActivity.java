package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.pauselabs.R;

import butterknife.Views;

/**
 * Created by Admin on 12/13/14.
 */
public class IceActivity extends Activity {

    Button addIceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ice_activity);

        addIceBtn = (Button) findViewById(R.id.add_ice_button);

        Views.inject(this);


    }
}
