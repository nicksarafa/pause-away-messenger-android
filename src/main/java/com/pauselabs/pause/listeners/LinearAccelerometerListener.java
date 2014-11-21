package com.pauselabs.pause.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;

/**
 * Created by Passa on 11/20/14.
 */
public class LinearAccelerometerListener implements SensorEventListener {

    private final String TAG = LinearAccelerometerListener.class.getSimpleName();

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (PauseApplication.isSleepTime()) {
            float x = Math.abs(event.values[0]), y = Math.abs(event.values[1]), z = Math.abs(event.values[2]);
            float stillConstant = Constants.Settings.STILL_CONSTANT;
            boolean isStill = false;

            if (x <= stillConstant && y <= stillConstant && z <= stillConstant)
                isStill = true;

            PauseApplication.setPhoneStill(isStill);

            PauseApplication.checkForSleepMode();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
