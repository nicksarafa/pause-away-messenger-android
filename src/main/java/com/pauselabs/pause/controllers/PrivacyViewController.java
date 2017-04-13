package com.pauselabs.pause.controllers;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.SearchPrivacyActivity;
import com.pauselabs.pause.adapters.contacts.ContactsGridAdapter;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.view.tabs.PrivacyActionBtnView;
import com.pauselabs.pause.view.tabs.PrivacyView;
import javax.inject.Inject;

/** Created by Admin on 3/8/15. */
public class PrivacyViewController implements View.OnClickListener {

  private final String TAG = PrivacyViewController.class.getSimpleName();

  public PrivacyView privacyView;
  public PrivacyActionBtnView privacyBtns;

  public ContactsGridAdapter contactsGridAdapter;

  @Inject LayoutInflater inflater;

  public PrivacyViewController() {
    Injector.inject(this);

    privacyView = (PrivacyView) inflater.inflate(R.layout.privacy_list_view, null);
    privacyBtns = (PrivacyActionBtnView) inflater.inflate(R.layout.privacy_action_view, null);

    // Set visibility to hidden by default
    privacyBtns.atnBtn1.setOnClickListener(this);

    privacyView.emergencyTabBtn.setOnClickListener(this);
    privacyView.blacklistTabBtn.setOnClickListener(this);

    contactsGridAdapter = new ContactsGridAdapter(PauseApplication.pauseActivity, true);
    privacyView.contactsList.setAdapter(contactsGridAdapter);

    updateUI();
  }

  public void updateUI() {
    contactsGridAdapter.updatedContacts();
    PauseApplication.pauseActivity
        .getSupportLoaderManager()
        .restartLoader(ContactsQuery.QUERY_ID, null, contactsGridAdapter);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.privacy_search_activity_start_btn:
        Intent i = new Intent(PauseApplication.pauseActivity, SearchPrivacyActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("usingIce", contactsGridAdapter.usingIce);
        PauseApplication.pauseActivity.startActivity(i);

        break;
      case R.id.emergency_tab_btn:
        contactsGridAdapter.usingIce = true;

        updateUI();

        break;
      case R.id.blacklist_tab_btn:
        contactsGridAdapter.usingIce = false;

        updateUI();

        break;
    }
  }
}
