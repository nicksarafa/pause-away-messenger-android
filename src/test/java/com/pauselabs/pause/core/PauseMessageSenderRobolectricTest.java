package com.pauselabs.pause.core;

import android.telephony.SmsManager;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowSmsManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Config(emulateSdk = 18) // Robolectric doesn't support API 19 yet
@RunWith(RobolectricTestRunner.class)
public class PauseMessageSenderRobolectricTest {

    private static final String TAG = PauseMessageSenderRobolectricTest.class.getSimpleName();

    static {
        // redirect the Log.x output to stdout.  Stdout will be recorded in the test results report
        ShadowLog.stream = System.out;
    }

    /**
     * Test if we can create a PauseMessageSender
     */
    @Test
    public void testCreate() {
        PauseMessageSender pauseMessageSender = new PauseMessageSender();
        assertTrue(pauseMessageSender != null);
    }

    /**
     * Test sending an sms message
     */
    @Test
    public void testSendSMSMessage() {
        final String PAUSE_TEXT = "Sorry can't talk now";
        String phoneNumber = "2482526955";

        PauseBounceBackMessage pauseBounceMessage = new PauseBounceBackMessage();
        pauseBounceMessage.setMessage(PAUSE_TEXT);


        PauseMessageSender pauseMessageSender = new PauseMessageSender();
        pauseMessageSender.sendSmsMessage(phoneNumber, pauseBounceMessage);

        ShadowSmsManager shadowSmsManager =  Robolectric.shadowOf(SmsManager.getDefault());
        ShadowSmsManager.TextSmsParams lastSenttextMessageParams = shadowSmsManager.getLastSentTextMessageParams();

        assertEquals(phoneNumber, lastSenttextMessageParams.getDestinationAddress());
        assertEquals(PAUSE_TEXT, lastSenttextMessageParams.getText());
    }
}
