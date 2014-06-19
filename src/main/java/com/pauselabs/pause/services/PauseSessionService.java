package com.pauselabs.pause.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.PauseSmsListener;

/**
 * Service initiates Pause Listeners
 */
public class PauseSessionService extends Service{

    private static final String TAG = PauseSessionService.class.getSimpleName();
    private PauseSmsListener smsListener = new PauseSmsListener();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Message.SMS_RECEIVED_INTENT);

        registerReceiver(smsListener, filter);


        return Service.START_NOT_STICKY; // Service will not be restarted if android kills it
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.v(TAG, "PauseSessionService onDestroy method called");

        // unregister receiver(s)
        unregisterReceiver(smsListener);

    }
}
