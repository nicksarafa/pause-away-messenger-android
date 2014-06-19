package com.pauselabs.pause.mms;


import android.content.Context;
import android.net.ConnectivityManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@Config(emulateSdk = 18) // Robolectric doesn't support API 19 yet
@RunWith(RobolectricTestRunner.class)
public class PauseNetworkConnectivityRobolectricTest {

    static {
        // redirect the Log.x output to stdout.  Stdout will be recorded in the test results report
        ShadowLog.stream = System.out;
    }

    private ConnectivityManager mCm;

    @Before
    protected void setup() {
        mCm = (ConnectivityManager) Robolectric.application.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    @Test
    public void testStartusingNetworkFeature() {
        Assert.assertTrue(mCm.getAllNetworkInfo().length >= 2);

        final int failureCode = -1;

        final int result = mCm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");

        // Check MMS Connection enabled, result should not be 0 since we haven't opened a connection
        Assert.assertTrue(0 != result);
    }

}
