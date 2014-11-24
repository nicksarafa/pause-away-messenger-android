package com.pauselabs.pause.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by Passa on 11/23/14.
 */
public class MagneticFieldListener implements SensorEventListener {

    private final String TAG = MagneticFieldListener.class.getSimpleName();

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            Log.i(TAG,"magnet sensor \nx: " + x + "\ny: " + y + "\nz: " + z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
