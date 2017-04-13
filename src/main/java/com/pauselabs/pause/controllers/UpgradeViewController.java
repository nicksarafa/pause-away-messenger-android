package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.adapters.UpgradeAdapter;
import com.pauselabs.pause.view.tabs.UpgradeView;
import javax.inject.Inject;

/** Created by Admin on 3/8/15. */
public class UpgradeViewController {

  @Inject LayoutInflater inflater;

  public UpgradeView upgradeView;

  public UpgradeAdapter upgradeAdapter;

  public UpgradeViewController() {
    Injector.inject(this);

    upgradeView = (UpgradeView) inflater.inflate(R.layout.upgrade_view, null);

    upgradeAdapter = new UpgradeAdapter(upgradeView.getContext(), R.layout.upgrade_list_item);
    upgradeView.updateList.setAdapter(upgradeAdapter);
  }

  public void updateUI() {
    upgradeAdapter.resetList();
  }
}
