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
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.tabs.SettingsView;

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

    public SettingsViewController() {
        Injector.inject(this);

        settingsView = (SettingsView) inflater.inflate(R.layout.settings_list_view, null);

        settingsView.nameBtn.setContent(prefs.getString(Constants.Settings.NAME_KEY, "None"));
        settingsView.genderBtn.setContent((prefs.getBoolean(Constants.Settings.IS_MALE, false)) ? "Male" : "Female");
        settingsView.strangersBtn.setContent((prefs.getBoolean(Constants.Settings.REPLY_STRANGERS, true)) ? "Yes" : "No");
        settingsView.missedCallsBtn.setContent((prefs.getBoolean(Constants.Settings.REPLY_MISSED_CALL, true)) ? "Yes" : "No");
        settingsView.receivedSmsBtn.setContent((prefs.getBoolean(Constants.Settings.REPLY_SMS, true)) ? "Yes" : "No");
        settingsView.silentBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY, false)) ? "Yes" : "No");
        settingsView.vibrateBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, false)) ? "Yes" : "No");
        settingsView.voiceBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, true)) ? "On" : "Off");
        settingsView.toastBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY, true)) ? "On" : "Off");

        settingsView.nameBtn.setOnClickListener(this);
        settingsView.genderBtn.setOnClickListener(this);
        settingsView.strangersBtn.setOnClickListener(this);
        settingsView.missedCallsBtn.setOnClickListener(this);
        settingsView.receivedSmsBtn.setOnClickListener(this);
        settingsView.rateBtn.setOnClickListener(this);
        settingsView.contactBtn.setOnClickListener(this);
//        settingsView.supportBtn.setOnClickListener(this);
//        settingsView.termsBtn.setOnClickListener(this);
        settingsView.silentBtn.setOnClickListener(this);
        settingsView.vibrateBtn.setOnClickListener(this);
        settingsView.voiceBtn.setOnClickListener(this);
        settingsView.toastBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.nameBtn:
                changName();
                break;
            case R.id.genderBtn:
                changeGender();
                break;
            case R.id.strangersBtn:
                changeStrangers();
                break;
            case R.id.missedCallsBtn:
                changeMissedCalls();
                break;
            case R.id.receivedSMSBtn:
                changeSMS();
                break;
            case R.id.silentBtn:
                changeSilent();
                break;
            case R.id.vibrateBtn:
                changeVibrate();
                break;
            case R.id.voiceBtn:
                changeVoice();
                break;
            case R.id.toastsBtn:
                changeToast();
                break;
//            case R.id.supportBtn:
//                launchSupportLink();
//                break;
//            case R.id.termsBtn:
//                launchTermsLink();
//                break;
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

    public void changName() {
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

    public void changeGender() {
        boolean isMale = prefs.getBoolean(Constants.Settings.IS_MALE, false);

        if (isMale) {
            settingsView.genderBtn.setContent("Female");
        } else {
            settingsView.genderBtn.setContent("Male");
        }
        prefs.edit().putBoolean(Constants.Settings.IS_MALE, !isMale).apply();
    }

    private void changeStrangers() {
        boolean replyStrangers = prefs.getBoolean(Constants.Settings.REPLY_STRANGERS, true);

        if (replyStrangers) {
            settingsView.strangersBtn.setContent("No");
        } else {
            settingsView.strangersBtn.setContent("Yes");
        }
        prefs.edit().putBoolean(Constants.Settings.REPLY_STRANGERS, !replyStrangers).apply();
    }

    public void changeMissedCalls() {
        boolean replyMissedCall = prefs.getBoolean(Constants.Settings.REPLY_MISSED_CALL, true);

        if (replyMissedCall) {
            settingsView.missedCallsBtn.setContent("No");
        } else {
            settingsView.missedCallsBtn.setContent("Yes");
        }
        prefs.edit().putBoolean(Constants.Settings.REPLY_MISSED_CALL, !replyMissedCall).apply();
    }

    public void changeSMS() {
        boolean replySMS = prefs.getBoolean(Constants.Settings.REPLY_SMS, true);

        if (replySMS) {
            settingsView.receivedSmsBtn.setContent("No");
        } else {
            settingsView.receivedSmsBtn.setContent("Yes");
        }
        prefs.edit().putBoolean(Constants.Settings.REPLY_SMS, !replySMS).apply();
    }

    public void changeSilent() {
        boolean pauseOnSilent = prefs.getBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY,false);

        if (pauseOnSilent) {
            settingsView.silentBtn.setContent("No");
        } else {
            settingsView.silentBtn.setContent("Yes");
        }
        prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY, !pauseOnSilent).apply();
    }

    public void changeVibrate() {
        boolean pauseOnVibrate = prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,false);

        if (pauseOnVibrate) {
            settingsView.vibrateBtn.setContent("No");
        } else {
            settingsView.vibrateBtn.setContent("Yes");
        }
        prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, !pauseOnVibrate).apply();
    }

    public void changeVoice() {
        boolean pauseVoiceFeedback = prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY,false);

        if (pauseVoiceFeedback) {
            settingsView.voiceBtn.setContent("Off");
        } else {
            settingsView.voiceBtn.setContent("On");
        }
        prefs.edit().putBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, !pauseVoiceFeedback).apply();
    }

    public void changeToast() {
        boolean pauseToastsOn= prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY,true);

        if (pauseToastsOn) {
            settingsView.toastBtn.setContent("Off");
        } else {
            settingsView.toastBtn.setContent("On");
        }
        prefs.edit().putBoolean(Constants.Settings.PAUSE_TOASTS_ON_KEY, !pauseToastsOn).apply();
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

}
