package com.pauselabs.pause.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.adapters.UpgradeAdapter;
import com.pauselabs.pause.model.Parse.Feature;
import com.pauselabs.pause.model.Parse.User;
import com.pauselabs.pause.view.UpgradeListItem;
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
    upgradeView.updateList.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final UpgradeListItem item = (UpgradeListItem) view;
            final Feature tappedFeature = (Feature) item.getTag();

            ParseQuery<User> query = tappedFeature.getVotersRelation().getQuery();
            query.whereEqualTo("username", PauseApplication.parseVars.currentUser.getUsername());
            query.getFirstInBackground(
                new GetCallback<User>() {
                  @Override
                  public void done(User user, ParseException e) {
                    boolean isVoter = user != null;

                    if (isVoter) tappedFeature.removeVoter((User) User.getCurrentUser());
                    else tappedFeature.addVoter((User) User.getCurrentUser());

                    tappedFeature.saveInBackground(
                        new SaveCallback() {
                          @Override
                          public void done(ParseException e) {
                            item.setTag(tappedFeature);

                            updateUI();
                          }
                        });
                  }
                });
          }
        });
  }

  public void updateUI() {
    upgradeAdapter.resetList();
  }
}
