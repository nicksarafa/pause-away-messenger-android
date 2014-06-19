package com.pauselabs.pause.models;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@Config(emulateSdk = 18) // Robolectric doesn't support API 19 yet
@RunWith(RobolectricTestRunner.class)
public class PauseMessageRobolectricTest {

    private static final String TAG = PauseMessageRobolectricTest.class.getSimpleName();

    static {
        // redirect the Log.x output to stdout.  Stdout will be recorded in the test results report
        ShadowLog.stream = System.out;
    }


    /**
     * Test if we can create a PauseMessage
     */
    @Test
    public void testCreate() {
        String sender = "12482526955";
        String text = "test message";

        PauseMessage message = new PauseMessage(sender, text);

        Assert.assertEquals(message.getSender(), sender);
        Assert.assertEquals(message.getText(), text);
    }


}
