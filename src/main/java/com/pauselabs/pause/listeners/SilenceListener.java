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
import android.nfc.Tag;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseSession;

/**
 * Created by Passa on 11/10/14.
 */
public class SilenceListener extends BroadcastReceiver {
    private static final String TAG = SilenceListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        PauseSession currentSession = PauseApplication.getCurrentSession();
        if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                case AudioManager.RINGER_MODE_VIBRATE:
                    PauseApplication.startPauseService(Constants.Session.Creator.SILENCE);

                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    if (PauseApplication.isActiveSession())
                        PauseApplication.stopPauseService(Constants.Session.Destroyer.SILENCE);
                    PauseApplication.setOldRingerMode(am.getRingerMode());

                    break;
            }
        }

        /*if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            Log.i(TAG,"Key Pressed");
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode()) {
                Log.i(TAG,"Down Pressed");
                if (PauseApplication.isActiveSession()) {
                    int oldCreator = currentSession.getCreator();
                    int newCreator = (oldCreator == Constants.Session.Creator.class.getFields().length - 1) ? Constants.Session.Creator.SILENCE : oldCreator + 1;
                    currentSession.setCreator(newCreator);
                    PauseApplication.shouldUpdateNotification = true;

                    String modeName = "";
                    if (newCreator == Constants.Session.Creator.SILENCE)
                        modeName = "Silence";
                    if (newCreator == Constants.Session.Creator.DRIVE)
                        modeName = "Drive";
                    if (newCreator == Constants.Session.Creator.SLEEP)
                        modeName = "Sleep";
                    Toast.makeText(context,"You are now in " + modeName + " Mode",Toast.LENGTH_SHORT).show();
                }
            }
        }*/
    }
}
