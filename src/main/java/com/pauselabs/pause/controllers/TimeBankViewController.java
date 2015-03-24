package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.tabs.TimeBankActionBtnView;
import com.pauselabs.pause.view.tabs.TimeBankView;

import javax.inject.Inject;

/**
 * Created by Admin on 3/8/15.
 */
public class TimeBankViewController implements View.OnClickListener {

    public TimeBankView timeBankView;
    public TimeBankActionBtnView timeBankActionBtnView;

    @Inject
    LayoutInflater inflater;

    public TimeBankViewController() {
        Injector.inject(this);

        timeBankView = (TimeBankView) inflater.inflate(R.layout.time_bank_view, null);
        timeBankActionBtnView = (TimeBankActionBtnView) inflater.inflate(R.layout.time_bank_action_bar_icon_view, null);

        timeBankActionBtnView.timeBankActionBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        System.out.println("Time Bank Button clicked");

    }

    public void updateUI() {

    }


}
