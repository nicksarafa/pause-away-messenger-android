package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;

import javax.inject.Inject;

/**
 * Created by Passa on 11/10/14.
 */
public class SilenceListener extends BroadcastReceiver {

    private static final String TAG = SilenceListener.class.getSimpleName();

    @Inject SharedPreferences prefs;
    @Inject AudioManager am;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {

            if ((am.getRingerMode() == AudioManager.RINGER_MODE_SILENT && prefs.getBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY,Constants.Settings.DEFAULT_PAUSE_ON_SILENT)) || (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE && prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,Constants.Settings.DEFAULT_PAUSE_ON_VIBRATE))) {
                PauseApplication.startPauseService(Constants.Session.Creator.VOLUME);
            } else if ((am.getRingerMode() == AudioManager.RINGER_MODE_SILENT && !prefs.getBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY,Constants.Settings.DEFAULT_PAUSE_ON_SILENT)) || (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE && !prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,Constants.Settings.DEFAULT_PAUSE_ON_VIBRATE)) || am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                PauseApplication.stopPauseService(Constants.Session.Destroyer.VOLUME);
                PauseApplication.setOldRingerMode(am.getRingerMode());
            }
        }
    }
}
