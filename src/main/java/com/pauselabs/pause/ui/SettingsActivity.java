package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;

import com.pauselabs.R;

import butterknife.Views;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        // View injection with Butterknife
        Views.inject(this);


    }

}
