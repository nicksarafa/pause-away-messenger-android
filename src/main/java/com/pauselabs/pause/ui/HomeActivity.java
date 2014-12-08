package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;

import com.pauselabs.R;

import butterknife.Views;

/**
 * Created by Sarafa on 12/8/14.
 */

//TODO Can't get this to work properly despite it being the exact same as Settings Activity

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        Views.inject(this);
    }
}
