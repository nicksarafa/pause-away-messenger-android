package com.pauselabs.pause.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.listeners.PauseCallListener;
import com.pauselabs.pause.listeners.PauseSmsListener;
import com.pauselabs.pause.model.Constants;

import javax.inject.Inject;

/**
 * Service initiates Pause Listeners
 */
public class PauseSessionService extends Service{

    private static final String TAG = PauseSessionService.class.getSimpleName();

    private PauseSmsListener observer;
    private PauseCallListener callListener;

    @Inject
    AudioManager am;
    @Inject
    SharedPreferences prefs;


    @Override
    public void onCreate() {
        super.onCreate();

        Injector.inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        am.setRingerMode(PauseApplication.getOldRingerMode());
        Log.i(TAG,"Destroyed");

        PauseApplication.updateUI();

        int responseCount = PauseApplication.getCurrentSession().getResponseCount();

        PauseApplication.speak("Pause off.");
        PauseApplication.sendToast("Off");
        PauseApplication.sendToast(
                PauseApplication.numCall + " Missed Calls" + "\n" +
                PauseApplication.numSMS + " Missed Texts" + "\n" +
                responseCount + " Repl" + ((responseCount == 1) ? "y" : "ies") + " Sent"
        );

        PauseApplication.numSMS = 0;
        PauseApplication.numCall = 0;

        getContentResolver().unregisterContentObserver(observer);

        unregisterReceiver(callListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        if (prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,true))
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.i(TAG,"Started");

        PauseApplication.updateUI();

        PauseApplication.speak("Pause on.");
        PauseApplication.sendToast("On");

//        PauseApplication.sr.startListening(intent);

        // start SMS observer
        observer = new PauseSmsListener(null);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, observer);

        // start Phone Call listener
        callListener = new PauseCallListener();
        IntentFilter phoneStateFilter = new IntentFilter();
        phoneStateFilter.addAction(Constants.Message.PHONE_STATE_CHANGE_INTENT);
        phoneStateFilter.addAction(Constants.Message.NEW_OUTGOING_CALL_INTENT);
        registerReceiver(callListener, phoneStateFilter);

        Notification not = PauseApplication.updateMainNotification();
        startForeground(Constants.Notification.SESSION_NOTIFICATION_ID, not);

        return Service.START_STICKY; // Service will not be restarted if android kills it
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

}
