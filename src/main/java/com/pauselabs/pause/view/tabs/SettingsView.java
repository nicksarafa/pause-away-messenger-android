package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pauselabs.R;
import com.pauselabs.pause.view.SettingsButton;

import butterknife.InjectView;
import butterknife.Views;


public class SettingsView extends LinearLayout {

    private static final String TAG = SettingsView.class.getSimpleName();

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
    @InjectView(R.id.toastsBtn)
    public SettingsButton toastBtn;
    @InjectView(R.id.supportBtn)
    public SettingsButton supportBtn;
    @InjectView(R.id.privacyBtn)
    public SettingsButton privacyBtn;
    @InjectView(R.id.termsBtn)
    public SettingsButton termsBtn;

//    @InjectView(R.id.versionFooter)
//    TextView versionFooter;

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SettingsView(Context context) {
        super(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);

        PackageInfo pInfo = null;
//        try {
//            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            versionFooter.setText("Version 1.0." + pInfo.versionCode + " © 2014 Pause Labs, LLC");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

}
