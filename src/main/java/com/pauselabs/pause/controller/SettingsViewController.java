package com.pauselabs.pause.controller;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.BlacklistActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.SettingsView;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Passa on 12/26/14.
 */
public class SettingsViewController implements View.OnClickListener {

    public SettingsView settingsView;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    private Set<String> blacklistContacts;

    public SettingsViewController() {
        Injector.inject(this);

        settingsView = (SettingsView) inflater.inflate(R.layout.settings_view, null);

        settingsView.nameBtn.setContent(prefs.getString(Constants.Settings.NAME_KEY, "None"));
        settingsView.genderBtn.setContent(prefs.getString(Constants.Settings.GENDER_KEY, "None"));
        settingsView.missedCallsBtn.setContent(prefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.EVERYBODY));
        settingsView.receivedSmsBtn.setContent(prefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.EVERYBODY));

        settingsView.blacklistBtn.setContent(prefs.getString(Constants.Settings.USING_BLACKLIST, "Setup Blacklist"));
        blacklistContacts = prefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
        if(blacklistContacts.size() > 0) {
            settingsView.blacklistBtn.setContent("Blacklist Active");
        } else {
            settingsView.blacklistBtn.setContent("Setup Blacklist");
        }

        settingsView.volumeBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, false)) ? "Yes" : "No");
        settingsView.voiceBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, true)) ? "On" : "Off");
        settingsView.toastBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY, true)) ? "On" : "Off");

        settingsView.nameBtn.setOnClickListener(this);
        settingsView.genderBtn.setOnClickListener(this);
        settingsView.missedCallsBtn.setOnClickListener(this);
        settingsView.receivedSmsBtn.setOnClickListener(this);
        settingsView.rateBtn.setOnClickListener(this);
        settingsView.contactBtn.setOnClickListener(this);
        settingsView.blacklistBtn.setOnClickListener(this);
        settingsView.supportBtn.setOnClickListener(this);
        settingsView.termsBtn.setOnClickListener(this);
        settingsView.volumeBtn.setOnClickListener(this);
        settingsView.voiceBtn.setOnClickListener(this);
        settingsView.toastBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.nameBtn:
                PauseApplication.displayNameDialog(settingsView.getContext(), settingsView.nameBtn);
                break;
            case R.id.genderBtn:
                PauseApplication.displayGenderDialog(settingsView.getContext(), settingsView.genderBtn);
                break;
            case R.id.missedCallsBtn:
                PauseApplication.displayMissedCallsDialog(settingsView.getContext(), settingsView.missedCallsBtn);
                break;
            case R.id.receivedSMSBtn:
                PauseApplication.displaySMSReplyDialog(settingsView.getContext(), settingsView.receivedSmsBtn);
                break;
            case R.id.volumeBtn:
                PauseApplication.displayVibrateDialog(settingsView.getContext(), settingsView.volumeBtn);
                break;
            case R.id.voiceBtn:
                PauseApplication.displayVoiceDialog(settingsView.getContext(), settingsView.voiceBtn);
                break;
            case R.id.toastsBtn:
                PauseApplication.displayToastsDialog(settingsView.getContext(), settingsView.toastBtn);
                break;
            case R.id.blacklistBtn:
                launchBlacklistActivity();
                break;
            case R.id.supportBtn:
                launchSupportLink();
                break;
            case R.id.termsBtn:
                launchTermsLink();
                break;
            case R.id.rateBtn:
                launchPlayMarketRate();
                break;
            case R.id.contactBtn:
                sendFeedbackEmail();
                break;
            default:
                // do nothing
        }
    }



    private void launchPlayMarketRate() {
        Uri uri = Uri.parse("market://details?id=" + settingsView.getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            settingsView.getContext().startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            settingsView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + settingsView.getContext().getPackageName())));
        }
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"feedback@pauselabs.com"});
        emailIntent.setType("message/rfc822");
        settingsView.getContext().startActivity(Intent.createChooser(emailIntent, "Contact Üs"));
    }

    private void launchSupportLink() {
        Intent termsIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.woot.com"));
        settingsView.getContext().startActivity(termsIntent);
    }

    private void launchTermsLink() {
        Intent supportIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.google.com/"));
        settingsView.getContext().startActivity(supportIntent);
    }
    private void launchBlacklistActivity() {
        Intent blacklistIntent = new Intent(settingsView.getContext(), BlacklistActivity.class);
        settingsView.getContext().startActivity(blacklistIntent);
    }

}
