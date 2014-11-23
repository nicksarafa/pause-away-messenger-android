package com.pauselabs.pause;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pauselabs.BuildConfig;
import com.pauselabs.R;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.PauseMessageSender;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseMMSPart;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.services.PauseApplicationService;
import com.pauselabs.pause.services.PauseSessionService;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class PauseApplication extends Application {

    private static PauseApplication instance;
    private static final String TAG = PauseApplication.class.getSimpleName();
    public static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static PauseMessageSender messageSender;
    private static PauseSession currentPauseSession;
    private static boolean drawerOpen = false;

    private static boolean phoneIsCharging = false;
    private static boolean phoneIsStill= false;

    private static int oldRingerMode;

    private static boolean driveModeAllowed = true;

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER; // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

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

        if ( BuildConfig.USE_CRASHLYTICS ) {
            Crashlytics.start(this);
        }

        instance = this;

        // Perform injection
        Injector.init(getRootModule(), this);

        initImageLoader(getApplicationContext());

        messageSender = new PauseMessageSender(instance);

        startPauseApplicationService();

    }

    /**
     * Initialize Android Universal Image Loader
     */
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * Paused state is initiated here
     */
    public static void startPauseApplicationService() {
        Intent pauseApplicationIntent = new Intent(instance, PauseApplicationService.class);
        instance.startService(pauseApplicationIntent);
        Log.i(TAG,"Start Pause Application Service");
    }

    /**
     * Paused state is initiated here
     */
    public static void startPauseService(int sessionCreator) {
        if (!isActiveSession()) {
            Intent pauseIntent = new Intent(instance, PauseSessionService.class);
            instance.startService(pauseIntent);

            // Create new Pause Session
            currentPauseSession = new PauseSession(sessionCreator);
        }
    }

    /**
     * Stop current pause
     */
    public static void stopPauseService(int destroyer) {
        if (isActiveSession() && getCurrentSession().getCreator() == destroyer) {
            Intent pauseIntent = new Intent(instance, PauseSessionService.class);
            instance.stopService(pauseIntent);

            // Delete Pause Session
            // TODO
            currentPauseSession.deactivateSession();
        }
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

        //messageSender.sendMmsMessage("12482526955", parts);
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

    public static boolean isActiveSession() {
        return (currentPauseSession != null && currentPauseSession.isActive());
    }

    public static PauseSession getCurrentSession() {
        return currentPauseSession;
    }

    public static void checkForSleepMode() {
        if (isPhoneCharging() && isPhoneStill() && isSleepTime())
            startPauseService(Constants.Session.Creator.SLEEP);
        else
            if (PauseApplication.getCurrentSession() != null && PauseApplication.getCurrentSession().getCreator() == Constants.Session.Creator.SLEEP)
                stopPauseService(Constants.Session.Destroyer.SLEEP);
    }

    public static boolean isSleepTime() {
        boolean sleepTime = false;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if (hour >= Constants.Settings.SLEEP_TIME_START || hour < Constants.Settings.SLEEP_TIME_STOP)
            sleepTime = true;

        return sleepTime;
    }

    public static boolean isPhoneCharging() { return phoneIsCharging; }
    public static void setPhoneCharging(boolean isCharging) { phoneIsCharging = isCharging; }

    public static boolean isPhoneStill() { return phoneIsStill; }
    public static void setPhoneStill(boolean isStill) { phoneIsStill = isStill; }

    public static int getOldRingerMode() { return oldRingerMode; }
    public static void setOldRingerMode(int mode) { oldRingerMode = mode; }

    public static boolean isDriveModeAllowed() { return driveModeAllowed; }
    public static void setDriveModeAllowed(boolean isAllowed) { driveModeAllowed = isAllowed; }


    public static synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getInstance());
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
