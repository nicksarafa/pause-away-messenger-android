package com.pauselabs.pause.ui;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.events.PauseSessionChangedEvent;
import com.pauselabs.pause.util.UIUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class MainActivity extends ActivityGroup implements TabHost.OnTabChangeListener {

    private final String TAG = MainActivity.class.getSimpleName();

    @Inject
    protected Bus mBus;

    private TabHost tabhost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        tabhost = (TabHost) findViewById(R.id.tabhost);
        tabhost.setCurrentTab(1);
       // tabhost.getChildAt(1).setVisibility(View.GONE);
        tabhost.setOnTabChangedListener(this);
        tabhost.setup(getLocalActivityManager());

        TabHost.TabSpec spec_a, spec_b, spec_c;

        spec_a = tabhost.newTabSpec("privacy");
        spec_a.setContent(new Intent(this,PrivacyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        spec_a.setIndicator("",getResources().getDrawable(R.drawable.past_tab_selector));
        tabhost.addTab(spec_a);

        spec_b = tabhost.newTabSpec("home");
        spec_b.setContent(new Intent(this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        spec_b.setIndicator("",getResources().getDrawable(R.drawable.home_tab_selector));
        tabhost.addTab(spec_b);

        spec_c = tabhost.newTabSpec("settings");
        spec_c.setContent(new Intent(this,SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        spec_c.setIndicator("",getResources().getDrawable(R.drawable.settings_tab_selector));
        tabhost.addTab(spec_c);
    }



    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    /**
     * Handle all Pause session start/stop logic here through the event bus
     */
    @Subscribe
    public void onPauseSessionChangedEvent(PauseSessionChangedEvent event) {

        Log.i(TAG,"Pause Session Changed Event");
        switch(event.getSessionState()) {
            case Constants.Pause.PAUSE_SESSION_STATE_ACTIVE:
                // start Pause Service
                PauseApplication.startPauseService(Constants.Session.Creator.CUSTOM);
                Log.i(TAG,"onPauseSessionChangedEvent");
                // update Scoreboard Fragment
//                ScoreboardFragment scoreboardFragment = (ScoreboardFragment)fragments[SCOREBOARD];
//                scoreboardFragment.updateScoreboardUI();
                // display Pause Scoreboard Fragment
//                showFragment(SCOREBOARD, false);
                break;
            case Constants.Pause.PAUSE_SESSION_STATE_STOPPED:
                // stop Pause Service
                PauseApplication.stopPauseService(Constants.Session.Destroyer.CUSTOM);
                // display Create Pause Fragment
//                showFragment(CREATE_PAUSE, false);
                break;
        }
    }


    @Override
    public void onTabChanged(String tabId) {

    }
}
