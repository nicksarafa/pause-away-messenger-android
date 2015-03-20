package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.parse.ParseException;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.adapters.UpgradeAdapter;
import com.pauselabs.pause.model.Parse.Feature;
import com.pauselabs.pause.model.Parse.User;
import com.pauselabs.pause.view.UpgradeListItem;
import com.pauselabs.pause.view.tabs.UpgradeView;

import javax.inject.Inject;

/**
 * Created by Admin on 3/8/15.
 */
public class UpgradeViewController {

    @Inject
    LayoutInflater inflater;

    public UpgradeView upgradeView;

    public UpgradeAdapter upgradeAdapter;

    public UpgradeViewController() {
        Injector.inject(this);

        upgradeView = (UpgradeView) inflater.inflate(R.layout.upgrade_view, null);

        upgradeAdapter = new UpgradeAdapter(upgradeView.getContext(), R.layout.upgrade_list_item);
        upgradeView.updateList.setAdapter(upgradeAdapter);
        upgradeView.updateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UpgradeListItem item = (UpgradeListItem) view;
                Feature tappedFeature = (Feature)item.getTag();

                try {
                    tappedFeature.addVoter((User)User.getCurrentUser());
                    tappedFeature.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateUI() {

    }

}
