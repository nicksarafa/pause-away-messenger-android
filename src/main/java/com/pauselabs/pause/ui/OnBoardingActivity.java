package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.core.Constants;

import javax.inject.Inject;

/**
 * Created by Passa on 12/10/14.
 */
public class OnBoardingActivity extends Activity implements View.OnClickListener {

    @Inject
    SharedPreferences prefs;

    EditText name;
    Button male, female;
    Drawable colorChanger;

    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ob_0);

        Injector.inject(this);

        if (prefs.getString(Constants.Pause.PAUSE_FIRST_LAUNCH_KEY, "").equals(Constants.Pause.PAUSE_FIRST_LAUNCH_TRUE))
            startActivity(new Intent(this, MainActivity.class));

        name = (EditText) findViewById(R.id.ob_name);

        male = (Button) findViewById(R.id.male);
        female = (Button) findViewById(R.id.female);

        male.setOnClickListener(this);
        female.setOnClickListener(this);
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
                    gender = Constants.Settings.GENDER_MALE_KEY;

                    if (male == null) {
                        male = (Button) findViewById(v.getId());
                    } else {
                        male.setBackgroundResource(R.drawable.btn_gender_pressed);
                        male = (Button) findViewById(v.getId());
                    }

                    male.setBackgroundDrawable(dr);

                    break;

                case R.id.female:
                    gender = Constants.Settings.GENDER_FEMALE_KEY;

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
            editor.putString(Constants.Pause.PAUSE_FIRST_LAUNCH_KEY,Constants.Pause.PAUSE_FIRST_LAUNCH_TRUE);
            editor.putString(Constants.Settings.NAME_KEY, name.getText().toString());
            editor.putString(Constants.Settings.GENDER_KEY, gender);
            editor.apply();

            startActivity(new Intent(this,MainActivity.class));
        } else {
            Toast.makeText(this,"Please enter your name first!",Toast.LENGTH_LONG).show();
        }
    }
}
