package com.pauselabs.pause.ui;

import android.app.ActivityGroup;
import android.os.Bundle;
import android.widget.TabHost;

import com.pauselabs.R;
import com.pauselabs.pause.util.UIUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class MainActivity extends ActivityGroup {

    private final String TAG = MainActivity.class.getSimpleName();

    @Inject
    protected Bus mBus;

    private TabHost tabhost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        //Add External Font (Later)

//        TextView myTextView=(TextView)findViewById(R.id.textBox);
//        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/mytruetypefont.ttf");
//        myTextView.setTypeface(typeFace);

    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    /**
     * Handle all Pause session start/stop logic here through the event bus
     */
//    @Subscribe
//    public void onPauseSessionChangedEvent(PauseSessionChangedEvent event) {
//
//        Log.i(TAG,"Pause Session Changed Event");
//        switch(event.getSessionState()) {
//            case Constants.Pause.PAUSE_SESSION_STATE_ACTIVE:
//                // start Pause Service
//                PauseApplication.startPauseService(Constants.Session.Creator.CUSTOM);
//                Log.i(TAG,"onPauseSessionChangedEvent");
//                // update Scoreboard Fragment
////                ScoreboardFragment scoreboardFragment = (ScoreboardFragment)fragments[SCOREBOARD];
////                scoreboardFragment.updateScoreboardUI();
//                // display Pause Scoreboard Fragment
////                showFragment(SCOREBOARD, false);
//                break;
//            case Constants.Pause.PAUSE_SESSION_STATE_STOPPED:
//                // stop Pause Service
//                PauseApplication.stopPauseService(Constants.Session.Destroyer.CUSTOM);
//                // display Create Pause Fragment
////                showFragment(CREATE_PAUSE, false);
//                break;
//        }
//    }
}
