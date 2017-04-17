package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.SmsMessage;
import android.util.Log;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseMessage;
import com.pauselabs.pause.services.PauseMessageReceivedService;

/** This receiver listens for incoming SMS messages and triggers the MessageReceivedService */
public class PauseSmsListener extends BroadcastReceiver {

  private static final String TAG = PauseSmsListener.class.getSimpleName();

  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Constants.Message.SMS_RECEIVED_INTENT)) {

      Bundle bundle = intent.getExtras();
      SmsMessage[] msgs = null;
      String msg_from;

      if (bundle != null) {
        // Retrieve the SMS message received
        try {
          Object[] pdus = (Object[]) bundle.get(Constants.Message.PDUS_EXTRA);
          msgs = new SmsMessage[pdus.length];
          for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            msg_from = msgs[i].getOriginatingAddress();
            String msgBody = msgs[i].getMessageBody();

            Log.v(TAG, "message received from : " + msg_from + " text: " + msgBody);

            // Create Message object
            PauseMessage messageReceived = new PauseMessage(msgs[i]);

            // Start PauseMessageReceivedService and include message as extra
            Intent messageReceivedIntent = new Intent(context, PauseMessageReceivedService.class);
            messageReceivedIntent.putExtra(
                Constants.Message.MESSAGE_PARCEL, (Parcelable) messageReceived);
            context.startService(messageReceivedIntent);
          }
        } catch (Exception e) {
          Log.d("Error retrieving sms message", e.getMessage());
        }
      }
    }
  }
}
