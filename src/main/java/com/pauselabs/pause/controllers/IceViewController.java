package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.tabs.IceView;

import javax.inject.Inject;

;

/**
 * Created by Admin on 1/28/15.
 */
public class IceViewController implements View.OnClickListener {

    public IceView iceView;

    @Inject
    LayoutInflater inflater;

    public IceViewController() {
        Injector.inject(this);
        
        iceView = (IceView)inflater.inflate(com.pauselabs.R.layout.ice_view,null);

        iceView.addIceBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
