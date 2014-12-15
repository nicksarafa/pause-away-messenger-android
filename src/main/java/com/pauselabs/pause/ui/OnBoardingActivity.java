package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.SpeechListener;

import javax.inject.Inject;

/**
 * Created by Passa on 12/10/14.
 */
public class OnBoardingActivity extends Activity implements View.OnClickListener {

    @Inject
    SharedPreferences prefs;

    EditText name;
    Button male, female, save;

    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("OnBoarding","2");

        setContentView(R.layout.ob_0);

        Injector.inject(this);

        if (prefs.getString(Constants.Pause.PAUSE_FIRST_LAUNCH_KEY, "").equals(Constants.Pause.PAUSE_FIRST_LAUNCH_TRUE))
            startActivity(new Intent(this,MainActivity.class));

        name = (EditText)findViewById(R.id.ob_name);

        male = (Button)findViewById(R.id.male);
        female = (Button)findViewById(R.id.female);
        save = (Button)findViewById(R.id.ob_save);

        male.setOnClickListener(this);
        female.setOnClickListener(this);
        save.setOnClickListener(this);

        while(PauseApplication.tts.isSpeaking()) {}
        PauseApplication.sr.setRecognitionListener(new SpeechListener() {
            @Override
            public void onResults(Bundle results) {
                super.onResults(results);

                name.setText(lastResult);
            }
        });
        PauseApplication.sr.startListening(SpeechListener.getNewSpeechIntent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.male:
                female.setSelected(false);
                male.setSelected(true);
                Log.i("","male");

                gender = "he";

                break;
            case R.id.female:
                male.setSelected(false);
                female.setSelected(true);
                Log.i("","female");

                gender = "she";

                break;
            case R.id.ob_save:
                String nameString = name.getText().toString();
                if (/*(male.isSelected() || female.isSelected()) &&*/ name.getText().toString() != "") {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.Pause.PAUSE_FIRST_LAUNCH_KEY,Constants.Pause.PAUSE_FIRST_LAUNCH_TRUE);
                    editor.putString(Constants.Settings.NAME, name.getText().toString());
                    editor.putString(Constants.Settings.GENDER,gender);
                    editor.apply();

                    PauseApplication.tts.speak("It is a pleasure to be meeting you, " + nameString + "! Lets get started!", TextToSpeech.QUEUE_ADD, null);

                    startActivity(new Intent(this, MainActivity.class));
                }

                break;
        }
    }
}
