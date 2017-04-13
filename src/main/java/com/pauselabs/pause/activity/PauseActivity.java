package com.pauselabs.pause.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gc.materialdesign.views.ButtonFloat;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controllers.PrivacyViewController;
import com.pauselabs.pause.controllers.SavesDirectoryViewController;
import com.pauselabs.pause.controllers.SettingsViewController;
import com.pauselabs.pause.controllers.SummaryViewController;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.view.PauseActivityView;
import com.pauselabs.pause.view.tabs.actionbar.TabBarView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Locale;

import javax.inject.Inject;

public class PauseActivity extends ActionBarActivity {

    public static final String TAG = PauseActivity.class.getSimpleName();

    public static final int SAVES_TAB = 0;
    public static final int PRIVACY_TAB = 1;
    public static final int SETTINGS_TAB = 2;
//    public static final int TIME_BANK_TAB = 4;
//    public static final int UPGRADE_TAB = 5;

    public PauseActivityView pauseActivityView;
    public ActionBar actionBar;
    public TabBarView tabBarView;
    public int pageIndex;

    public SummaryViewController summaryViewController;
    public SavesDirectoryViewController savesDirectoryViewController;
    public PrivacyViewController privacyViewController;
    public SettingsViewController settingsViewController;
//    public TimeBankViewController timeBankViewController;
//    public UpgradeViewController upgradeViewController;

    private SlidingUpPanelLayout mLayout;

    @Inject
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        PauseApplication.pauseActivity = this;

        pauseActivityView = (PauseActivityView) inflater.inflate(R.layout.pause_activity_view,null);

        setContentView(pauseActivityView);

        summaryViewController = new SummaryViewController();
        savesDirectoryViewController = new SavesDirectoryViewController();
        privacyViewController = new PrivacyViewController();
        settingsViewController = new SettingsViewController();
//        timeBankViewController = new TimeBankViewController();
//        upgradeViewController = new UpgradeViewController();

        pauseActivityView.viewPager.setAdapter(new SectionsPagerAdapter(getFragmentManager()));

        tabBarView = new TabBarView(this);
        tabBarView.setViewPager(pauseActivityView.viewPager);

        tabBarView.addView(privacyViewController.privacyBtns);
//        tabBarView.addView(timeBankViewController.timeBankActionBtnView);

        // Set Additional Action Bar Items to Visibility.GONE otherwise they can't be seen
//        timeBankViewController.timeBankActionBtnView.setVisibility(View.GONE);
        privacyViewController.privacyBtns.setVisibility(View.GONE);

        tabBarView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageIndex = position;
            }

            @Override
            public void onPageSelected(int position) {
                pageIndex = position;

                if (pageIndex == PRIVACY_TAB) {
                    privacyViewController.privacyBtns.setVisibility(View.VISIBLE);
                } else {
                    privacyViewController.privacyBtns.setVisibility(View.GONE);
                }

//                if (pageIndex == TIME_BANK_TAB) {
//                    timeBankViewController.timeBankActionBtnView.setVisibility(View.VISIBLE);
//
//                } else {
//                    timeBankViewController.timeBankActionBtnView.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        
        setSupportActionBar(pauseActivityView.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.off));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(tabBarView);

        pauseActivityView.addView(summaryViewController.summaryView);
        summaryViewController.summaryView.setClickable(true);

        pauseActivityView.setDragView(pauseActivityView.startPauseButton);
        pauseActivityView.startPauseButton.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_pause_off));
        pauseActivityView.startPauseButton.setBackgroundColor(getResources().getColor(R.color.on));
        pauseActivityView.setPanelHeight(0);
        pauseActivityView.setAnchorPoint(0.8894308943f);

        /**
         * Initialize sothree sliding panel
         */
        pauseActivityView.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float ratio) {
                actionBar.getCustomView().setY(-((ratio + (ratio * 0.121875f)) * actionBar.getHeight()));
                moveButton();
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
//
//            @Override
//            public void onPanelAnchored(View view) {
//                PauseApplication.startPauseService(Constants.Session.Creator.VOLUME);
//
////                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.on));
//
//                pauseActivityView.startPauseButton.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_pause_on));
//                pauseActivityView.startPauseButton.setBackgroundColor(getResources().getColor(R.color.off));
//
//                moveButton();
//            }
//
//            @Override
//            public void onPanelCollapsed(View view) {
//                PauseApplication.stopPauseService(PauseApplication.getCurrentSession().getCreator());
//
////                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.off));
//
//                pauseActivityView.startPauseButton.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_pause_off));
//                pauseActivityView.startPauseButton.setBackgroundColor(getResources().getColor(R.color.on));
//
//                moveButton();
//            }
//
//            @Override
//            public void onPanelExpanded(View view) {
//
//            }
//
//
//            @Override
//            public void onPanelHidden(View view) {
//
//            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        },1000);
    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void updateUI() {
        summaryViewController.updateUI();
        savesDirectoryViewController.updateUI();
        privacyViewController.updateUI();
        settingsViewController.updateUI();
//        timeBankViewController.updateUI();
//        upgradeViewController.updateUI();

        if(PauseApplication.isActiveSession() && pauseActivityView.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            pauseActivityView.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        } else if (!PauseApplication.isActiveSession() && pauseActivityView.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            pauseActivityView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

        moveButton();
    }

    private void moveButton() {
        ButtonFloat button = pauseActivityView.startPauseButton;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)button.getLayoutParams();
        button.setY(summaryViewController.summaryView.getY() - (button.getHeight() + lp.bottomMargin));
    }


    /******************************************************/
    /**                     Fragment                     **/
    /******************************************************/

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements TabBarView.IconTabProvider {

        private int[] tab_icons = {
                R.drawable.ic_action_ab_custom_on,
                R.drawable.ic_action_ab_privacy_on,
                R.drawable.ic_action_ab_settings_on
//                R.drawable.ic_action_wake,
//                R.drawable.ic_action_lightbulb,
        };


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return tab_icons.length;
        }

        @Override
        public int getPageIconResId(int position) {
            return tab_icons[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case SAVES_TAB:
                    return getString(R.string.saves_section_title).toUpperCase(l);
                case PRIVACY_TAB:
                    return getString(R.string.privacy_section_title).toUpperCase();
                case SETTINGS_TAB:
                    return getString(R.string.settings_section_title).toUpperCase(l);
//                case TIME_BANK_TAB:
//                    return "Time Bank".toUpperCase();
//                case UPGRADE_TAB:
//                    return getString(R.string.upgrade_section_title).toUpperCase();

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = null;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case SAVES_TAB:
                    rootView = PauseApplication.pauseActivity.savesDirectoryViewController.savesDirectoryView;

                    break;

                case PRIVACY_TAB:
                    rootView = PauseApplication.pauseActivity.privacyViewController.privacyView;

                    break;

                case SETTINGS_TAB:
                    rootView = PauseApplication.pauseActivity.settingsViewController.settingsView;

                    break;

//                case TIME_BANK_TAB:
//                    rootView = PauseApplication.pauseActivity.timeBankViewController.timeBankView;
//
//                    break;
//
//                case UPGRADE_TAB:
//                    rootView = PauseApplication.pauseActivity.upgradeViewController.upgradeView;
//
//                    break;

            }

            return rootView;
        }
    }



}