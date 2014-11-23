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

            if (mph >= Constants.Settings.MPH_TILL_PAUSE && !PauseApplication.isActiveSession()) {
                if (PauseApplication.isDriveModeAllowed()) {
                    PauseApplication.startPauseService(Constants.Session.Creator.DRIVE);

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
            } else if (mph < Constants.Settings.MPH_TILL_PAUSE) {
                timer = new Timer("SpeedTestTimer");
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // No longer in driving mode
                        if (mph < Constants.Settings.MPH_TILL_PAUSE) {
                            PauseApplication.setDriveModeAllowed(true);

                            if ((PauseApplication.isActiveSession() && PauseApplication.getCurrentSession().getCreator() == Constants.Session.Creator.DRIVE))
                                PauseApplication.stopPauseService(Constants.Session.Destroyer.DRIVE);
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
