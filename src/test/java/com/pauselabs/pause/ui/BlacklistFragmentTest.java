package com.pauselabs.pause.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by tyndallm on 10/19/14.
 */
@RunWith(RobolectricTestRunner.class)
public class BlacklistFragmentTest {

    BlacklistFragment blacklistFragment;

    /**
     * Helper function used for testing fragment independently of Activity
     * @param fragment
     */
    public static void startFragment(Fragment fragment) {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(fragment, null);
        transaction.commit();
    }

    @Before
    public void setUp() throws Exception
    {
        blacklistFragment = new BlacklistFragment();
        startFragment(blacklistFragment);
    }

    @Test
    public void testFragmentExists() throws Exception {
        assertTrue(blacklistFragment != null);
    }
}
