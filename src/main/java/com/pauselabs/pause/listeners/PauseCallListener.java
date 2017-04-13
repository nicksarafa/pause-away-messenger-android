package com.pauselabs.pause.listeners;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.PauseMessage;
import com.pauselabs.pause.model.PauseSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * The PausePhoneStateListener is responsible for listening to changes in the Phone State and
 * sending a Pause message on a missed phone call
 */
public class PauseCallListener extends BroadcastReceiver {

  private static final String TAG = PauseCallListener.class.getSimpleName();

  TelephonyManager telephonyManager;

  private String number = "";
  private boolean wasRinging = false;

  @Override
  public void onReceive(Context context, Intent intent) {
    telephonyManager =
        (TelephonyManager)
            PauseApplication.getInstance().getSystemService(Service.TELEPHONY_SERVICE);

    if (intent.getAction().equals(Constants.Message.NEW_OUTGOING_CALL_INTENT)) {
      String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

      PauseMessage newMessage =
          new PauseMessage(
              "0",
              number,
              "outgoing call",
              new Date().getTime(),
              Constants.Message.Type.PHONE_OUTGOING);
      PauseApplication.handleMessageSent(newMessage);
    } else if (intent.getAction().equals(Constants.Message.PHONE_STATE_CHANGE_INTENT)) {
      ReplyPhoneStateListener replyPhoneStateListener = new ReplyPhoneStateListener(context);
      telephonyManager.listen(replyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
      telephonyManager.listen(replyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
  }

  class ReplyPhoneStateListener extends PhoneStateListener {
    private Context context;

    public ReplyPhoneStateListener(Context context) {
      this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

      switch (state) {
        case TelephonyManager.CALL_STATE_IDLE:
          if (wasRinging) {
            PauseMessage messageReceived =
                new PauseMessage(
                    number, "0", "", new Date().getTime(), Constants.Message.Type.PHONE_INCOMING);
            messageReceived.setMessage("Missed Call");

            PauseApplication.stopSpeaking();

            PauseApplication.numCall++;

            PauseApplication.handleMessageReceived(messageReceived);

            number = "";
            wasRinging = false;
          }

          break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
          break;
        case TelephonyManager.CALL_STATE_RINGING:
          number = incomingNumber;
          wasRinging = true;

          PauseSession currentPauseSession = PauseApplication.getCurrentSession();

          if (currentPauseSession.isIced(PauseApplication.lookupContact(number))) {
            PauseApplication.speak(
                "Your ICE contact, "
                    + currentPauseSession
                        .getConversationByContactNumber(incomingNumber)
                        .getContactName()
                    + ", is calling you!");
            PauseApplication.speak(
                "Your ICE contact, "
                    + currentPauseSession
                        .getConversationByContactNumber(incomingNumber)
                        .getContactName()
                    + ", is calling you!");
            PauseApplication.speak(
                "Your ICE contact, "
                    + currentPauseSession
                        .getConversationByContactNumber(incomingNumber)
                        .getContactName()
                    + ", is calling you!");
          } else hangUp();

          break;
      }
    }

    public void hangUp() {
      TelephonyManager telephonyManager =
          (TelephonyManager)
              PauseApplication.getInstance().getSystemService(Service.TELEPHONY_SERVICE);

      Class c = null;
      try {
        c = Class.forName(telephonyManager.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        Object telephonyService = m.invoke(telephonyManager); // Get the internal ITelephony object
        c = Class.forName(telephonyService.getClass().getName()); // Get its class
        m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
        m.setAccessible(true); // Make it accessible
        m.invoke(telephonyService); // invoke endCall()
      } catch (ClassNotFoundException
          | InvocationTargetException
          | NoSuchMethodException
          | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }
}
