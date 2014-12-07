package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;

import com.pauselabs.R;

import butterknife.Views;

/**
 * Created by Passa on 12/7/14.
 */
public class PrivacyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.privacy_activity);

        Views.inject(this);
    }
}
