package com.pauselabs.pause.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.pauselabs.R;

/**
 * Created by tyndallm on 10/5/14.
 */
public class BlacklistActivity extends ActionBarActivity implements BlacklistFragment.OnContactsInteractionListener{

    private BlacklistFragment mContactDetailFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main content view. On smaller screen devices this is a single pane view with one
        // fragment. One larger screen devices this is a two pane view with two fragments.
        setContentView(R.layout.blacklist_activity);
    }

    @Override
    public void onSelectionCleared() {

    }
}
