package com.pauselabs.pause;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import com.pauselabs.R;
import com.pauselabs.pause.core.PauseMessageSender;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseMMSPart;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.services.PauseSessionService;

import java.io.ByteArrayOutputStream;

public class PauseApplication extends Application {

    private static PauseApplication instance;
    private static final String TAG = PauseApplication.class.getSimpleName();

    public static PauseMessageSender messageSender;
    private static PauseSession currentPauseSession;

    public PauseApplication() {

    }

    public PauseApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    public PauseApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Perform injection
        Injector.init(getRootModule(), this);

        messageSender = new PauseMessageSender(instance);

    }

    /**
     * Paused state is initiated here
     */
    public static void startPauseService() {
        Intent pauseIntent = new Intent(instance, PauseSessionService.class);
        instance.startService(pauseIntent);

        // Create new Pause Session
        currentPauseSession = new PauseSession();

    }

    /**
     * Stop current pause
     */
    public static void stopPauseService() {
        Intent pauseIntent = new Intent(instance, PauseSessionService.class);
        instance.stopService(pauseIntent);

        // Delete Pause Session
        // TODO
    }

    /**
     * Send a test MMS message
     * @return
     */
    public static void sendMMSTestMessage() {
        Bitmap b = BitmapFactory.decodeResource(getInstance().getResources(),
                R.drawable.icon);   // Whatever your bitmap is that you want to send
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        PauseMMSPart[] parts = new PauseMMSPart[1];

        parts[0] = new PauseMMSPart();
        parts[0].Name = "Image";
        parts[0].MimeType = "image/png";
        parts[0].Data = byteArray;

        messageSender.sendMmsMessage("12482526955", parts);
    }

    public static void sendMMSTestToSelf() {
        PauseBounceBackMessage currentPause = new PauseBounceBackMessage();
        currentPause.setMessage("Sorry can't talk now, I'm wired in.");

        String usersPhone = ((TelephonyManager) getInstance().getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

        messageSender.sendMmsMessage(usersPhone, currentPause);
    }

    private Object getRootModule() {
        return new RootModule();
    }

    public static PauseApplication getInstance() {
        return instance;
    }

    public static PauseSession getCurrentSession() {
        return currentPauseSession;
    }
}
