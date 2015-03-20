package com.pauselabs.pause.controllers;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Passa on 12/26/14.
 */
public class SettingsViewController implements View.OnClickListener {

    public SettingsView settingsView;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;
    @Inject
    AudioManager am;

    public SettingsViewController() {
        Injector.inject(this);

        settingsView = (SettingsView) inflater.inflate(R.layout.settings_list_view, null);

        settingsView.nameBtn.setContent(prefs.getString(Constants.Settings.NAME_KEY, "None"));
        settingsView.genderBtn.setContent(prefs.getString(Constants.Settings.GENDER_KEY, ""));
        settingsView.strangersBtn.setContent((prefs.getBoolean(Constants.Settings.REPLY_STRANGERS_KEY, true)) ? "Yes" : "No");
        settingsView.missedCallsBtn.setContent((prefs.getBoolean(Constants.Settings.REPLY_MISSED_CALL_KEY, true)) ? "Yes" : "No");
        settingsView.receivedSmsBtn.setContent((prefs.getBoolean(Constants.Settings.REPLY_SMS_KEY, true)) ? "Yes" : "No");
        settingsView.silentBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY, true)) ? "Yes" : "No");
        settingsView.vibrateBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, true)) ? "Yes" : "No");
        settingsView.voiceBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, false)) ? "On" : "Off");
        settingsView.toastBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_KEY, true)) ? "On" : "Off");

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
        settingsView.defaultSettingsBtn.setOnClickListener(this);
    }

    public void updateUI() {

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
            case R.id.defaultSettingsBtn:
                resetDefaultSettings();
                break;
            default:
                // do nothing
        }
    }

    public void changName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PauseApplication.pauseActivity);

        alert.setTitle("Enter your name");
        alert.setMessage("Bounce back messages will include this");

        // Set an EditText view to get user input
        final EditText input = new EditText(PauseApplication.pauseActivity);
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
        String genderSwap;

        boolean isMale = prefs.getString(Constants.Settings.GENDER_KEY, "").equals(Constants.Settings.GENDER_MALE_VALUE);

        if (isMale)
            genderSwap = Constants.Settings.GENDER_FEMALE_VALUE;
        else
            genderSwap = Constants.Settings.GENDER_MALE_VALUE;

        settingsView.genderBtn.setContent(genderSwap);
        prefs.edit().putString(Constants.Settings.GENDER_KEY, genderSwap).apply();
    }

    public void changeStrangers() {
        changeStrangers(false);
    }
    public void changeStrangers(boolean def) {
        boolean replyStrangers;

        if (def)
            replyStrangers = Constants.Settings.DEFAULT_REPLY_STRANGERS;
        else
            replyStrangers = !prefs.getBoolean(Constants.Settings.REPLY_STRANGERS_KEY, Constants.Settings.DEFAULT_REPLY_STRANGERS);

        if (replyStrangers)
            settingsView.strangersBtn.setContent("Yes");
        else
            settingsView.strangersBtn.setContent("No");

        prefs.edit().putBoolean(Constants.Settings.REPLY_STRANGERS_KEY, replyStrangers).apply();
    }

    public void changeMissedCalls() {
        changeMissedCalls(false);
    }
    public void changeMissedCalls(boolean def) {
        boolean replyMissedCall;

        if (def)
            replyMissedCall = Constants.Settings.DEFAULT_REPLY_MISSED_CALL;
        else
            replyMissedCall = !prefs.getBoolean(Constants.Settings.REPLY_MISSED_CALL_KEY, Constants.Settings.DEFAULT_REPLY_MISSED_CALL);

        if (replyMissedCall)
            settingsView.missedCallsBtn.setContent("Yes");
        else
            settingsView.missedCallsBtn.setContent("No");

        prefs.edit().putBoolean(Constants.Settings.REPLY_MISSED_CALL_KEY, replyMissedCall).apply();
    }

    public void changeSMS() {
        changeSMS(false);
    }
    public void changeSMS(boolean def) {
        boolean replySMS;

        if (def)
            replySMS = Constants.Settings.DEFAULT_REPLY_SMS;
        else
            replySMS = !prefs.getBoolean(Constants.Settings.REPLY_SMS_KEY, Constants.Settings.DEFAULT_REPLY_SMS);

        if (replySMS)
            settingsView.receivedSmsBtn.setContent("Yes");
        else
            settingsView.receivedSmsBtn.setContent("No");

        prefs.edit().putBoolean(Constants.Settings.REPLY_SMS_KEY, replySMS).apply();
    }

    public void changeSilent() {
        changeSilent(false);
    }
    public void changeSilent(boolean def) {
        boolean pauseOnSilent;

        if (def)
            pauseOnSilent = Constants.Settings.DEFAULT_PAUSE_ON_SILENT;
        else
            pauseOnSilent = !prefs.getBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY,Constants.Settings.DEFAULT_PAUSE_ON_SILENT);

        if (pauseOnSilent)
            settingsView.silentBtn.setContent("Yes");
        else
            settingsView.silentBtn.setContent("No");

        prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_SILENT_KEY, pauseOnSilent).apply();
    }

    public void changeVibrate() {
        changeVibrate(false);
    }
    public void changeVibrate(boolean def) {
        boolean pauseOnVibrate;

        if (def)
            pauseOnVibrate = Constants.Settings.DEFAULT_PAUSE_ON_VIBRATE;
        else
            pauseOnVibrate = !prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY,Constants.Settings.DEFAULT_PAUSE_ON_VIBRATE);

        if (pauseOnVibrate)
            settingsView.vibrateBtn.setContent("Yes");
        else
            settingsView.vibrateBtn.setContent("No");

        prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, pauseOnVibrate).apply();
    }

    public void changeVoice() {
        changeVoice(false);
    }
    public void changeVoice(boolean def) {
        boolean pauseVoiceFeedback;

        if (def)
            pauseVoiceFeedback = Constants.Settings.DEFAULT_PAUSE_VOICE_FEEDBACK;
        else
            pauseVoiceFeedback = !prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY,Constants.Settings.DEFAULT_PAUSE_VOICE_FEEDBACK);

        if (pauseVoiceFeedback) {
            settingsView.voiceBtn.setContent("On");

            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, AudioManager.FLAG_ALLOW_RINGER_MODES);
        } else {
            settingsView.voiceBtn.setContent("Off");

            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        prefs.edit().putBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, pauseVoiceFeedback).apply();
    }

    public void changeToast() { changeToast(false);  }

    public void changeToast(boolean def) {
        boolean pauseToastsOn;

        if (def)
            pauseToastsOn = Constants.Settings.DEFAULT_PAUSE_SHOW_TOASTS;
        else
            pauseToastsOn = !prefs.getBoolean(Constants.Settings.PAUSE_TOASTS_KEY,Constants.Settings.DEFAULT_PAUSE_SHOW_TOASTS);

        if (pauseToastsOn)
            settingsView.toastBtn.setContent("On");
        else
            settingsView.toastBtn.setContent("Off");

        prefs.edit().putBoolean(Constants.Settings.PAUSE_TOASTS_KEY, pauseToastsOn).apply();
    }

    public void launchPlayMarketRate() {
        Uri uri = Uri.parse("market://details?id=" + settingsView.getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            settingsView.getContext().startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            PauseApplication.pauseActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + settingsView.getContext().getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void sendFeedbackEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"feedback@pauselabs.com"});
        emailIntent.setType("message/rfc822");
        PauseApplication.pauseActivity.startActivity(Intent.createChooser(emailIntent, "Contact Üs").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void resetDefaultSettings() {
        SweetAlertDialog alert = new SweetAlertDialog(PauseApplication.pauseActivity);
        alert.setTitleText("Reset Settings to Default");
        alert.setContentText("Would you like to reset the settings to default?");

        alert.setConfirmText("Yes, Reset...");
        alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                changeStrangers(true);
                changeMissedCalls(true);
                changeSMS(true);
                changeSilent(true);
                changeVibrate(true);
                changeVoice(true);
                changeToast(true);

                sweetAlertDialog.dismissWithAnimation();
            }
        });

        alert.setCancelText("No! Keep my settings!");
        alert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        alert.show();
    }

    public void launchSupportLink() {
        Intent termsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.woot.com")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PauseApplication.pauseActivity.startActivity(termsIntent);
    }

    public void launchTermsLink() {
        Intent supportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PauseApplication.pauseActivity.startActivity(supportIntent);
    }

}
