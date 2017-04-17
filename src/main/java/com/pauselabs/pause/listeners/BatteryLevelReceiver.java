package com.pauselabs.pause.listeners;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.ui.MainActivity;

/** Listens for low battery signal, triggers notification to prompt user to create a Pause */
public class BatteryLevelReceiver extends BroadcastReceiver {

  private NotificationManager notificationManager;
  private Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;

    // if battery is low check to see if a Pause is already running
    if (!pauseSessionIsActive()) {
      notificationManager =
          (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(
          Constants.Notification.LOW_BATTERY_NOTIFICATION_ID,
          getNotification(Constants.Notification.LOW_BATTERY_MESSAGE));
    }
  }

  private boolean pauseSessionIsActive() {
    boolean isActive = false;
    PauseSession currentSession = PauseApplication.getCurrentSession();
    if (currentSession != null && currentSession.isActive()) {
      isActive = true;
    }
    return isActive;
  }

  /**
   * Creates a notification to show in the notification bar
   *
   * @param message the message to display in the notification bar
   * @return a new {@link android.app.Notification}
   */
  private Notification getNotification(String message) {
    final Intent i = new Intent(mContext, MainActivity.class);

    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, i, 0);

    return new NotificationCompat.Builder(mContext)
        .setContentTitle(mContext.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_stat_pause_icon_pause)
        .setContentText(message)
        .setAutoCancel(false)
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .setWhen(System.currentTimeMillis())
        .setContentIntent(pendingIntent)
        .getNotification();
  }
}
