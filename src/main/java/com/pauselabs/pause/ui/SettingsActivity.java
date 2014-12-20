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
import android.view.View;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.views.SettingsButton;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;


public class SettingsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

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
    @InjectView(R.id.genderBtn)
    SettingsButton genderBtn;
    @InjectView(R.id.volumeBtn)
    SettingsButton volumeBtn;
    @InjectView(R.id.voiceBtn)
    SettingsButton voiceBtn;
    @InjectView(R.id.supportBtn)
    SettingsButton supportBtn;
//    @InjectView(R.id.privacyBtn)
//    SettingsButton privacyBtn;
    @InjectView(R.id.termsBtn)
    SettingsButton termsBtn;
    @InjectView(R.id.versionFooter)
    TextView versionFooter;

    @Inject
    protected SharedPreferences prefs;

    private SavedPauseDataSource mDatasource;
    private Set<String> blacklistContacts;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        Views.inject(this);
        Injector.inject(this);

        mDatasource = new SavedPauseDataSource(this);
        mDatasource.open();

        init();

        nameBtn = (SettingsButton)findViewById(R.id.nameBtn);
        nameBtn.setOnClickListener(this);
        genderBtn = (SettingsButton)findViewById(R.id.genderBtn);
        genderBtn.setOnClickListener(this);
        missedCallsBtn.setOnClickListener(this);
        receivedSmsBtn.setOnClickListener(this);
        rateBtn.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
        blacklistBtn.setOnClickListener(this);
        supportBtn.setOnClickListener(this);
        termsBtn.setOnClickListener(this);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionFooter.setText("Version 1.0." + pInfo.versionCode + " © 2014 Pause Labs, LLC");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.nameBtn:
                PauseApplication.displayNameDialog(this);
                break;
            case R.id.genderBtn:
                PauseApplication.displayGenderDialog(this);
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
            case R.id.supportBtn:
                launchSupportLink();
                break;
            case R.id.termsBtn:
                launchTermsLink();
                break;
            case R.id.volumeBtn:
                displayVibrateDialog();
                break;
            case R.id.voiceBtn:
                displayVoiceDialog();
            default:
                // do nothing
        }

    }

    private void init() {
        nameBtn.setContent(prefs.getString(Constants.Settings.NAME_KEY, "None"));

        genderBtn.setContent(prefs.getString(Constants.Settings.GENDER_KEY, "None"));

        missedCallsBtn.setContent(prefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.EVERYBODY));
        receivedSmsBtn.setContent(prefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.EVERYBODY));
        //blacklistBtn.setContent(prefs.getString(Constants.Settings.USING_BLACKLIST, "Setup Blacklist"));

        blacklistContacts = prefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
        if(blacklistContacts.size() > 0) {
            blacklistBtn.setContent("Blacklist Active");
        } else {
            blacklistBtn.setContent("Setup Blacklist");
        }

    }

    private void displayMissedCallsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Reply to missed calls");
        alert.setItems(R.array.reply_setting_options, new DialogInterface.OnClickListener() {

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
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Reply to SMS messages");
        alert.setItems(R.array.reply_setting_options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] options = getResources().getStringArray(R.array.reply_setting_options);
                prefs.edit().putString(Constants.Settings.REPLY_SMS, options[which]).apply();
                receivedSmsBtn.setContent(options[which]);
            }
        });

        alert.show();
    }

    private void displayVibrateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Activate Pause on Vibrate");
        alert.setItems(R.array.volume_settings_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] options = getResources().getStringArray(R.array.volume_settings_options);
                prefs.edit().putBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, (options[which].equals("Yes"))).apply();
                volumeBtn.setContent(options[which]);
            }
        });

    }

    private void displayVoiceDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Disable Pause On/Off Voice");
        alert.setItems(R.array.voice_settings_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] options = getResources().getStringArray(R.array.voice_settings_options);
                prefs.edit().putBoolean(Constants.Settings.PAUSE_VOICE_ON_KEY, (options[which].equals("Yes"))).apply();
                voiceBtn.setContent(options[which]);
            }

        });
    }

    private void launchPlayMarketRate() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"feedback@pauselabs.com"});
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Contact Üs"));
    }

    /**
     * Clear all saved pause messages
     */
    private void clearSaved() {
        // make sure session is not currently running
        if(PauseApplication.isActiveSession()){
            PauseApplication.stopPauseService(PauseApplication.getCurrentSession().getCreator());
        }
        mDatasource.deleteAllSavedPauseMessages();
    }

    private void launchSupportLink() {
        Intent termsIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.woot.com"));
        startActivity(termsIntent);
    }

    private void launchTermsLink() {
        Intent supportIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.google.com/"));
        startActivity(supportIntent);
    }
    private void launchBlacklistActivity() {
        Intent blacklistIntent = new Intent(this, BlacklistActivity.class);
        startActivity(blacklistIntent);
    }

}
