package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.ui.MainActivity;

/** Created by tyndallm on 8/24/14. */
public class NotificationActionListener extends BroadcastReceiver {
  private static final String TAG = NotificationActionListener.class.getSimpleName();

  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive");
    if (intent.getExtras() != null) {
      int actionCode = intent.getIntExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, 0);
      switch (actionCode) {
        case Constants.Notification.STOP_PAUSE_SESSION:
          PauseApplication.stopPauseService();

          Intent createPauseIntent = new Intent(context, MainActivity.class);
          createPauseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(createPauseIntent);
          break;
        case Constants.Notification.EDIT_PAUSE_SESSION:
          Long currentPauseId =
              intent.getLongExtra(Constants.Pause.EDIT_PAUSE_MESSAGE_ID_EXTRA, -1L);
          PauseApplication.stopPauseService();
          if (currentPauseId >= 0) {
            Intent editPauseIntent = new Intent(context, MainActivity.class);
            editPauseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            editPauseIntent.putExtra(Constants.Pause.EDIT_PAUSE_MESSAGE_ID_EXTRA, currentPauseId);
            context.startActivity(editPauseIntent);
          }
          break;
      }
    }
  }
}
