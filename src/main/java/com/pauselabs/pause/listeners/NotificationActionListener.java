package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.MainActivity;
import com.pauselabs.pause.model.Constants;

/**
 * Created by tyndallm on 8/24/14.
 */
public class NotificationActionListener extends BroadcastReceiver {
    private static final String TAG = NotificationActionListener.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras() != null){
            int actionCode = intent.getIntExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, 0);
            switch(actionCode) {
                case Constants.Notification.STOP_PAUSE_SESSION:
                    PauseApplication.stopPauseService(PauseApplication.getCurrentSession().getCreator());

                    break;
                case Constants.Notification.EDIT_PAUSE_SESSION:
                    Intent i = new Intent(PauseApplication.getInstance(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("SET_EDIT_ITEM",MainActivity.EMOJI_SUMMARY_TAB);
                    PauseApplication.getInstance().startActivity(i);

                    break;
                case Constants.Notification.NOT_SLEEPING:
                    PauseApplication.stopPauseService(Constants.Session.Destroyer.SLEEP);

                    break;
                case Constants.Notification.NOT_DRIVER:
                    PauseApplication.stopPauseService(Constants.Session.Destroyer.DRIVE);

                    break;
                case Constants.Notification.MODE_CUSTOM:
                    PauseApplication.getCurrentSession().setCreator(Constants.Session.Creator.CUSTOM);
                    PauseApplication.updateNotifications();

                    break;
                case Constants.Notification.MODE_SILENCE:
                    PauseApplication.getCurrentSession().setCreator(Constants.Session.Creator.SILENCE);
                    PauseApplication.updateNotifications();

                    break;
                case Constants.Notification.MODE_SLEEP:
                    PauseApplication.getCurrentSession().setCreator(Constants.Session.Creator.SLEEP);
                    PauseApplication.updateNotifications();

                    break;
                case Constants.Notification.MODE_DRIVE:
                    PauseApplication.getCurrentSession().setCreator(Constants.Session.Creator.DRIVE);
                    PauseApplication.updateNotifications();

                    break;
            }
        }
    }
}
