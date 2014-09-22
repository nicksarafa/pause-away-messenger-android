package com.pauselabs.pause.ui;

import android.os.Bundle;
import com.pauselabs.R;

/**
 * Created by tyndallm on 7/23/14.
 */
public class ScoreboardActivity extends PauseFragmentActivity{

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.scoreboard_activity);

    }

    @Override
    public void onBackPressed() {
        // Override back so that a session must be ended properly before going back to create screen
        super.onBackPressed();
        finish();
    }

}
