package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;

/** Created by Passa on 11/20/14. */
public class PhoneChargingListener extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    boolean charging =
        status == BatteryManager.BATTERY_STATUS_CHARGING
            || status == BatteryManager.BATTERY_STATUS_FULL;
    PauseApplication.setPhoneCharging(charging);

    if (!charging) PauseApplication.stopPauseService(Constants.Session.Destroyer.SLEEP);
  }
}
