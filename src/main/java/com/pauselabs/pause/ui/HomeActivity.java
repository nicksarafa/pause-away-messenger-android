package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.ComponentRandomizer;
import com.pauselabs.pause.views.HomeButton;

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

    LinearLayout layout;

    ComponentRandomizer cr;

    TextView pauseMessage;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        layout = (LinearLayout)findViewById(R.id.home_button_layout);
        pauseMessage = (TextView)findViewById(R.id.home_pause_message);

        Views.inject(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_button_view, (ViewGroup) findViewById(R.id.home_activity), false);

        cr = new ComponentRandomizer(this,"jasonBourne.json");

        updateView(count);
    }

    private void updateView(int num) {

        layout.removeAllViews();

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

                layout.addView(newBtn);
            }

            HomeButton nextBtn = new HomeButton(this);
            nextBtn.getButton().setId(Constants.Settings.ACTION_CYCLE);
            nextBtn.getButton().setText("Next");
            nextBtn.getButton().setOnClickListener(this);

            Log.i(TAG, "Next Button Created");

            layout.addView(nextBtn);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case Constants.Settings.ACTION_CYCLE:
                updateView(count++);

                break;
            case Constants.Settings.ACTION_CHANGE_NAME:
                PauseApplication.displayNameDialog(this);

                break;
            case Constants.Settings.ACTION_CHANGE_GENDER:
                PauseApplication.displayGenderDialog(this);

                break;
        }
    }
}
