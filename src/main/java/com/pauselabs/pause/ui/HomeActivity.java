package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.models.ComponentRandomizer;
import com.pauselabs.pause.models.StringRandomizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Views;

/**
 * Created by Sarafa on 12/8/14.
 */

public class HomeActivity extends Activity implements View.OnClickListener {

    ComponentRandomizer cr;
    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        Views.inject(this);

        cr = new ComponentRandomizer(this,"jasonBourne.json");

        object = cr.getComponent();
        updateView();
    }

    private void updateView() {
        try {
            String pauseMsg = object.getString("pauseMsg");
            JSONArray btnArray = object.getJSONArray("buttons");

            Log.i("HomeActivity",pauseMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
