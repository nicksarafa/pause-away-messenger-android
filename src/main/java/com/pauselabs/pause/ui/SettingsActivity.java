package com.pauselabs.pause.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import butterknife.Views;
import com.pauselabs.R;


public class SettingsActivity extends PauseFragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        // View injection with Butterknife
        Views.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
