package com.pauselabs.pause.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.pauselabs.R;

/**
 * Created by tyndallm on 10/5/14.
 */
public class PrivacylistActivity extends ActionBarActivity implements PrivacylistFragment.OnContactsInteractionListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main content view. On smaller screen devices this is a single pane view with one
        // fragment. One larger screen devices this is a two pane view with two fragments.

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setElevation(0);

        setContentView(R.layout.privacy_list_view);
        getSupportActionBar().setTitle("BLACKLIST");

    }

    @Override
    public void onSelectionCleared() {

    }
}
