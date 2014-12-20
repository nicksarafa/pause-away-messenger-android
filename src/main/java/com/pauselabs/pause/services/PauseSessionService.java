package com.pauselabs.pause.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
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

/**
 * Service initiates Pause Listeners
 */
public class PauseSessionService extends Service{

    private static final String TAG = PauseSessionService.class.getSimpleName();

    private PauseSmsListener observer;
    private PausePhoneStateListener phoneListener;

    private AudioManager am;


    @Override
    public void onCreate() {
        super.onCreate();

        Injector.inject(this);

        am = (AudioManager)getSystemService(AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        if (PauseApplication.getOldRingerMode() != AudioManager.RINGER_MODE_SILENT)
            am.setRingerMode(PauseApplication.getOldRingerMode());


        PauseApplication.tts.speak("Pause off.", TextToSpeech.QUEUE_ADD, null);

        PauseApplication.numSMS = 0;
        PauseApplication.numCall = 0;

        getContentResolver().unregisterContentObserver(observer);

        unregisterReceiver(phoneListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PauseApplication.setOldRingerMode(am.getRingerMode());
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        PauseApplication.tts.speak("Pause on.", TextToSpeech.QUEUE_ADD, null);

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
