package com.pauselabs.pause.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.MmsSendHelper;

/**
 * This service is responsible for sending the actual MMS network request
 */
public class PauseMmsTransportService extends IntentService {
    private static final String TAG = PauseMmsTransportService.class.getSimpleName();
    private MmsSendHelper sendHelper;

    public PauseMmsTransportService() {
        super("PauseMmsTransportService");
        sendHelper = new MmsSendHelper();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null) {
            byte[] bytesToSend = extras.getByteArray(Constants.Mms.MMS_BYTE_ARRAY_EXTRA);

            try{
                // TODO chnage so proxy and radio can be toggled
                sendHelper.sendMms(PauseApplication.getInstance(), bytesToSend, "", true, false);
            }
            catch(Exception e) {
                Log.v(TAG, e.getMessage().toString());
            }
        }


    }
}
