package com.pauselabs.pause.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controller.NoSessionViewController;
import com.pauselabs.pause.controller.SettingsViewController;
import com.pauselabs.pause.controller.SummaryViewController;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.view.TabBarView;

import javax.inject.Inject;

/**
 * Created by Sarafa on 12/8/14.
 */

public class HomeActivity extends Activity {

    public static final String TAG = HomeActivity.class.getSimpleName();

    public static NoSessionViewController noSessionViewController;
    public static SummaryViewController summaryViewController;
    public static SettingsViewController settingsViewController;
    private TabBarView tabBarView;

   // SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager viewPager;
    LinearLayout homeContent;
    LinearLayout settingsHolder;

    @Inject
    LayoutInflater inflater;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        noSessionViewController = new NoSessionViewController();
        summaryViewController = new SummaryViewController();
        settingsViewController = new SettingsViewController();

        PauseApplication.homeActivity = this;

        settingsHolder.addView(settingsViewController.settingsView);

        updateView();
    }

    public void updateView() {
        homeContent.removeAllViews();
        if (PauseApplication.isActiveSession()) {
            homeContent.addView(summaryViewController.summaryView);
            summaryViewController.updateUI();
        } else {
            homeContent.addView(noSessionViewController.noSessionView);
            noSessionViewController.updateUI();
        }
    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }
}
