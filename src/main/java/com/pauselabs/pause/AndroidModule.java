package com.pauselabs.pause;

import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Module for all Android related provisions
 */
@Module(complete = false, library = true)
public class AndroidModule {

    @Provides
    @Singleton
    Context provideAppContext() {
        return PauseApplication.getInstance().getApplicationContext();
    }

    @Provides
    SharedPreferences provideDefaultSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    AudioManager provideAudioManager() { return (AudioManager) PauseApplication.getInstance().getSystemService(PauseApplication.AUDIO_SERVICE); }

    @Provides
    PackageInfo providePackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    LayoutInflater provideInflator() {
        return (LayoutInflater) PauseApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Provides
    TelephonyManager provideTelephonyManager(Context context) {
        return (TelephonyManager) PauseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Provides
    InputMethodManager provideInputMethodManager(final Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides
    ApplicationInfo provideApplicationInfo(final Context context) {
        return context.getApplicationInfo();
    }

    @Provides
    AccountManager provideAccountManager(final Context context) {
        return AccountManager.get(context);
    }

    @Provides
    ClassLoader provideClassLoader(final Context context) {
        return context.getClassLoader();
    }

    @Provides
    NotificationManager provideNotificationManager(final Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
