package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.ComponentRandomizer;
import com.pauselabs.pause.views.HomeButton;
import com.pauselabs.pause.views.HomeButtonSeparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Views;

/**
 * Created by Sarafa on 12/8/14.
 */

public class HomeActivity extends Activity implements View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    public SettingsLayout settingsLayout;

    RelativeLayout contentLayout;
    LinearLayout buttonLayout;

    ComponentRandomizer cr;

    TextView pauseMessage;

    //ImageView displaySettingsBtn;

    int count = 0;

    Animation in = new AlphaAnimation(0.0f, 1.0f);
    Animation out = new AlphaAnimation(1.0f, 0.0f);
    AnimationSet as = new AnimationSet(true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        Views.inject(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_button_view, (ViewGroup) findViewById(R.id.home_activity), false);
        inflater.inflate(R.layout.home_button_separator, (ViewGroup) findViewById(R.id.home_activity), false);

        contentLayout = (RelativeLayout)findViewById(R.id.home_activity);
        buttonLayout = (LinearLayout)findViewById(R.id.home_button_layout);
        pauseMessage = (TextView)findViewById(R.id.home_pause_message);

        settingsLayout = new SettingsLayout(this);

        cr = new ComponentRandomizer(this,"jasonBourne.json");

//        as.addAnimation(out);
//        in.setStartOffset(1000);
//        as.addAnimation(in);

        in.setDuration(1000);
        out.setDuration(1000);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateView(++count);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        updateView(count);
    }

    private void updateView(int num) {
        buttonLayout.removeAllViews();

        ArrayList<JSONObject> objects = cr.getComponents();
        try {
            String pauseMsg = objects.get(num).getString("pauseMsg");
            JSONArray btnArray = objects.get(num).getJSONArray("buttons");

            pauseMessage.setText(pauseMsg);

            for (int i = 0; i < btnArray.length(); i++) {
                JSONObject btnObject = btnArray.getJSONObject(i);

                HomeButton newBtn = new HomeButton(this);
                newBtn.getButton().setId(btnObject.getInt("actionId"));
                newBtn.getButton().setText(btnObject.getString("btnText"));
                newBtn.getButton().setOnClickListener(this);

                HomeButtonSeparator separator = new HomeButtonSeparator(this);

                buttonLayout.addView(separator);
                buttonLayout.addView(newBtn);
            }

            HomeButtonSeparator separator = new HomeButtonSeparator(this);
            buttonLayout.addView(separator);

            HomeButton nextBtn = new HomeButton(this);
            nextBtn.getButton().setId(Constants.Settings.ACTION_CYCLE);
            nextBtn.getButton().setText("Next");
            nextBtn.getButton().setOnClickListener(this);

            buttonLayout.addView(nextBtn);

            contentLayout.startAnimation(in);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.displaySettingsBtn:
//                startActivity(new Intent(this, SettingsActivity.class));
//                break;
            case Constants.Settings.ACTION_CYCLE:
                contentLayout.startAnimation(out);

                break;
            case Constants.Settings.ACTION_CHANGE_NAME:
                PauseApplication.displayNameDialog(this, settingsLayout);

                break;
            case Constants.Settings.ACTION_CHANGE_GENDER:
                PauseApplication.displayGenderDialog(this, settingsLayout);

                break;
        }
    }
}
