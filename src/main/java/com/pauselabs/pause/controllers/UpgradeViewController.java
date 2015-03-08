package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.tabs.UpgradeView;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by Admin on 3/8/15.
 */
public class UpgradeViewController {

    @Inject
    LayoutInflater inflater;

    @InjectView(R.id.upgrade_view)
    public RelativeLayout upgradeView;

    public UpgradeViewController() {
        Injector.inject(this);

        upgradeView = (UpgradeView) inflater.inflate(R.layout.upgrade_view, null);
    }

}
