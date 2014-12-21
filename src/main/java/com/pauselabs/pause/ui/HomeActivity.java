package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.JsonReader;
import com.pauselabs.pause.views.HomeButton;
import com.pauselabs.pause.views.HomeButtonSeparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.Views;

/**
 * Created by Sarafa on 12/8/14.
 */

public class HomeActivity extends Activity implements View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    public SettingsLayout settingsLayout;

    LinearLayout layout;

    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;
    int lastRingerMode;

    JsonReader jr;

    TextView pauseMessage;

    //ImageView displaySettingsBtn;

    JSONObject mainObject;
    JSONArray components;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        Injector.inject(this);
        Views.inject(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_button_view, (ViewGroup) findViewById(R.id.home_activity), false);
        inflater.inflate(R.layout.home_button_separator, (ViewGroup) findViewById(R.id.home_activity), false);

        layout = (LinearLayout)findViewById(R.id.home_button_layout);
        pauseMessage = (TextView)findViewById(R.id.home_pause_message);

        settingsLayout = new SettingsLayout(this);

        jr = new JsonReader(this,"jasonBourne.json");
        mainObject = jr.getObject();

        updateView();
    }




    private void updateView() {
        layout.removeAllViews();

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

            pauseMessage.setText(matcher.replaceAll(prefs.getString(Constants.Settings.NAME_KEY,"")));

            for (int i = 0; i < btnArray.length(); i++) {
                JSONObject btnObject = btnArray.getJSONObject(i);

                HomeButton newBtn = new HomeButton(this);
                newBtn.getButton().setId(btnObject.getInt("actionId"));
                newBtn.getButton().setText(btnObject.getString("btnText"));
                newBtn.getButton().setOnClickListener(this);

                HomeButtonSeparator separator = new HomeButtonSeparator(this);

                layout.addView(separator);
                layout.addView(newBtn);
            }

            if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false)) {
                HomeButtonSeparator separator = new HomeButtonSeparator(this);
                layout.addView(separator);

                HomeButton nextBtn = new HomeButton(this);
                nextBtn.getButton().setId(Constants.Settings.ACTION_CYCLE);
                nextBtn.getButton().setText("Next");
                nextBtn.getButton().setOnClickListener(this);

                layout.addView(nextBtn);
            }
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
                lastRingerMode = am.getRingerMode();
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_UNSILENCE:
                am.setRingerMode(lastRingerMode);

                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_FINISH:
                count = 0;
                prefs.edit().putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, true).apply();

                updateView();

                break;
            case Constants.Settings.ACTION_CHANGE_NAME:
                PauseApplication.displayNameDialog(this, settingsLayout);

                break;
            case Constants.Settings.ACTION_CHANGE_GENDER:
                PauseApplication.displayGenderDialog(this, settingsLayout);

                break;
        }
    }

    private void cycle() {
        if (!prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false))
            prefs.edit().putInt(Constants.Pause.ONBOARDING_NUMBER_KEY, count + 1).apply();

        count = (count < components.length() - 1) ? ++count : 0;

        updateView();
    }
}
