package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.tabs.TimeBankView;

import javax.inject.Inject;

/**
 * Created by Admin on 3/8/15.
 */
public class TimeBankViewController {

    @Inject
    LayoutInflater inflater;

    public TimeBankView timeBankView;

//    public TimeBankAdapter timeBankAdapter;

    public TimeBankViewController() {
        Injector.inject(this);

        timeBankView = (TimeBankView) inflater.inflate(R.layout.time_bank_view, null);

    }

    public void updateUI() {

    }


}
