package com.pauselabs.pause.controller;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.MainActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.JsonReader;
import com.pauselabs.pause.view.HomeButton;
import com.pauselabs.pause.view.HomeButtonSeparator;
import com.pauselabs.pause.view.NoSessionView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by Passa on 12/26/14.
 */
public class OnBoardingViewController implements View.OnClickListener {

    public NoSessionView noSessionView;

    @Inject
    LayoutInflater inflater;
    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    public TextView pauseMessage;
    public LinearLayout buttonLayout;

    JsonReader jr;
    JSONObject mainObject;
    JSONArray components;
    int count = 0;

    Animation in = new AlphaAnimation(0.0f, 1.0f);
    Animation out = new AlphaAnimation(1.0f, 0.0f);
    AnimationSet as = new AnimationSet(true);

    public OnBoardingViewController() {
        Injector.inject(this);

        noSessionView = (NoSessionView) inflater.inflate(R.layout.no_session_view, null);

        pauseMessage = (TextView) noSessionView.findViewById(R.id.home_pause_message);
        buttonLayout = (LinearLayout) noSessionView.findViewById(R.id.home_button_layout);

        jr = new JsonReader(noSessionView.getContext(),"jasonBourne.json");
        mainObject = jr.getObject();

        in.setDuration(1000);
        out.setDuration(1000);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateUI();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void updateUI() {
        buttonLayout.removeAllViews();

        try {
            if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false)) {
                components = mainObject.getJSONArray("normalJason");
            } else {
                components = mainObject.getJSONArray("onBoardingProcess");
                count = prefs.getInt(Constants.Pause.ONBOARDING_NUMBER_KEY, 0);
            }

            JSONObject component = (JSONObject)components.get(count);

            String pauseMsg = component.getString("pauseMsg");
            JSONArray btnArray = component.getJSONArray("buttons");

            Pattern contactPattern = Pattern.compile("%name");
            Matcher matcher = contactPattern.matcher(pauseMsg);

            pauseMessage.setText(matcher.replaceAll(prefs.getString(Constants.Settings.NAME_KEY, "")));

            for (int i = 0; i < btnArray.length(); i++) {
                JSONObject btnObject = btnArray.getJSONObject(i);

                HomeButton newBtn = new HomeButton(noSessionView.getContext());
                newBtn.getButton().setId(btnObject.getInt("actionId"));
                newBtn.getButton().setText(btnObject.getString("btnText"));
                newBtn.getButton().setOnClickListener(this);

                HomeButtonSeparator separator = new HomeButtonSeparator(noSessionView.getContext());

                buttonLayout.addView(separator);
                buttonLayout.addView(newBtn);
            }

            if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false)) {
                HomeButtonSeparator separator = new HomeButtonSeparator(noSessionView.getContext());
                buttonLayout.addView(separator);

                HomeButton nextBtn = new HomeButton(noSessionView.getContext());
                nextBtn.getButton().setId(Constants.Settings.ACTION_CYCLE);
                nextBtn.getButton().setText("Next");
                nextBtn.getButton().setOnClickListener(this);

                buttonLayout.addView(nextBtn);
            }

            noSessionView.startAnimation(in);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case Constants.Settings.ACTION_CYCLE:
                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_SILENCE:
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_UNSILENCE:
                am.setRingerMode(PauseApplication.getOldRingerMode());

                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_FINISH:
                count = 0;
                prefs.edit().putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, true).apply();

                updateUI();

                break;
//            case Constants.Settings.ACTION_CHANGE_NAME:
//                PauseApplication.displayNameDialog(MainActivity.settingsViewController.settingsView.nameBtn);
//
//                break;
//            case Constants.Settings.ACTION_CHANGE_GENDER:
//                PauseApplication.displayGenderDialog(MainActivity.settingsViewController.settingsView.genderBtn);
//
//                break;
        }
    }

    private void cycle() {
        if (!prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false))
            prefs.edit().putInt(Constants.Pause.ONBOARDING_NUMBER_KEY, count + 1).apply();

        count = (count < components.length() - 1) ? ++count : 0;

        noSessionView.startAnimation(out);
    }
}
