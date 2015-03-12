package com.pauselabs.pause.controllers.onboarding;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.OnBoardingActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.GenderView;

import javax.inject.Inject;

/**
 * Created by Passa on 1/28/15.
 */
public class GenderViewController implements View.OnClickListener {

    public GenderView genderView;

    @Inject
    LayoutInflater inflater;
    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    boolean isMale;

    OnBoardingActivity onBoardingActivity;

    public GenderViewController(OnBoardingActivity activity) {
        Injector.inject(this);

        onBoardingActivity = activity;

        genderView = (GenderView) inflater.inflate(R.layout.ob_gender_first_view, null);

        genderView.male.setOnClickListener(this);
        genderView.female.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (!genderView.name.getText().toString().equals("")) {
            genderView.male.setEnabled(false);
            genderView.female.setEnabled(false);

            switch (v.getId()) {
                case R.id.male:
                    isMale = true;

                    genderView.male.setBackgroundResource(R.drawable.btn_gender_pressed);

                    break;

                case R.id.female:
                    isMale = false;

                    genderView.female.setBackgroundResource(R.drawable.btn_gender_pressed);

                    break;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, true);
            editor.putString(Constants.Settings.NAME_KEY, genderView.name.getText().toString());
            editor.putBoolean(Constants.Settings.IS_MALE, isMale);
            editor.apply();

            genderView.name.onEditorAction(EditorInfo.IME_ACTION_DONE);

            onBoardingActivity.cycle();
        } else {
            PauseApplication.sendToast("Please Enter Your Name First!");
        }
    }
}
