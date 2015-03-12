package com.pauselabs.pause.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
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

    public PrivacyViewController() {
        Injector.inject(this);

        // Set visibility to hidden by default
//        privacyBtns.atnBtn1.setVisibility(View.INVISIBLE);
//        privacyBtns.atnBtn1.setOnClickListener(this);

        privacyView = (PrivacyView) inflater.inflate(R.layout.privacy_view, null);
        privacyBtns = (PrivacyActionBtnView) inflater.inflate(R.layout.privacy_action_view, null);

        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacy_search_activity_start_btn:
                Log.i("PrivacyController", "Switching to PrivacySearchView");

                break;
        }

    }
}
