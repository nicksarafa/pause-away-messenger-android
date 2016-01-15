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
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pauselabs.R;
import com.pauselabs.pause.activity.PauseActivity;
import com.pauselabs.pause.activity.StartActivity;
import com.pauselabs.pause.core.PauseMessageSender;
import com.pauselabs.pause.listeners.NotificationActionListener;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.Parse.Feature;
import com.pauselabs.pause.model.Parse.GlobalVars;
import com.pauselabs.pause.model.Parse.User;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseMessage;
import com.pauselabs.pause.model.PauseSession;
import com.pauselabs.pause.services.PauseApplicationService;
import com.pauselabs.pause.services.PauseSessionService;
import com.squareup.otto.Bus;

import io.fabric.sdk.android.Fabric;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import bolts.Continuation;
import bolts.Task;

public class PauseApplication extends Application {

    private static PauseApplication instance;
    public static StartActivity startActivity;
    public static PauseActivity pauseActivity;

    private static final String TAG = PauseApplication.class.getSimpleName();
    public static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static NotificationManager notificationManager;
    @Inject protected Bus eventBus;

    public static GlobalVars parseVars;

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
        Fabric.with(this, new Crashlytics());

        if (instance == null) {
            // Perform injection
            Injector.init(getRootModule(), this);

            instance = this;

//            if ( BuildConfig.USE_CRASHLYTICS ) {
//                Crashlytics.start(instance);
//            }

//            Crashlytics.getInstance().setDebugMode(true);
//            Crashlytics.start(this);

            ParseObject.registerSubclass(User.class);
            ParseObject.registerSubclass(Feature.class);
            Parse.initialize(this, Constants.Pause.Parse.APP_ID, Constants.Pause.Parse.CLIENT_KEY);

            parseVars = new GlobalVars();

            // Check if user exists to login, else register
            try {
                String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                ParseQuery<User> query = ParseQuery.getQuery(User.class);
                query.whereEqualTo("objectId", android_id);
                boolean userExists = (query.find().size() == 1);

                if (User.getCurrentUser() == null && !userExists) {
                    final User newUser = new User();
                    newUser.setUsername(android_id);
                    newUser.setPassword(android_id);

                    newUser.signUpInBackground().continueWith(new Continuation<Void, Object>() {
                        @Override
                        public Object then(Task<Void> task) throws Exception {
                            parseVars.setCurrentUser(newUser);

                            return null;
                        }
                    });
                } else {
                    User.logInInBackground(android_id, android_id).continueWith(new Continuation<ParseUser, Object>() {
                        @Override
                        public Object then(Task<ParseUser> task) throws Exception {
                            parseVars.setCurrentUser(task.getResult());

                            return null;
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
        Intent i;

        String message;
        if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, false)) {
            int num = 0;
            for (PauseConversation convo : getCurrentSession().getConversations())
                if (convo.getMessagesReceived().size() != 0)
                    num++;
            message = (num > 0) ? num + ((num == 1) ? " person has" : " people have") + " contacted you." : "No one has contacted you";

            i = new Intent(instance, PauseActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            message = "Please finish our onboarding process to use the apps features.";

            i = new Intent(instance, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

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

        // custom intent
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
                .setLargeIcon(BitmapFactory.decodeResource(instance.getResources(), R.mipmap.ic_launcher))
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
            case Constants.Session.Creator.VOLUME:
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
                .setLargeIcon(BitmapFactory.decodeResource(instance.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(instance.getString(R.string.app_name))
                .setContentText(instance.getString(R.string.pause_session_change))
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getBroadcast(instance, new Random().nextInt(), new Intent(), PendingIntent.FLAG_CANCEL_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_MIN);

        if (getCurrentSession().getCreator() != Constants.Session.Creator.VOLUME)
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
        if (pauseActivity != null) {
            pauseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PauseApplication.pauseActivity.updateUI();
                }
            });
        }
    }

    public static void sendToast(final String textToSend) {
        sendToast(textToSend, SuperToast.Duration.VERY_SHORT, R.drawable.toast_card_bg_pause_on);
    }
    public static void sendToast(final String textToSend, final int duration) {
        sendToast(textToSend, duration);
    }
    public static void sendToast(final String textToSend, final int duration, final int background) {
        sendToast(textToSend, duration, background, SuperToast.Animations.FLYIN);
    }
    public static void sendToast(final String textToSend, final int duration, final int background, final SuperToast.Animations animation) {
        sendToast(textToSend, duration, background, animation, Color.WHITE);
    }
    public static void sendToast(final String textToSend, final int duration, final int background, final SuperToast.Animations animation, final int textColor) {
        sendToast(textToSend, duration, background, animation, textColor, R.drawable.ic_action_pause_on);
    }
    public static void sendToast(final String textToSend, final int duration, final int background, final SuperToast.Animations animation, final int textColor, final int icon) {
        sendToast(textToSend, duration, background, animation, textColor, icon, SuperToast.IconPosition.LEFT);
    }
    public static void sendToast(final String textToSend, final int duration, final int background, final SuperToast.Animations animation, final int textColor, final int icon, final SuperToast.IconPosition iconPosition) {
        if (prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_KEY,Constants.Settings.DEFAULT_PAUSE_SHOW_TOASTS)) {
            new Runnable() {
                @Override
                public void run() {
                    SuperToast superToast = new SuperToast(instance);
                    superToast.setText(textToSend);
                    superToast.setDuration(duration);
                    superToast.setBackground(background);
                    superToast.setAnimations(animation);
                    superToast.setTextColor(textColor);
                    superToast.setIcon(icon, iconPosition);
                    ((TextView)((LinearLayout)superToast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                    superToast.show();
                }
            }.run();
        }
    }

    public static void speak(String textToSpeak) {
        if (prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY,Constants.Settings.DEFAULT_PAUSE_VOICE_FEEDBACK)) {
            tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
    }
    public static void stopSpeaking() {
        tts.stop();
    }

}
