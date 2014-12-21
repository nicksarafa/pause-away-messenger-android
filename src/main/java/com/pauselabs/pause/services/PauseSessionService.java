package com.pauselabs.pause.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.PausePhoneStateListener;
import com.pauselabs.pause.listeners.PauseSmsListener;
import com.pauselabs.pause.models.PauseMessage;
import com.pauselabs.pause.models.PauseSession;

import java.util.Date;

import javax.inject.Inject;

/**
 * Service initiates Pause Listeners
 */
public class PauseSessionService extends Service{

    private static final String TAG = PauseSessionService.class.getSimpleName();

    private PauseSmsListener observer;
    private PausePhoneStateListener phoneListener;

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
        am.setRingerMode(PauseApplication.getOldRingerMode());

        int responseCount = PauseApplication.getCurrentSession().getResponseCount();

        PauseApplication.speak("Pause off.");
        PauseApplication.sendToast("PAÜSE OFF Ü");
        PauseApplication.sendToast(
                PauseApplication.numCall + " missed calls \n" +
                PauseApplication.numSMS + " missed texts \n\n" +
                "I sent " + responseCount + " message" + ((responseCount == 1) ? "" : "s") + " for you."
        );

        PauseApplication.numSMS = 0;
        PauseApplication.numCall = 0;

        getContentResolver().unregisterContentObserver(observer);

        unregisterReceiver(phoneListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,false))
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        PauseApplication.speak("Pause on.");
        PauseApplication.sendToast("PAÜSE ON Ü");

//        PauseApplication.sr.startListening(intent);

        // start SMS observer
        observer = new PauseSmsListener(null);
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, observer);

        // start Phone Call listener
        phoneListener = new PausePhoneStateListener();
        IntentFilter phoneStateFilter = new IntentFilter();
        phoneStateFilter.addAction(Constants.Message.PHONE_STATE_CHANGE_INTENT);
        registerReceiver(phoneListener, phoneStateFilter);

        return Service.START_NOT_STICKY; // Service will not be restarted if android kills it
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

}
