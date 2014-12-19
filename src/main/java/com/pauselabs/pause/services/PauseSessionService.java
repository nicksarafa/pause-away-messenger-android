package com.pauselabs.pause.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.PausePhoneStateListener;
import com.pauselabs.pause.listeners.PauseSmsListener;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseSession;

import org.w3c.dom.Text;

import java.util.Date;

import javax.inject.Inject;

/**
 * Service initiates Pause Listeners
 */
public class PauseSessionService extends Service{

    private static final String TAG = PauseSessionService.class.getSimpleName();

    private PauseSmsListener smsListener = new PauseSmsListener();
    private PausePhoneStateListener phoneListener = new PausePhoneStateListener();
    private boolean sessionRunning = false;
    private boolean sessionStarted;

    private Date mEndTime;
    private Date mStartTime;
    private PauseSession mActiveSession;
    private PauseBounceBackMessage mActivePauseBounceBack;

    private boolean mStophandler = false;

    private AudioManager am;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(!mStophandler) {
//                notifyPauseSessionRunning();
                handler.postDelayed(this, 1000);
            }
        }
    };


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

        mStophandler = true;
        handler.removeCallbacks(runnable);

        // unregister receiver(s)
        unregisterReceiver(smsListener);
        unregisterReceiver(phoneListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PauseApplication.setOldRingerMode(am.getRingerMode());
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        PauseApplication.tts.speak("Pause on.", TextToSpeech.QUEUE_ADD, null);

//        PauseApplication.sr.startListening(intent);

        if(!sessionStarted) {

            sessionStarted = true;

            startPauseSession();
            // Run as foreground service: http://stackoverflow.com/a/3856940/5210
            // Another example: https://github.com/commonsguy/cw-android/blob/master/Notifications/FakePlayer/src/com/commonsware/android/fakeplayerfg/PlayerService.java
//            startForeground(Constants.Notification.SESSION_NOTIFICATION_ID, null);
        }

        return Service.START_NOT_STICKY; // Service will not be restarted if android kills it
    }

    private void startPauseSession() {

        // start SMS receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsListener, filter);

        // start Phone Call receiver
        IntentFilter phoneStateFilter = new IntentFilter();
        phoneStateFilter.addAction(Constants.Message.PHONE_STATE_CHANGE_INTENT);
        registerReceiver(phoneListener, phoneStateFilter);

        // Retrieve Pause end time
        mActiveSession = PauseApplication.getCurrentSession();
//        mActivePauseBounceBack = mActiveSession.getActiveBounceBackMessage();
//        mEndTime = new Date(mActivePauseBounceBack.getEndTime());

        mStartTime = new Date();


        //notifyPauseSessionRunning();
        runnable.run();
    }

    /*private void notifyPauseSessionRunning() {
        Date currentDate = new Date();
        if(currentDate.getTime() > mEndTime.getTime()){
//            // timer has expired
//            stopPauseSession();

            // display results dialog
            mStophandler = true;

            PauseApplication.updateNotifications();

        }
        else {
            long diff = mEndTime.getTime() - currentDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            PauseApplication.updateNotifications();
        }

        //updateNotification(getString(R.string.pause_session_running));
    }*/


    public IBinder onBind(Intent intent) {
        return null;
    }



}
