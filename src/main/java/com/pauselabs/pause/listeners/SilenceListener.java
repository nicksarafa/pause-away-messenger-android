package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;

import javax.inject.Inject;

/**
 * Created by Passa on 11/10/14.
 */
public class SilenceListener extends BroadcastReceiver {

    private static final String TAG = SilenceListener.class.getSimpleName();

    @Inject SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

            if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT || (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE && prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,false))) {
                PauseApplication.startPauseService(Constants.Session.Creator.SILENCE);
            } else if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL || (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE && !prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,false))) {
                if (PauseApplication.isActiveSession())
                    PauseApplication.stopPauseService(Constants.Session.Destroyer.SILENCE);
                PauseApplication.setOldRingerMode(am.getRingerMode());
            }
        }
    }
}
