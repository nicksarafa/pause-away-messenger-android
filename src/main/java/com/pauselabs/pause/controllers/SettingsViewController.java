package com.pauselabs.pause.controllers;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.BlackWhitelistActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.tabs.SettingsView;

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
    private Set<String> whitelistContacts;

    public SettingsViewController() {
        Injector.inject(this);

        settingsView = (SettingsView) inflater.inflate(R.layout.settings_view, null);

        settingsView.nameBtn.setContent(prefs.getString(Constants.Settings.NAME_KEY, "None"));
        settingsView.genderBtn.setContent((prefs.getBoolean(Constants.Settings.IS_MALE, false)) ? "Male" : "Female");
        settingsView.missedCallsBtn.setContent(prefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.EVERYBODY));
        settingsView.receivedSmsBtn.setContent(prefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.EVERYBODY));

        settingsView.blacklistBtn.setContent(prefs.getString(Constants.Settings.USING_BLACKLIST, "Setup Blacklist"));
        blacklistContacts = prefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
        if(blacklistContacts.size() > 0) {
            settingsView.blacklistBtn.setContent("Blacklist Active");
        } else {
            settingsView.blacklistBtn.setContent("Setup Blacklist");
        }
        settingsView.whitelistBtn.setContent(prefs.getString(Constants.Settings.USING_WHITELIST, "Setup Whitelist"));
        whitelistContacts = prefs.getStringSet(Constants.Settings.WHITELIST, new HashSet<String>());
        if(whitelistContacts.size() > 0) {
            settingsView.whitelistBtn.setContent("Whitelist Active");
        } else {
            settingsView.whitelistBtn.setContent("Setup Whitelist");
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
        settingsView.whitelistBtn.setOnClickListener(this);
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
                displayNameDialog();
                break;
            case R.id.genderBtn:
                displayGenderDialog();
                break;
            case R.id.missedCallsBtn:
                displayMissedCallsDialog();
                break;
            case R.id.receivedSMSBtn:
                displaySMSReplyDialog();
                break;
            case R.id.volumeBtn:
                displayVibrateDialog();
                break;
            case R.id.voiceBtn:
                displayVoiceDialog();
                break;
            case R.id.toastsBtn:
                displayToastsDialog();
                break;
            case R.id.blacklistBtn:
                launchBlacklistActivity();
                break;
            case R.id.whitelistBtn:
                launchWhitelistActivity();
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

    public void displayNameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PauseApplication.mainActivity);

        alert.setTitle("Enter your name");
        alert.setMessage("Bounce back messages will include this");

        // Set an EditText view to get user input
        final EditText input = new EditText(PauseApplication.mainActivity);
        String existingName = prefs.getString(Constants.Settings.NAME_KEY, "");
        if(!existingName.equals("")){
            input.setText(existingName);
            input.setSelection(input.getText().length());
        }

        alert.setView(input);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                prefs.edit().putString(Constants.Settings.NAME_KEY, value).apply();
                settingsView.nameBtn.setContent(value);
            }
        });

        alert.setNegativeButton("Cancel", null);

        alert.show();
    }

    public void displayGenderDialog() {
        boolean isMale = prefs.getBoolean(Constants.Settings.IS_MALE, false);

        if (isMale) {
            prefs.edit().putBoolean(Constants.Settings.IS_MALE, !isMale).apply();
            settingsView.genderBtn.setContent("Female");
        } else {
            prefs.edit().putBoolean(Constants.Settings.IS_MALE, !isMale).apply();
            settingsView.genderBtn.setContent("Male");
        }
    }

    public void displayMissedCallsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PauseApplication.mainActivity);

        alert.setTitle("Reply to missed calls");
        alert.setItems(R.array.reply_setting_options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] options = PauseApplication.mainActivity.getResources().getStringArray(R.array.reply_setting_options);
                prefs.edit().putString(Constants.Settings.REPLY_MISSED_CALL, options[which]).apply();
                settingsView.missedCallsBtn.setContent(options[which]);
            }
        });

        alert.show();
    }

    public void displaySMSReplyDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PauseApplication.mainActivity);

        alert.setTitle("Reply to SMS messages");
        alert.setItems(R.array.reply_setting_options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] options = PauseApplication.mainActivity.getResources().getStringArray(R.array.reply_setting_options);
                prefs.edit().putString(Constants.Settings.REPLY_SMS, options[which]).apply();
                settingsView.receivedSmsBtn.setContent(options[which]);
            }
        });

        alert.show();
    }

    public void displayVibrateDialog() {
        boolean pauseOnVibrate = prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,false);

        if (pauseOnVibrate) {
            prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, !pauseOnVibrate).apply();
            settingsView.volumeBtn.setContent("No");
        } else {
            prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, !pauseOnVibrate).apply();
            settingsView.volumeBtn.setContent("Yes");
        }
    }

    public void displayVoiceDialog() {
        boolean pauseVoiceFeedback = prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY,false);

        if (pauseVoiceFeedback) {
            prefs.edit().putBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, !pauseVoiceFeedback).apply();
            settingsView.voiceBtn.setContent("Off");
        } else {
            prefs.edit().putBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, !pauseVoiceFeedback).apply();
            settingsView.voiceBtn.setContent("On");
        }
    }

    public void displayToastsDialog() {
        boolean pauseToastsOn= prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY,true);

        if (pauseToastsOn) {
            prefs.edit().putBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY, !pauseToastsOn).apply();
            settingsView.toastBtn.setContent("Off");
        } else {
            prefs.edit().putBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY, !pauseToastsOn).apply();
            settingsView.toastBtn.setContent("On");
        }
    }

    private void launchPlayMarketRate() {
        Uri uri = Uri.parse("market://details?id=" + settingsView.getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            settingsView.getContext().startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            PauseApplication.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + settingsView.getContext().getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"feedback@pauselabs.com"});
        emailIntent.setType("message/rfc822");
        PauseApplication.mainActivity.startActivity(Intent.createChooser(emailIntent, "Contact Üs").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void launchSupportLink() {
        Intent termsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.woot.com")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PauseApplication.mainActivity.startActivity(termsIntent);
    }

    private void launchTermsLink() {
        Intent supportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PauseApplication.mainActivity.startActivity(supportIntent);
    }
    private void launchBlacklistActivity() {
        Intent blacklistIntent = new Intent(PauseApplication.mainActivity, BlackWhitelistActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("test",0);
        PauseApplication.mainActivity.startActivity(blacklistIntent);
    }
    private void launchWhitelistActivity() {
        Intent whitelistIntent = new Intent(PauseApplication.mainActivity, BlackWhitelistActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("test",1);
        PauseApplication.mainActivity.startActivity(whitelistIntent);
    }

}
