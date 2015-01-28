package com.pauselabs.pause.controllers.onboarding;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.OnBoardingActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.JsonReader;
import com.pauselabs.pause.view.HomeButton;
import com.pauselabs.pause.view.HomeButtonSeparator;
import com.pauselabs.pause.view.InteractiveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by Passa on 1/28/15.
 */
public class InteractiveViewController implements View.OnClickListener {

    public InteractiveView interactiveView;

    @Inject
    LayoutInflater inflater;
    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    JsonReader jr;
    JSONObject mainObject;
    public JSONArray components;
    public int count;

    OnBoardingActivity onBoardingActivity;

    public InteractiveViewController(OnBoardingActivity activity) {
        Injector.inject(this);

        onBoardingActivity = activity;

        interactiveView = (InteractiveView) inflater.inflate(R.layout.interactive_view, null);

        jr = new JsonReader(onBoardingActivity,"jasonBourne.json");
        mainObject = jr.getObject();
    }

    public void updateUI(Animation in) {
        interactiveView.buttonLayout.removeAllViews();

        try {
            components = mainObject.getJSONArray("onBoardingProcess");
            count = prefs.getInt(Constants.Pause.ONBOARDING_NUMBER_KEY, 0);

            JSONObject component = (JSONObject) components.get(count);

            String pauseMsg = component.getString("pauseMsg");
            JSONArray btnArray = component.getJSONArray("buttons");

            Pattern contactPattern = Pattern.compile("%name");
            Matcher matcher = contactPattern.matcher(pauseMsg);

            interactiveView.pauseMessage.setText(matcher.replaceAll(prefs.getString(Constants.Settings.NAME_KEY, "")));

            for (int i = 0; i < btnArray.length(); i++) {
                JSONObject btnObject = btnArray.getJSONObject(i);

                HomeButton newBtn = new HomeButton(onBoardingActivity);
                newBtn.getButton().setId(btnObject.getInt("actionId"));
                newBtn.getButton().setText(btnObject.getString("btnText"));
                newBtn.getButton().setOnClickListener(this);

                HomeButtonSeparator separator = new HomeButtonSeparator(onBoardingActivity);

                interactiveView.buttonLayout.addView(separator);
                interactiveView.buttonLayout.addView(newBtn);
            }

            interactiveView.startAnimation(in);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case Constants.Settings.ACTION_CYCLE:
                onBoardingActivity.cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_SILENCE:
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                onBoardingActivity.cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_UNSILENCE:
                am.setRingerMode(PauseApplication.getOldRingerMode());

                onBoardingActivity.cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_FINISH:
                count = -1;
                prefs.edit().putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, true).apply();

                onBoardingActivity.startApp();

                break;
        }
    }
}
