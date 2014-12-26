package com.pauselabs.pause.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controller.NoSessionViewController;
import com.pauselabs.pause.controller.SettingsViewController;
import com.pauselabs.pause.controller.SummaryViewController;
import com.pauselabs.pause.util.UIUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import javax.inject.Inject;

/**
 * Created by Sarafa on 12/8/14.
 */

public class HomeActivity extends Activity {

    public static final String TAG = HomeActivity.class.getSimpleName();

    public static NoSessionViewController noSessionViewController;
    public static SummaryViewController summaryViewController;
    public static SettingsViewController settingsViewController;

    SlidingUpPanelLayout homeView;
    LinearLayout homeContent;
    LinearLayout settingsHolder;

    @Inject
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        noSessionViewController = new NoSessionViewController();
        summaryViewController = new SummaryViewController();
        settingsViewController = new SettingsViewController();

        homeView = (SlidingUpPanelLayout) inflater.inflate(R.layout.home_view, null);
        setContentView(homeView);

        PauseApplication.homeActivity = this;

        homeContent = (LinearLayout) homeView.findViewById(R.id.home_content);
        settingsHolder = (LinearLayout) homeView.findViewById(R.id.settings_holder);
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
