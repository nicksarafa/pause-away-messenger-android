package com.pauselabs.pause;

import android.app.Application;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pauselabs.R;
import com.pauselabs.pause.activity.MainActivity;
import com.pauselabs.pause.core.PauseMessageSender;
import com.pauselabs.pause.listeners.NotificationActionListener;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseMessage;
import com.pauselabs.pause.model.PauseSession;
import com.pauselabs.pause.services.PauseApplicationService;
import com.pauselabs.pause.services.PauseSessionService;
import com.squareup.otto.Bus;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

public class PauseApplication extends Application {

    private static PauseApplication instance;
    public static MainActivity mainActivity;

    private static final String TAG = PauseApplication.class.getSimpleName();
    public static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static NotificationManager notificationManager;
    @Inject protected Bus eventBus;

    static SharedPreferences prefs;

    public static TextToSpeech tts;

    public static PauseMessageSender messageSender;
    private static PauseSession currentPauseSession;

    public static SpeechRecognizer sr;

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

        if (instance == null) {
            // Perform injection
            Injector.init(getRootModule(), this);

            instance = this;

//            if ( BuildConfig.USE_CRASHLYTICS ) {
//                Crashlytics.start(instance);
//            }

            prefs = PreferenceManager.getDefaultSharedPreferences(instance);

            // Register the bus so we can send notifcations
            //        eventBus.register(this);

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            initImageLoader(getApplicationContext());

            messageSender = new PauseMessageSender(instance);

            startPauseApplicationService();

            sr = SpeechRecognizer.createSpeechRecognizer(instance);

            tts = new TextToSpeech(instance,new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.getDefault());
                        tts.setSpeechRate(0.9f);
                        tts.setPitch(1.45f);

                        // The app has not been opened yet. Play intro voice.
                        if (!prefs.getBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, false))
                            speak("Hello. I am your new personal assistant. What's your name?");
                    }
                }
            });
        }
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
            if (getCurrentSession() != null && getCurrentSession().getCreator() == Constants.Session.Creator.SLEEP)
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

    /**
     * Creates a notification to show in the notification bar
     *
     * @return a new {@link android.app.Notification}
     */
    public static void updateNotifications() {
        notificationManager.notify(Constants.Notification.SESSION_NOTIFICATION_ID, updateMainNotification());
//        updateChangeModeNotification();
    }
//
//    private static void cancelNotifications() {
//        notificationManager.cancel(Constants.Notification.SESSION_NOTIFICATION_ID);
//        notificationManager.cancel(Constants.Notification.CHANGE_MODE_NOTIFICATION_ID);
//    }

    public static Notification updateMainNotification() {
        int num = 0;
        for (PauseConversation convo: getCurrentSession().getConversations())
            if (convo.getMessagesReceived().size() != 0)
                num++;
        String message = (num > 0) ? num + ((num == 1) ? " person has" : " people have") + " contacted you." : "No one has contacted you";

        final Intent i = new Intent(instance, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // open activity intent
        PendingIntent pendingIntent = PendingIntent.getActivity(instance, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // edit session intent
        Intent editPauseIntent = new Intent(instance, NotificationActionListener.class);
        editPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.EDIT_PAUSE_SESSION);
        PendingIntent editPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), editPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // stop session intent
        Intent stopPauseIntent = new Intent(instance, NotificationActionListener.class);
        stopPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.STOP_PAUSE_SESSION);
        PendingIntent stopPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), stopPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // not sleeping intent
        Intent notSleepingPauseIntent = new Intent(instance, NotificationActionListener.class);
        notSleepingPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.NOT_SLEEPING);
        PendingIntent notSleepingPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), notSleepingPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // not driver intent
        Intent notDriverPauseIntent = new Intent(instance, NotificationActionListener.class);
        notDriverPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.NOT_DRIVER);
        PendingIntent notDriverPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), notDriverPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // cusomt intent
        Intent customPauseIntent = new Intent(instance, NotificationActionListener.class);
        customPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_CUSTOM);
        PendingIntent customPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), customPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // sleep intent
        Intent sleepPauseIntent = new Intent(instance, NotificationActionListener.class);
        sleepPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_SLEEP);
        PendingIntent sleepPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), sleepPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // drive intent
        Intent drivePauseIntent = new Intent(instance, NotificationActionListener.class);
        drivePauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_DRIVE);
        PendingIntent drivePausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), drivePauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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
                        .addAction(R.drawable.ic_stat_notificaiton_end, "Edit", editPausePendingIntent);
//                        .addAction(R.drawable.ic_action_sleep, "Sleep", sleepPausePendingIntent);


                break;
            case Constants.Session.Creator.SILENCE:
                notBuilder
                        .setContentTitle(instance.getString(R.string.app_name) + " " + instance.getString(R.string.pause_session_running_silence))
                        .addAction(R.drawable.ic_stat_notificaiton_end, "End", stopPausePendingIntent);
//                        .addAction(R.drawable.ic_icon_steering_wheel, "Drive", drivePausePendingIntent)
//                        .addAction(R.drawable.ic_action_sleep, "Sleep", sleepPausePendingIntent);


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
            case Constants.Session.Creator.FLIP:
                notBuilder
                        .setContentTitle(instance.getString(R.string.app_name) + " " + instance.getString(R.string.pause_session_running_silence))
                        .addAction(R.drawable.ic_stat_notificaiton_end, "End", stopPausePendingIntent);

                break;
        }

        return notBuilder.build();
    }

    private static void updateChangeModeNotification() {

        // silence intent
        Intent silencePauseIntent = new Intent(instance, NotificationActionListener.class);
        silencePauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_SILENCE);
        PendingIntent silencePausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), silencePauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // silence intent
        Intent sleepPauseIntent = new Intent(instance, NotificationActionListener.class);
        sleepPauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_SLEEP);
        PendingIntent sleepPausePendingIntent = PendingIntent.getBroadcast(instance, new Random().nextInt(), sleepPauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // silence intent
        Intent drivePauseIntent = new Intent(instance, NotificationActionListener.class);
        drivePauseIntent.putExtra(Constants.Notification.PAUSE_NOTIFICATION_INTENT, Constants.Notification.MODE_DRIVE);
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

    public static void handleMessageReceived(PauseMessage receivedMessage) {
        currentPauseSession = getCurrentSession();

        PauseConversation conversation = currentPauseSession.getConversationByContactNumber(receivedMessage.getFrom());
        conversation.addMessage(receivedMessage);

        String contactId = lookupContact(receivedMessage.getFrom());

        if (currentPauseSession.isIced(contactId)) {
                // TODO not playing sound

                AudioManager manager = (AudioManager)instance.getSystemService(Context.AUDIO_SERVICE);
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                MediaPlayer player = MediaPlayer.create(instance, notification);
                player.start();
        }

        if (currentPauseSession.shouldSenderReceivedBounceback(contactId,receivedMessage.getType()) && conversation.getMessagesSentFromUser().size() == 0) {
            PauseMessage bounceBackMessage = getMessageToBounceBack(receivedMessage.getFrom(), conversation);
            conversation.addMessage(bounceBackMessage);
            messageSender.sendSmsMessage(bounceBackMessage.getTo(), bounceBackMessage);

            currentPauseSession.incrementResponseCount();
        } else {
            sendToast("Ignored " + receivedMessage.getTypeString() + " from " + conversation.getContactName());
        }

        updateNotifications();
        updateUI();
    }

    public static void handleMessageSent(PauseMessage sentMessage) {
        currentPauseSession = getCurrentSession();

        PauseConversation conversation = currentPauseSession.getConversationByContactNumber(sentMessage.getTo());
        conversation.addMessage(sentMessage);

        if (conversation.getMessagesSentFromUser().size() == 1)
            PauseApplication.sendToast("I will no longer reply to " + conversation.getContactName() + " until your next Pause.");

        updateNotifications();
        updateUI();
    }

    public static String lookupContact(String contactNumber) {
        String contactId = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber));

        ContentResolver contentResolver = instance.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();

                contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));

            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return contactId;

    }

    public static PauseMessage getMessageToBounceBack(String to, PauseConversation conversation) {
        return new PauseMessage(
                "0",
                to,
                conversation.getStringForBounceBackMessage(),
                new Date().getTime(),
                Constants.Message.Type.SMS_PAUSE_OUTGOING
        );
    }

    public static void updateUI() {
        if (mainActivity != null) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PauseApplication.mainActivity.updateView();
                }
            });
        }
    }

    private static Handler toastHandler = new Handler() {
        public void handleMessage(Message msg) {
            String message = (String)msg.obj;

            SuperToast superToast = new SuperToast(instance);
            superToast.setDuration(SuperToast.Duration.MEDIUM);
            superToast.setText(message);
            superToast.setBackground(R.drawable.toast_card_background);
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setTextColor(Color.WHITE);
            superToast.setIcon(R.drawable.ic_action_pause_on, SuperToast.IconPosition.LEFT);
            ((TextView)((LinearLayout)superToast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
            superToast.show();



//            Toast toast = Toast.makeText(instance, message, Toast.LENGTH_LONG);
//            ((TextView)((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
//            toast.show();

        }
    };

    public static void sendToast(final String textToSend) {
        if (prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY,true)) {
            Message msg = new Message();
            msg.obj = textToSend;
            toastHandler.sendMessage(msg);
        }
    }

    public static void speak(String textToSpeak) {
        if (prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY,false)) {
            tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
    }
    public static void stopSpeaking() {
        tts.stop();
    }

}
