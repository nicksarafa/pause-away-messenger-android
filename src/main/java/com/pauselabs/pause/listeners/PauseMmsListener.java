package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.SmsMessage;
import android.util.Log;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseMessage;
import com.pauselabs.pause.services.PauseMessageReceivedService;

/**
 * This receiver listens for incoming MMS messages and triggers the MessageReceivedService
 */
public class PauseMmsListener extends BroadcastReceiver {
    private static final String TAG = PauseMmsListener.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        if(action.equals(Constants.Mms.ACTION_MMS_RECEIVED) && type.equals(Constants.Mms.MMS_DATA_TYPE)) {

            PauseApplication.numSMS++;

            Bundle bundle = intent.getExtras();

            Log.v(TAG, "bundle " + bundle);
            SmsMessage[] msgs = null;
            String str = "";
            int contactId = -1;
            String address;

            if (bundle != null) {

                byte[] buffer = bundle.getByteArray("data");
                Log.v(TAG, "buffer " + buffer);
                String incomingNumber = new String(buffer);
                int indx = incomingNumber.indexOf("/TYPE");
                if (indx > 0 && (indx - 15) > 0) {
                    int newIndx = indx - 15;
                    incomingNumber = incomingNumber.substring(newIndx, indx);
                    indx = incomingNumber.indexOf("+");
                    if (indx > 0) {
                        incomingNumber = incomingNumber.substring(indx);
                        Log.v(TAG, "Mobile Number: " + incomingNumber);
                    }
                }

                // Create Message object
                PauseMessage messageReceived = new PauseMessage(incomingNumber,"0", "");

                // TODO Test this!

                // Start PauseMessageReceivedService and include message as extra
                Intent messageReceivedIntent = new Intent(context, PauseMessageReceivedService.class);
                messageReceivedIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (Parcelable) messageReceived);
                context.startService(messageReceivedIntent);
            }

        }

    }
}
