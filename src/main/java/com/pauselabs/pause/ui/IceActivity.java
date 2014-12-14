package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;

import com.pauselabs.R;

/**
 * Created by Admin on 12/13/14.
 */
public class IceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ice_activity);

        //Views.inject(this);

       // button = (Button) findViewById(R.id.ice_button);

    }
}
