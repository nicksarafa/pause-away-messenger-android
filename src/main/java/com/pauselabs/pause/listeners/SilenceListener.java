package com.pauselabs.pause.listeners;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;

/**
 * Created by Passa on 11/10/14.
 */
public class SilenceListener extends BroadcastReceiver {
    private static final String TAG = SilenceListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    PauseApplication.startPauseService(Constants.Session.Creator.SILENCE);

                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    PauseApplication.stopPauseService(Constants.Session.Destroyer.SILENCE);

                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    PauseApplication.stopPauseService(Constants.Session.Destroyer.SILENCE);

                    break;
            }

            PauseApplication.setOldRingerMode(am.getRingerMode());
        }
    }
}
