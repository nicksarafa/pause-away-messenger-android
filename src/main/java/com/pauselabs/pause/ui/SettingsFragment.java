package com.pauselabs.pause.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.views.SettingsButton;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

/** Temporary debugging convenience fragment */
public class SettingsFragment extends Fragment implements View.OnClickListener {

  private static final String TAG = SettingsFragment.class.getSimpleName();

  @InjectView(R.id.nameBtn)
  SettingsButton nameBtn;

  @InjectView(R.id.missedCallsBtn)
  SettingsButton missedCallsBtn;

  @InjectView(R.id.receivedSMSBtn)
  SettingsButton receivedSmsBtn;

  @InjectView(R.id.blacklistBtn)
  SettingsButton blacklistBtn;

  @InjectView(R.id.rateBtn)
  SettingsButton rateBtn;

  @InjectView(R.id.contactBtn)
  SettingsButton contactBtn;
  //    @InjectView(R.id.supportBtn)
  //    SettingsButton supportBtn;
  //    @InjectView(R.id.privacyBtn)
  //    SettingsButton privacyBtn;
  //    @InjectView(R.id.termsBtn)
  //    SettingsButton termsBtn;
  @InjectView(R.id.versionFooter)
  TextView versionFooter;

  @Inject protected SharedPreferences prefs;

  private SavedPauseDataSource mDatasource;
  private Set<String> blacklistContacts;

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.settings_fragment, null);

    // Inject Butterknife views
    Views.inject(this, view);

    mDatasource = new SavedPauseDataSource(getActivity());
    mDatasource.open();

    init();

    nameBtn.setOnClickListener(this);
    missedCallsBtn.setOnClickListener(this);
    receivedSmsBtn.setOnClickListener(this);
    rateBtn.setOnClickListener(this);
    contactBtn.setOnClickListener(this);
    blacklistBtn.setOnClickListener(this);

    PackageInfo pInfo = null;
    try {
      pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
      versionFooter.setText("Version 1.0." + pInfo.versionCode + " © 2014 Pause Labs, LLC");
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mDatasource.open();
  }

  @Override
  public void onPause() {
    mDatasource.close();
    super.onPause();
  }

  @Override
  public void onAttach(final Activity activity) {
    super.onAttach(activity);
    Injector.inject(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.nameBtn:
        Log.d(TAG, "nameBtn clicked");
        displayNameDialog();
        break;
      case R.id.missedCallsBtn:
        displayMissedCallsDialog();
        break;
      case R.id.receivedSMSBtn:
        displaySMSReplyDialog();
        break;
      case R.id.rateBtn:
        launchPlayMarketRate();
        break;
      case R.id.contactBtn:
        sendFeedbackEmail();
        break;
      case R.id.blacklistBtn:
        launchBlacklistActivity();
        break;
      default:
        // do nothing
    }
  }

  private void init() {
    nameBtn.setContent(prefs.getString(Constants.Settings.NAME, "None"));
    missedCallsBtn.setContent(
        prefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.CONTACTS_ONLY));
    receivedSmsBtn.setContent(
        prefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.CONTACTS_ONLY));
    //blacklistBtn.setContent(prefs.getString(Constants.Settings.USING_BLACKLIST, "Setup Blacklist"));

    blacklistContacts = prefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
    if (blacklistContacts.size() > 0) {
      blacklistBtn.setContent("Blacklist Active");
    } else {
      blacklistBtn.setContent("Setup Blacklist");
    }
  }

  private void displayNameDialog() {
    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

    alert.setTitle("Enter your name");
    alert.setMessage("Bounce back messages will include this");

    // Set an EditText view to get user input
    final EditText input = new EditText(getActivity());
    String existingName = prefs.getString(Constants.Settings.NAME, "");
    if (!existingName.equals("")) {
      input.setText(existingName);
      input.setSelection(input.getText().length());
    }

    alert.setView(input);

    alert.setPositiveButton(
        "Save",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            String value = input.getText().toString();
            prefs.edit().putString(Constants.Settings.NAME, value).apply();
            nameBtn.setContent(value);
          }
        });

    alert.setNegativeButton(
        "Cancel",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });

    alert.show();
  }

  private void displayMissedCallsDialog() {
    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

    alert.setTitle("Reply to missed calls");
    alert.setItems(
        R.array.reply_setting_options,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            String[] options = getResources().getStringArray(R.array.reply_setting_options);
            prefs.edit().putString(Constants.Settings.REPLY_MISSED_CALL, options[which]).apply();
            missedCallsBtn.setContent(options[which]);
          }
        });

    alert.show();
  }

  private void displaySMSReplyDialog() {
    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

    alert.setTitle("Reply to SMS messages");
    alert.setItems(
        R.array.reply_setting_options,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            String[] options = getResources().getStringArray(R.array.reply_setting_options);
            prefs.edit().putString(Constants.Settings.REPLY_SMS, options[which]).apply();
            receivedSmsBtn.setContent(options[which]);
          }
        });

    alert.show();
  }

  private void launchPlayMarketRate() {
    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
    try {
      startActivity(goToMarket);
    } catch (ActivityNotFoundException e) {
      startActivity(
          new Intent(
              Intent.ACTION_VIEW,
              Uri.parse(
                  "http://play.google.com/store/apps/details?id="
                      + getActivity().getPackageName())));
    }
  }

  private void sendFeedbackEmail() {
    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    emailIntent.putExtra(
        android.content.Intent.EXTRA_EMAIL, new String[] {"feedback@pauselabs.com"});
    emailIntent.setType("message/rfc822");
    startActivity(Intent.createChooser(emailIntent, "Contact Üs"));
  }

  /** Clear all saved pause messages */
  private void clearSaved() {
    // make sure session is not currently running
    if (PauseApplication.getCurrentSession() != null
        && PauseApplication.getCurrentSession().isActive()) {
      PauseApplication.stopPauseService();
    }
    mDatasource.deleteAllSavedPauseMessages();
  }

  private void launchBlacklistActivity() {
    Intent blacklistIntent = new Intent(getActivity(), BlacklistActivity.class);
    getActivity().startActivity(blacklistIntent);
  }
}
