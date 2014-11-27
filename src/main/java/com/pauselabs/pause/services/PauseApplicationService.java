package com.pauselabs.pause.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.LinearAccelerometerListener;
import com.pauselabs.pause.listeners.PhoneChargingListener;
import com.pauselabs.pause.listeners.SilenceListener;

/**
 * Created by Passa on 11/20/14.
 */
public class PauseApplicationService extends Service {

    private final String TAG = PauseApplicationService.class.getSimpleName();

    private LinearAccelerometerListener accelerometerListener;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetron;

    private PhoneChargingListener chargingListener;

    private SilenceListener silenceListener;

    @Override
    public void onCreate() {
        super.onCreate();

//        Injector.inject(this);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(accelerometerListener);
        unregisterReceiver(chargingListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Pause Application Service Start Command");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometerListener = new LinearAccelerometerListener();
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        chargingListener = new PhoneChargingListener();
        IntentFilter chargingIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(chargingListener,chargingIntentFilter);

        silenceListener = new SilenceListener();
        IntentFilter silenceIntentFilter = new IntentFilter(Constants.Message.PHONE_SILENCE_INTENT);
        registerReceiver(silenceListener,silenceIntentFilter);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
