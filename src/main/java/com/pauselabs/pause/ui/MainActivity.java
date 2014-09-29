package com.pauselabs.pause.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.events.PauseSessionChangedEvent;
import com.pauselabs.pause.events.SavedPauseMessageSelectedEvent;
import com.pauselabs.pause.util.UIUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Views;

public class MainActivity extends PauseFragmentActivity {

    @Inject
    protected Bus mBus;

    private static final int SPLASH = 0;
    private static final int CREATE_PAUSE = 1;
//    private static final int SCOREBOARD = 2;
    private static final int FRAGMENT_COUNT = CREATE_PAUSE + 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_activity);

        // View injection with Butterknife
        Views.inject(this);

        // Set up navigation drawer
        title = drawerTitle = getTitle();


        if(!isTablet()) {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerToggle = new ActionBarDrawerToggle(
                    this,                    /* Host activity */
                    drawerLayout,           /* DrawerLayout object */
                    R.drawable.ic_drawer,    /* nav drawer icon to replace 'Up' caret */
                    R.string.navigation_drawer_open,    /* "open drawer" description */
                    R.string.navigation_drawer_close) { /* "close drawer" description */

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(title);
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(drawerTitle);
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(drawerToggle);

            navigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

            // Set up the drawer.
            navigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // init main screen
        initScreen();

    }

    private void initScreen() {
        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[CREATE_PAUSE] = fm.findFragmentById(R.id.createPauseFragment);
//        fragments[SCOREBOARD] = fm.findFragmentById(R.id.scoreboardFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0 ; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        // show default screen
        showFragment(CREATE_PAUSE, false);

        getActionBar().setDisplayShowTitleEnabled(false);
    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    /**
     * Responsible for showing a given fragment and hiding all others
     * @param fragmentIndex
     * @param addToBackStack
     */
    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            if(i == fragmentIndex) {
                transaction.show(fragments[i]);
            }
            else {
                transaction.hide(fragments[i]);
            }

        }
        if(addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PauseApplication.getCurrentSession() != null && PauseApplication.getCurrentSession().isActive()) {
            Intent scoreboardIntent = new Intent(this, ScoreboardActivity.class);
            startActivity(scoreboardIntent);
        }
        else{
            showFragment(CREATE_PAUSE, false);
        }
    }

    /**
     * Handle all saved message selection to update CreatePauseFragment
     */
    @Subscribe
    public void onSavedPauseSelectedEvent(SavedPauseMessageSelectedEvent event) {
        CreatePauseFragment createPauseFragment = (CreatePauseFragment) fragments[CREATE_PAUSE];
        createPauseFragment.savedPauseMessageSelected(event.getSavedMessageId());
    }



    /**
     * Handle all Pause session start/stop logic here through the event bus
     */
    @Subscribe
    public void onPauseSessionChangedEvent(PauseSessionChangedEvent event) {
        switch(event.getSessionState()) {
            case Constants.Pause.PAUSE_SESSION_STATE_ACTIVE:
                // start Pause Service
                PauseApplication.startPauseService();
                // update Scoreboard Fragment
//                ScoreboardFragment scoreboardFragment = (ScoreboardFragment)fragments[SCOREBOARD];
//                scoreboardFragment.updateScoreboardUI();
                // display Pause Scoreboard Fragment
//                showFragment(SCOREBOARD, false);
                break;
            case Constants.Pause.PAUSE_SESSION_STATE_STOPPED:
                // stop Pause Service
                PauseApplication.stopPauseService();
                // display Create Pause Fragment
                showFragment(CREATE_PAUSE, false);
                break;
        }
    }



}
