package com.pauselabs.pause.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.PausePhoneStateListener;
import com.pauselabs.pause.listeners.PauseSmsListener;
import com.pauselabs.pause.ui.ScoreboardActivity;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Service initiates Pause Listeners
 */
public class PauseSessionService extends Service{

    private static final String TAG = PauseSessionService.class.getSimpleName();

    @Inject protected Bus eventBus;
    @Inject NotificationManager notificationManager;

    private PauseSmsListener smsListener = new PauseSmsListener();
    private PausePhoneStateListener phoneListener = new PausePhoneStateListener();
    private boolean sessionRunning = false;
    private boolean sessionStarted;

    private boolean mStophandler = false;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(!mStophandler) {
//                updateTimeRemaining();
                handler.postDelayed(this, 1000);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        Injector.inject(this);

        // Register the bus so we can send notifcations
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {

        // Unregister bus, since its not longer needed as the service is shutting down
        eventBus.unregister(this);

        // unregister receiver(s)
        unregisterReceiver(smsListener);
        unregisterReceiver(phoneListener);

        notificationManager.cancel(Constants.Notification.SESSION_NOTIFICATION_ID);

        Log.d(TAG, "Service has been destroyed");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!sessionStarted) {

            sessionStarted = true;

            startPauseSession();
            // Run as foreground service: http://stackoverflow.com/a/3856940/5210
            // Another example: https://github.com/commonsguy/cw-android/blob/master/Notifications/FakePlayer/src/com/commonsware/android/fakeplayerfg/PlayerService.java
            startForeground(Constants.Notification.SESSION_NOTIFICATION_ID, getNotification(getString(R.string.pause_session_running)));
        }

        return Service.START_NOT_STICKY; // Service will not be restarted if android kills it
    }

    private void startPauseSession() {

        // start SMS receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Message.SMS_RECEIVED_INTENT);
        registerReceiver(smsListener, filter);

        // start Phone Call receiver
        IntentFilter phoneStateFilter = new IntentFilter();
        phoneStateFilter.addAction(Constants.Message.PHONE_STATE_CHANGE_INTENT);
        registerReceiver(phoneListener, phoneStateFilter);

        notifyPauseSessionRunning();
    }

    private void notifyPauseSessionRunning() {
        updateNotification(getString(R.string.pause_session_running));
    }

    private void updateNotification(String message) {
        notificationManager.notify(Constants.Notification.SESSION_NOTIFICATION_ID, getNotification(message));
    }

    /**
     * Creates a notification to show in the notification bar
     *
     * @param message the message to display in the notification bar
     * @return a new {@link Notification}
     */
    private Notification getNotification(String message) {
        final Intent i = new Intent(this, ScoreboardActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_stat_pause_icon_pause)
                .setContentText(message)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .getNotification();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }


}
