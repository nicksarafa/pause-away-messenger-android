package com.pauselabs.pause.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controllers.start.GenderViewController;
import com.pauselabs.pause.controllers.start.OnboardingViewController;
import com.pauselabs.pause.model.Constants;

import javax.inject.Inject;

/**
 * Created by Passa on 12/10/14.
 */
public class StartActivity extends Activity {

    public GenderViewController genderViewController;
    public OnboardingViewController onboardingViewController;

    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.getInstance().setDebugMode(true);
        Crashlytics.start(this);

        PauseApplication.startActivity = this;

        Injector.inject(this);

        genderViewController = new GenderViewController(this);
        onboardingViewController = new OnboardingViewController(this);

        if (!prefs.getBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, false))
            prefs.edit().putBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, true).apply();

        if (prefs.getString(Constants.Settings.NAME_KEY, null) != null) {
            if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false))
                startApp();
            else {
                showOnboarding();
            }
        } else {
            setToRinger(1);

            setContentView(genderViewController.genderView);
        }
    }

    public void setToRinger(int volume) {
        am.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }

    public void startApp() {
        startActivity(new Intent(this, PauseActivity.class));
    }

    public void showOnboarding() {
        setToRinger(1);

        setContentView(onboardingViewController.onboardingContainerView);
    }
}
