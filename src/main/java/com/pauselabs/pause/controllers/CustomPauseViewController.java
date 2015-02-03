package com.pauselabs.pause.controllers;

import android.content.SharedPreferences;
import android.view.LayoutInflater;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.tabs.CustomPauseView;

import javax.inject.Inject;

/**
 * Created by Passa on 1/26/15.
 */
public class CustomPauseViewController {

    public CustomPauseView customPauseView;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public CustomPauseViewController() {
        Injector.inject(this);

        customPauseView = (CustomPauseView) inflater.inflate(R.layout.custom_pause_view, null);

        customPauseView.customTxtView.setText(prefs.getString(Constants.Pause.CUSTOM_PAUSE_MESSAGE_KEY, ""));


    }

}
