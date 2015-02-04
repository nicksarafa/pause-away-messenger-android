package com.pauselabs.pause.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.crashlytics.android.Crashlytics;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.controllers.onboarding.GenderViewController;
import com.pauselabs.pause.controllers.onboarding.InteractiveViewController;
import com.pauselabs.pause.model.Constants;

import javax.inject.Inject;

/**
 * Created by Passa on 12/10/14.
 */
public class OnBoardingActivity extends Activity {

    public GenderViewController genderViewController;
    public InteractiveViewController interactiveViewController;

    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    public boolean genderSet;

    Animation in = new AlphaAnimation(0.0f, 1.0f);
    Animation out = new AlphaAnimation(1.0f, 0.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.getInstance().setDebugMode(true);
        Crashlytics.start(this);

        Injector.inject(this);


        genderViewController = new GenderViewController(this);
        interactiveViewController = new InteractiveViewController(this);

        if (prefs.getBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, false)) {
            if (prefs.getInt(Constants.Pause.ONBOARDING_NUMBER_KEY, -1) == -1 || prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false))
                startApp();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, false);
            editor.putInt(Constants.Pause.ONBOARDING_NUMBER_KEY, 0);
            editor.apply();

            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, AudioManager.FLAG_ALLOW_RINGER_MODES);
        }

        if (prefs.getString(Constants.Settings.NAME_KEY, null) == null) {
            setContentView(genderViewController.genderView);
            genderSet = false;
        } else {
            setContentView(interactiveViewController.interactiveView);
            interactiveViewController.updateUI(in);
            genderSet = true;
        }

        in.setDuration(600);
        out.setDuration(600);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                interactiveViewController.updateUI(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        genderViewController.genderView.startAnimation(in);
    }

    public void startApp() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void cycle() {
        if (!genderSet) {
            genderSet = true;

            genderViewController.genderView.startAnimation(out);

            setContentView(interactiveViewController.interactiveView);
        } else {
            interactiveViewController.interactiveView.startAnimation(out);

            if (!prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, false))
                prefs.edit().putInt(Constants.Pause.ONBOARDING_NUMBER_KEY, ++interactiveViewController.count).apply();
        }
    }
}
