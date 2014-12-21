package com.pauselabs.pause.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.views.SettingsButton;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;


public class SettingsLayout extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = SettingsLayout.class.getSimpleName();

    @InjectView(R.id.nameBtn)
    public SettingsButton nameBtn;
    @InjectView(R.id.missedCallsBtn)
    public SettingsButton missedCallsBtn;
    @InjectView(R.id.receivedSMSBtn)
    public SettingsButton receivedSmsBtn;
    @InjectView(R.id.blacklistBtn)
    public SettingsButton blacklistBtn;
    @InjectView(R.id.rateBtn)
    public SettingsButton rateBtn;
    @InjectView(R.id.contactBtn)
    public SettingsButton contactBtn;
    @InjectView(R.id.genderBtn)
    public SettingsButton genderBtn;
    @InjectView(R.id.volumeBtn)
    public SettingsButton volumeBtn;
    @InjectView(R.id.voiceBtn)
    public SettingsButton voiceBtn;
    @InjectView(R.id.supportBtn)
    public SettingsButton supportBtn;
//    @InjectView(R.id.privacyBtn)
//    SettingsButton privacyBtn;
    @InjectView(R.id.termsBtn)
    SettingsButton termsBtn;
    @InjectView(R.id.versionFooter)
    TextView versionFooter;

    @Inject
    protected SharedPreferences prefs;

    private Set<String> blacklistContacts;

    private Context context;

    public SettingsLayout(Context context) {
        super(context);

        this.context = context;
    }

    public SettingsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public SettingsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
        Injector.inject(this);

        init();

        nameBtn.setOnClickListener(this);
        genderBtn.setOnClickListener(this);
        missedCallsBtn.setOnClickListener(this);
        receivedSmsBtn.setOnClickListener(this);
        rateBtn.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
        blacklistBtn.setOnClickListener(this);
        supportBtn.setOnClickListener(this);
        termsBtn.setOnClickListener(this);
        volumeBtn.setOnClickListener(this);
        voiceBtn.setOnClickListener(this);

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionFooter.setText("Version 1.0." + pInfo.versionCode + " © 2014 Pause Labs, LLC");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.nameBtn:
                PauseApplication.displayNameDialog(context, this);
                break;
            case R.id.genderBtn:
                PauseApplication.displayGenderDialog(context, this);
                break;
            case R.id.missedCallsBtn:
                PauseApplication.displayMissedCallsDialog(context, this);
                break;
            case R.id.receivedSMSBtn:
                PauseApplication.displaySMSReplyDialog(context, this);
                break;
            case R.id.volumeBtn:
                PauseApplication.displayVibrateDialog(context, this);
                break;
            case R.id.voiceBtn:
                PauseApplication.displayVoiceDialog(context, this);
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
            default:
                // do nothing
        }

    }

    private void init() {
        nameBtn.setContent(prefs.getString(Constants.Settings.NAME_KEY, "None"));

        genderBtn.setContent(prefs.getString(Constants.Settings.GENDER_KEY, "None"));

        missedCallsBtn.setContent(prefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.EVERYBODY));
        receivedSmsBtn.setContent(prefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.EVERYBODY));

        blacklistBtn.setContent(prefs.getString(Constants.Settings.USING_BLACKLIST, "Setup Blacklist"));
        blacklistContacts = prefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
        if(blacklistContacts.size() > 0) {
            blacklistBtn.setContent("Blacklist Active");
        } else {
            blacklistBtn.setContent("Setup Blacklist");
        }

        volumeBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_ON_VIBRATE_KEY, false)) ? "Yes" : "No");
        voiceBtn.setContent((prefs.getBoolean(Constants.Settings.PAUSE_VOICE_FEEDBACK_KEY, true)) ? "On" : "Off");
    }

    private void launchPlayMarketRate() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"feedback@pauselabs.com"});
        emailIntent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(emailIntent, "Contact Üs"));
    }

    private void launchSupportLink() {
        Intent termsIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.woot.com"));
        context.startActivity(termsIntent);
    }

    private void launchTermsLink() {
        Intent supportIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.google.com/"));
        context.startActivity(supportIntent);
    }
    private void launchBlacklistActivity() {
        Intent blacklistIntent = new Intent(context, BlacklistActivity.class);
        context.startActivity(blacklistIntent);
    }

}
