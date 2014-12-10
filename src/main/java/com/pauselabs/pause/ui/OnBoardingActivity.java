package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pauselabs.R;
import com.pauselabs.pause.core.Constants;

import javax.inject.Inject;

import butterknife.Views;

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

        setContentView(R.layout.ob_name);

        Views.inject(this);

        name = (EditText)findViewById(R.id.ob_name);

        male = (Button)findViewById(R.id.male);
        female = (Button)findViewById(R.id.female);
        save = (Button)findViewById(R.id.ob_save);

        male.setOnClickListener(this);
        female.setOnClickListener(this);
        save.setOnClickListener(this);
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
                if ((male.isSelected() || female.isSelected()) && name.getText().toString() != "") {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.Pause.PAUSE_FIRST_LAUNCH_KEY,Constants.Pause.PAUSE_FIRST_LAUNCH_TRUE);
                    editor.putString(Constants.Settings.NAME, name.getText().toString());
                    editor.putString(Constants.Settings.GENDER,gender);
                    editor.apply();

                    startActivity(new Intent(this,MainActivity.class));
                }

                break;
        }
    }
}
