package com.pauselabs.pause.controller;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.TabViews.CustomPauseView;

import javax.inject.Inject;

/**
 * Created by Passa on 1/26/15.
 */
public class CustomPauseViewController implements View.OnClickListener {

    public CustomPauseView customPauseView;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public CustomPauseViewController() {
        Injector.inject(this);

        customPauseView = (CustomPauseView) inflater.inflate(R.layout.custom_pause_view, null);

        customPauseView.beginBtn.setOnClickListener(this);

        customPauseView.customTxtView.setText(prefs.getString(Constants.Pause.CUSTOM_PAUSE_MESSAGE_KEY, ""));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.begin && !customPauseView.customTxtView.getText().equals("")) {
            prefs.edit().putString(Constants.Pause.CUSTOM_PAUSE_MESSAGE_KEY, customPauseView.customTxtView.getText().toString()).apply();

            PauseApplication.startPauseService(Constants.Session.Creator.CUSTOM);
        }
    }
}
