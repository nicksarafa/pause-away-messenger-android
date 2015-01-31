package com.pauselabs.pause.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.adapters.ContactsAdapter;

import javax.inject.Inject;

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

        setSupportActionBar((Toolbar)getLayoutInflater().inflate(R.layout.toolbar,null));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("Blacklist");
        ab.setDisplayUseLogoEnabled(false);
    }

    @Override
    public void onSelectionCleared() {

    }
}
