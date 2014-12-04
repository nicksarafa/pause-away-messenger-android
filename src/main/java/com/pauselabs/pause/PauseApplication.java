package com.pauselabs.pause;

import android.app.Application;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
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
import com.pauselabs.pause.listeners.NotificationActionListener;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseMMSPart;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.services.PauseApplicationService;
import com.pauselabs.pause.services.PauseSessionService;
import com.pauselabs.pause.ui.ScoreboardActivity;
import com.squareup.otto.Bus;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.inject.Inject;

public class PauseApplication extends Application {

    private static PauseApplication instance;
    private static final String TAG = PauseApplication.class.getSimpleName();
    public static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static NotificationManager notificationManager;
    @Inject protected Bus eventBus;

    public static PauseMessageSender messageSender;
    private static PauseSession currentPauseSession;
    private static boolean drawerOpen = false;

    private static boolean phoneIsCharging = false;
    private static boolean phoneIsStill= false;

    private static int oldRingerMode;

    private static boolean driveModeAllowed = true;

    public static int numSMS = 0;
    public static int numCall = 0;

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

        // Register the bus so we can send notifcations
//        eventBus.register(this);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

            updateNotifications();
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

            cancelNotifications();
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

    public static void updateNotifications() {
        updateMainNotification();
        updateChangeModeNotification();
    }

    private static void cancelNotifications() {
        notificationManager.cancel(Constants.Notification.SESSION_NOTIFICATION_ID);
        notificationManager.cancel(Constants.Notification.CHANGE_MODE_NOTIFICATION_ID);
    }

    /**
     * Creates a notification to show in the notification bar
     *
     * @return a new {@link android.app.Notification}
     */
    private static void updateMainNotification() {
        int num = getCurrentSession().getConversations().size();
        String message = (num > 0) ? num + ((num == 1) ? " person has" : " people have") + " contacted you." : "No one has contacted you";

        final Intent i = new Intent(instance, ScoreboardActivity.class);

        // open activity intent
        PendingIntent pendingIntent = PendingIntent.getActivity(instance, 0, i, 0);

        // stop session intent
        Intent stopPauseIntent = new Intent(instance, NotificationActionListener.class);
        stopPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.STOP_PAUSE_SESSION);
        PendingIntent stopPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), stopPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // edit session intent
        Intent editPauseIntent = new Intent(instance, NotificationActionListener.class);
        editPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.EDIT_PAUSE_SESSION);
        editPauseIntent.putExtra(Constants.Pause.EDIT_PAUSE_MESSAGE_ID_EXTRA, getCurrentSession().getActiveBounceBackMessage().getId());
        PendingIntent editPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), editPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // not sleeping intent
        Intent notSleepingPauseIntent = new Intent(instance, NotificationActionListener.class);
        notSleepingPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.NOT_SLEEPING);
        PendingIntent notSleepingPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), notSleepingPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // not driver intent
        Intent notDriverPauseIntent = new Intent(instance, NotificationActionListener.class);
        notDriverPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.NOT_DRIVER);
        PendingIntent notDriverPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), notDriverPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(instance);

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        String bigText = message + "\n";
        bigText += "\n" + numSMS + " missed Texts";
        bigText += "\n" + numCall + " missed Calls";
        bigStyle.bigText(bigText);

        notBuilder
                .setSmallIcon(R.drawable.ic_stat_pause_icon_pause)
                .setLargeIcon(BitmapFactory.decodeResource(instance.getResources(), R.drawable.ic_launcher))
                .setStyle(bigStyle)
                .setContentText(message)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        switch (getCurrentSession().getCreator()) {
            case Constants.Session.Creator.CUSTOM:
                notBuilder
                        .setContentTitle(instance.getString(R.string.app_name) + " " + instance.getString(R.string.pause_session_running_custom))
                        .addAction(R.drawable.ic_stat_notificaiton_end, "End", stopPausePendingIntent)
                        .addAction(R.drawable.ic_stat_notification_pencil, "Edit", editPausePendingIntent);

                break;
            case Constants.Session.Creator.SILENCE:
                notBuilder
                        .setContentTitle(instance.getString(R.string.app_name) + " " + instance.getString(R.string.pause_session_running_silence))
                        .addAction(R.drawable.ic_stat_notificaiton_end, "End", stopPausePendingIntent);

                break;
            case Constants.Session.Creator.SLEEP:
                notBuilder
                        .setContentTitle(instance.getString(R.string.app_name) + " " + instance.getString(R.string.pause_session_running_sleep))
                        .addAction(R.drawable.ic_stat_notificaiton_end, "Not Sleeping", notSleepingPausePendingIntent);

                break;
            case Constants.Session.Creator.DRIVE:
                notBuilder
                        .setContentTitle(instance.getString(R.string.app_name) + " " + instance.getString(R.string.pause_session_running_drive))
                        .addAction(R.drawable.ic_stat_notificaiton_end, "Not the Driver", notDriverPausePendingIntent);

                break;
        }

        notificationManager.notify(Constants.Notification.SESSION_NOTIFICATION_ID,notBuilder.build());
    }

    private static void updateChangeModeNotification() {

        // silence intent
        Intent silencePauseIntent = new Intent(instance, NotificationActionListener.class);
        silencePauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_SILENCE);
        PendingIntent silencePausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), silencePauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // silence intent
        Intent sleepPauseIntent = new Intent(instance, NotificationActionListener.class);
        silencePauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_SLEEP);
        PendingIntent sleepPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), sleepPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // silence intent
        Intent drivePauseIntent = new Intent(instance, NotificationActionListener.class);
        silencePauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_DRIVE);
        PendingIntent drivePausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), drivePauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder changeModeNotBuilder = new NotificationCompat.Builder(instance);

        changeModeNotBuilder
                .setSmallIcon(R.drawable.ic_stat_pause_icon_pause)
                .setLargeIcon(BitmapFactory.decodeResource(instance.getResources(), R.drawable.ic_launcher))
                .setContentTitle(instance.getString(R.string.app_name))
                .setContentText(instance.getString(R.string.pause_session_change))
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getBroadcast(instance, new Random().nextInt(), new Intent(), PendingIntent.FLAG_CANCEL_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_MIN);

        if (getCurrentSession().getCreator() != Constants.Session.Creator.SILENCE)
            changeModeNotBuilder.addAction(R.drawable.ic_stat_notificaiton_end, "Silence", silencePausePendingIntent);
        if (getCurrentSession().getCreator() != Constants.Session.Creator.SLEEP)
            changeModeNotBuilder.addAction(R.drawable.ic_stat_notificaiton_end, "Sleep", sleepPausePendingIntent);
        if (getCurrentSession().getCreator() != Constants.Session.Creator.DRIVE)
            changeModeNotBuilder.addAction(R.drawable.ic_stat_notificaiton_end, "Drive", drivePausePendingIntent);

        notificationManager.notify(Constants.Notification.CHANGE_MODE_NOTIFICATION_ID,changeModeNotBuilder.build());
    }

    public static synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getInstance());
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
