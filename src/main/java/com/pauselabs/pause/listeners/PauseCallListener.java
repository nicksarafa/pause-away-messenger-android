package com.pauselabs.pause.listeners;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.PauseMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * The PausePhoneStateListener is responsible for listening to changes in the Phone State and sending a Pause message on a missed phone call
 */
public class PauseCallListener extends BroadcastReceiver{

    private static final String TAG = PauseCallListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.Message.NEW_OUTGOING_CALL_INTENT)) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            PauseMessage newMessage = new PauseMessage("0", number, "outgoing call", new Date().getTime(), Constants.Message.Type.PHONE_OUTGOING);
            PauseApplication.handleMessageSent(newMessage);
        } else if (intent.getAction().equals(Constants.Message.PHONE_STATE_CHANGE_INTENT)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
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
            SharedPreferences sharedPreferences = this.context.getApplicationContext().getSharedPreferences(Constants.Message.MISSED_CALL_PREFERENCE, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);

//            int olderSharedPreference = sharedPreferences.getInt(Constants.Message.PREFERENCE_OLDER_PHONE_STATE, -1);

//            if(!incomingNumber.equals("")) {
//                sharedPreferences.edit().putString(Constants.Message.PREFERENCE_LAST_CALL_NUMBER, incomingNumber).apply();
//            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.Message.PREFERENCE_OLDER_PHONE_STATE, state);
            editor.apply();

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
//                    if (olderSharedPreference == TelephonyManager.CALL_STATE_RINGING) {
//                        String savedNumber = sharedPreferences.getString(Constants.Message.PREFERENCE_LAST_CALL_NUMBER, "none");
//                        PauseMessage messageReceived = new PauseMessage(savedNumber, "0", "missed phone call", new Date().getTime(), Constants.Message.Type.PHONE_INCOMING);
//                        PauseApplication.handleMessageReceived(messageReceived);
//
//                        PauseApplication.numCall++;
//
//                        PauseApplication.updateNotifications();
//                        PauseApplication.updateUI();
//                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    PauseMessage messageReceived = new PauseMessage(incomingNumber, "0", "", new Date().getTime(), Constants.Message.Type.PHONE_INCOMING);
                    PauseApplication.handleMessageReceived(messageReceived);

                    break;
            }
        }
    }
}
