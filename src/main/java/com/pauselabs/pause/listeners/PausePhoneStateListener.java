package com.pauselabs.pause.listeners;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseMessage;
import com.pauselabs.pause.services.PauseMessageReceivedService;

/**
 * The PausePhoneStateListener is responsible for listening to changes in the Phone State and sending a Pause message on a missed phone call
 */
public class PausePhoneStateListener extends BroadcastReceiver{

    private static final String TAG = PausePhoneStateListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "receive phone state change.");
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);
        ReplyPhoneStateListener replyPhoneStateListener = new ReplyPhoneStateListener(
                context);
        telephonyManager.listen(replyPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        telephonyManager.listen(replyPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);

    }

    class ReplyPhoneStateListener extends PhoneStateListener {
        private Context context;

        public ReplyPhoneStateListener(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            SharedPreferences sharedPreferences = this.context
                    .getApplicationContext().getSharedPreferences(
                            Constants.Message.MISSED_CALL_PREFERENCE,
                            Context.MODE_WORLD_WRITEABLE
                                    | Context.MODE_WORLD_READABLE);

            int olderSharedPreference = sharedPreferences.getInt(
                    Constants.Message.PREFERENCE_OLDER_PHONE_STATE, -1);
            Log.d(TAG, "Old phone state: "
                    + olderSharedPreference);

            if(!incomingNumber.equals("")) {
                sharedPreferences.edit().putString(Constants.Message.PREFERENCE_LAST_CALL_NUMBER, incomingNumber).commit();
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.Message.PREFERENCE_OLDER_PHONE_STATE, state);
            editor.commit();
            Log.d(TAG, "Write phone state: " + state);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (olderSharedPreference == TelephonyManager.CALL_STATE_OFFHOOK
                            || olderSharedPreference == TelephonyManager.CALL_STATE_RINGING) {
//                        Thread replyThread = new Thread(new ReplyThread(state,
//                                incomingNumber, context));
//                        replyThread.start();
                        // Create Message object

                        String savedNumber = sharedPreferences.getString(Constants.Message.PREFERENCE_LAST_CALL_NUMBER, "none");
                        PauseMessage messageReceived = new PauseMessage(savedNumber, "missed phone call");

                        // Start PauseMessageReceivedService and include message as extra
                        Intent messageReceivedIntent = new Intent(context, PauseMessageReceivedService.class);
                        messageReceivedIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (Parcelable) messageReceived);
                        context.startService(messageReceivedIntent);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
            }
        }
    }
}
