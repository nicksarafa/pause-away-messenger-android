package com.pauselabs.pause.core;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.pauselabs.pause.models.PauseMessage;

import java.util.ArrayList;

//import com.google.android.mms.pdu.*;

//import com.google.android.mms.pdu.*;

/**
 * This class is responsible for the actual sending of Pause bounce back messages
 */
public class PauseMessageSender {

    private static final String TAG = PauseMessageSender.class.getSimpleName();
    private Context mContext;
    private static final int MAX_SMS_LENGTH = 160;

    public PauseMessageSender(Context context){
        this.mContext = context;

    }

    public void sendSmsMessage(String to, PauseMessage pauseMessage){
        SmsManager smsManager = SmsManager.getDefault();

        // the Android divideMessage works by encoding each character as 7 bits, sms texts can only be upto 160 chars.
        // for some reason if the message contains a character such as an apostrophe it encodes these as 16 bits,
        // and can cause messages which are less than 160 characters to be divided
        ArrayList<String> messageParts = smsManager.divideMessage(pauseMessage.getMessage());


        smsManager.sendMultipartTextMessage(to, null, messageParts, null, null);


        Log.v(TAG, "Attempting to send message to: " + to + " text: " + pauseMessage.getMessage());
    }

}
