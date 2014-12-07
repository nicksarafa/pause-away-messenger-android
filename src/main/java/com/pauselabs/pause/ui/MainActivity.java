package com.pauselabs.pause.ui;

import android.app.Activity;
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

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Inject
    protected Bus mBus;

    private TabHost tabhost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        tabhost = (TabHost) findViewById(R.id.tabhost);
        tabhost.setup();

        TabHost.TabSpec spec_a, spec_b, spec_c;

        spec_a = tabhost.newTabSpec("spec_a");
        spec_a.setContent(R.id.tab_a);
        spec_a.setIndicator("tab_a");
        tabhost.addTab(spec_a);

        spec_b = tabhost.newTabSpec("spec_b");
        spec_b.setContent(R.id.tab_b);
        spec_b.setIndicator("tab_b");
        tabhost.addTab(spec_b);

        spec_c = tabhost.newTabSpec("spec_c");
        spec_c.setContent(R.id.tab_c);
        spec_c.setIndicator("tab_c");
        tabhost.addTab(spec_c);



        //Kill Tab Divider Lines
        //tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        //Past Tab (First Tab User Sees)
        //TabHost.TabSpec tabSpec = tabHost.newTabSpec("past");
        //tabSpec.setContent(R.id.tab_a);
        //tabSpec.setIndicator("",getResources().getDrawable(R.drawable.past_tab_selector));
        //tabHost.addTab(tabSpec);

        //Home Tab
        //tabSpec = tabHost.newTabSpec("home");
        //tabSpec.setContent(R.id.tab_b);
        //tabSpec.setIndicator("",getResources().getDrawable(R.drawable.home_tab_selector));
        //tabHost.addTab(tabSpec);

        //Settings Tab
        //tabSpec = tabHost.newTabSpec("settings");
        //tabSpec.setContent(R.id.tab_c);
        // tabSpec.setIndicator("",getResources().getDrawable(R.drawable.settings_tab_selector));
        //tabHost.addTab(tabSpec);

        // View injection with Butterknife
        // Views.inject(this);
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



}
