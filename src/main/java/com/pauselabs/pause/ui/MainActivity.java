package com.pauselabs.pause.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import butterknife.Views;
import com.pauselabs.R;

public class MainActivity extends PauseFragmentActivity {

    private static final int SPLASH = 0;
    private static final int CREATE_PAUSE = 1;
    private static final int FRAGMENT_COUNT = CREATE_PAUSE + 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private CharSequence drawerTitle;
    private CharSequence title;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        // View injection with Butterknife
        Views.inject(this);

        // Set up navigation drawer

        // init main screen
        initScreen();

    }

    private void initScreen() {
        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[CREATE_PAUSE] = fm.findFragmentById(R.id.createPauseFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0 ; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        // show default screen
        showFragment(CREATE_PAUSE, false);
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



}
