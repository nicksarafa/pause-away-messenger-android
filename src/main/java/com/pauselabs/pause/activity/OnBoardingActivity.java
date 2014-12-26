package com.pauselabs.pause.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;

import javax.inject.Inject;

/**
 * Created by Passa on 12/10/14.
 */
public class OnBoardingActivity extends Activity implements View.OnClickListener {

    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    EditText name;
    Button male, female;
    Drawable colorChanger;

    boolean isMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ob_name_gender);

        Injector.inject(this);

        if (prefs.getBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, false))
            startApp();
        else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, false);
            editor.putInt(Constants.Pause.ONBOARDING_NUMBER_KEY, 0);
            editor.apply();

            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2,AudioManager.FLAG_ALLOW_RINGER_MODES);
        }

        name = (EditText) findViewById(R.id.ob_name);

        male = (Button) findViewById(R.id.male);
        female = (Button) findViewById(R.id.female);

        male.setOnClickListener(this);
        female.setOnClickListener(this);
    }

    private void startApp() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (!name.getText().toString().equals("")) {
            male.setEnabled(false);
            female.setEnabled(false);

            Drawable dr = getResources().getDrawable(
                    R.drawable.btn_gender_pressed);
            dr.setColorFilter(Color.parseColor("#53FB6D"), PorterDuff.Mode.SRC_ATOP);

            switch (v.getId()) {
                case R.id.male:
                    isMale = true;

                    if (male == null) {
                        male = (Button) findViewById(v.getId());
                    } else {
                        male.setBackgroundResource(R.drawable.btn_gender_pressed);
                        male = (Button) findViewById(v.getId());
                    }

                    male.setBackgroundDrawable(dr);

                    break;

                case R.id.female:
                    isMale = false;

                    if (female == null) {
                        female = (Button) findViewById(v.getId());
                    } else {
                        female.setBackgroundResource(R.drawable.btn_gender_pressed);
                    female = (Button) findViewById(v.getId());
                    }

                    female.setBackgroundDrawable(dr);

                    break;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.Pause.PAUSE_ALREADY_LAUNCHED_KEY, true);
            editor.putString(Constants.Settings.NAME_KEY, name.getText().toString());
            editor.putBoolean(Constants.Settings.IS_MALE, isMale);
            editor.apply();

            startApp();
        } else {
            PauseApplication.sendToast("Please Enter Your Name First!");
        }
    }
}
