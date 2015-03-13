package com.pauselabs.pause.controllers;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.SearchPrivacyActivity;
import com.pauselabs.pause.view.tabs.PrivacyActionBtnView;
import com.pauselabs.pause.view.tabs.PrivacyView;

import javax.inject.Inject;

/**
 * Created by Admin on 3/8/15.
 */
public class PrivacyViewController implements View.OnClickListener {

    private final String TAG = PrivacyViewController.class.getSimpleName();

    public PrivacyView privacyView;
    public PrivacyActionBtnView privacyBtns;

    @Inject
    LayoutInflater inflater;
    @Inject 

    public PrivacyViewController() {
        Injector.inject(this);

        privacyView = (PrivacyView) inflater.inflate(R.layout.privacy_view, null);
        privacyBtns = (PrivacyActionBtnView) inflater.inflate(R.layout.privacy_action_view, null);

        // Set visibility to hidden by default
        privacyBtns.atnBtn1.setOnClickListener(this);

        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacy_search_activity_start_btn:
                Log.i("PrivacyController", "Switching to PrivacySearchView");

                PauseApplication.pauseActivity.startActivity(new Intent(PauseApplication.pauseActivity,SearchPrivacyActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                break;
        }

    }
}
