package com.pauselabs.pause.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.tabs.PrivacyActionBtnView;
import com.pauselabs.pause.view.tabs.PrivacyView;

import javax.inject.Inject;

;

/**
 * Created by Admin on 1/28/15.
 */
public class PrivacyViewController implements View.OnClickListener {

    public PrivacyView privacyView;
    public PrivacyActionBtnView privacyBtns;

    @Inject
    LayoutInflater inflater;

    public PrivacyViewController() {
        Injector.inject(this);
        
        privacyView = (PrivacyView)inflater.inflate(R.layout.privacy_view,null);
        privacyBtns = (PrivacyActionBtnView)inflater.inflate(R.layout.privacy_action_view, null);
        // Set visibility to hidden by default
        privacyBtns.setVisibility(View.INVISIBLE);

        privacyBtns.atnBtn1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ice_atn_1:
                Log.i("IceController","Action Button 1 Pressed");

                break;
        }
    }
}
