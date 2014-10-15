package com.pauselabs.pause.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;

import com.pauselabs.R;

import butterknife.Views;


public class SettingsActivity extends PauseFragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        // View injection with Butterknife
        Views.inject(this);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(Html.fromHtml("<font color=\"black\">Settings</font>"));
        ab.setDisplayUseLogoEnabled(false);

    }

}
