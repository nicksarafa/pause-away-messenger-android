package com.pauselabs.pause.listeners;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseSession;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Passa on 11/17/14.
 */
public class PLocationListener implements LocationListener {

    private final String TAG = PLocationListener.class.getSimpleName();

    private final float TOMPH = 2.23694f;
    private float mph;

    private Timer timer;

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasSpeed()) {
            mph = location.getSpeed() * TOMPH;

            if (mph >= Constants.Settings.MPH_TILL_PAUSE) {
                if (timer != null) {
                    Log.i(TAG,"Driving and timer is running, cancelling.");

                    timer.cancel();
                    timer = null;
                }

                if (PauseApplication.isDriveModeAllowed()) {
                    Log.i(TAG,"Drive Mode is allowed. Starting Session.");

                    PauseApplication.startPauseService(Constants.Session.Creator.DRIVE);
                } else
                    Log.i(TAG,"Drive Mode not allowed.");
            } else if (mph < Constants.Settings.MPH_TILL_PAUSE && timer == null) {
                Log.i(TAG,"Not driving, starting timer.");

                timer = new Timer("SpeedTestTimer");
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        Log.i(TAG, "Timer executed.");

                        // No longer in driving mode
                        if (mph < Constants.Settings.MPH_TILL_PAUSE) {
                            Log.i(TAG, "Still not driving, enabling Drive Mode.");

                            PauseApplication.setDriveModeAllowed(true);

                            if ((PauseApplication.isActiveSession() && PauseApplication.getCurrentSession().getCreator() == Constants.Session.Creator.DRIVE)) {
                                Log.i(TAG, "Session is still running via Drive Mode. Stopping.");

                                PauseApplication.stopPauseService(Constants.Session.Destroyer.DRIVE);
                            }
                        }
                    }

                }, (long) (Constants.Settings.LOCATION_STOPPED_TIME_OUT * 60 * 1000));
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
