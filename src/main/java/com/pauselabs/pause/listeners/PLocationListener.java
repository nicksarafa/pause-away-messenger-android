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

    private PauseSession currentSession;

    private Timer timer;

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasSpeed()) {
            currentSession = PauseApplication.getCurrentSession();
            mph = location.getSpeed() * TOMPH;

            if (mph >= Constants.Settings.MPH_TILL_PAUSE && (currentSession == null || !currentSession.isActive())) {

                Log.i(TAG,"Moving in a car.");

                currentSession = PauseApplication.startPauseService(Constants.Session.Creator.DRIVE);

                if (timer != null)
                    timer.cancel();
            } else if (mph < Constants.Settings.MPH_TILL_PAUSE && (currentSession != null && currentSession.getCreator() == Constants.Session.Creator.DRIVE && currentSession.isActive())) {

                Log.i(TAG,"No longer moving. Start timer.");

                timer = new Timer("SpeedTestTimer");
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // No longer in driving mode
                        if (mph < Constants.Settings.MPH_TILL_PAUSE) {
                            PauseApplication.stopPauseService();
                            currentSession = null;
                        }
                    }

                }, (long) (Constants.Settings.LOCATION_STOPPED_TIME_OUT*60*1000));
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
