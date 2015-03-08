package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.tabs.PrivacyView;

import javax.inject.Inject;

/**
 * Created by Admin on 3/8/15.
 */
public class PrivacyViewController {

    public PrivacyView privacyView;

    @Inject
    LayoutInflater inflater;

    public PrivacyViewController() {
        Injector.inject(this);

        privacyView = (PrivacyView) inflater.inflate(R.layout.privacy_view, null);
    }
}
