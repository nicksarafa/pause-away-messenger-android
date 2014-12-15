package com.pauselabs.pause.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Passa on 12/11/14.
 */
public class PhoneFilppedListener implements SensorEventListener {

    boolean isFaceDown = false;
    boolean isStill = false;

    Timer timer;

    @Override
    public void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        if(type == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            if(distance < 1.0f) {
                isFaceDown = true;

//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        // Phone is still not moving
//                        // Activate Paüse via Sleep Mode
//                        PauseApplication.checkForSleepMode();
//                    }
//
//                }, (long) (Constants.Settings.STILL_ACCELEROMETER_TIME_OUT * 60 * 1000));
            } else {
                isFaceDown = false;

//                timer.cancel();
            }
        } /*else if(type == Sensor.TYPE_LINEAR_ACCELERATION && isFaceDown) {
            float x = Math.abs(event.values[0]), y = Math.abs(event.values[1]), z = Math.abs(event.values[2]);
            float stillConstant = Constants.Settings.STILL_CONSTANT;

            if (x <= stillConstant && y <= stillConstant && z <= stillConstant) {
                isStill = true;

                timer = new Timer();
            } else {
                isStill = false;

                timer.cancel();
            }
        }*/

        checkToPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void checkToPause() {
        if(isFaceDown) {
            PauseApplication.startPauseService(Constants.Session.Creator.FLIP);
        } else {
            PauseApplication.stopPauseService(Constants.Session.Creator.FLIP);
        }
    }
}
