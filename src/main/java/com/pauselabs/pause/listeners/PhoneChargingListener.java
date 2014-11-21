package com.pauselabs.pause.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;

/**
 * Created by Passa on 11/20/14.
 */
public class PhoneChargingListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constants.Message.PHONE_POWER_CONNECTED_INTENT) || action.equals(Constants.Message.PHONE_POWER_DISCONNECTED_INTENT)) {
            PauseApplication.checkIsPhoneCharging(intent);
        }
    }
}
