package com.pauselabs.pause.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controllers.ASCIIDirectoryViewController;
import com.pauselabs.pause.controllers.CustomPauseViewController;
import com.pauselabs.pause.controllers.PrivacyViewController;
import com.pauselabs.pause.controllers.SearchPrivacyViewController;
import com.pauselabs.pause.controllers.SettingsViewController;
import com.pauselabs.pause.controllers.SummaryViewController;
import com.pauselabs.pause.controllers.UpgradeViewController;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.view.PauseActivityView;
import com.pauselabs.pause.view.tabs.actionbar.TabBarView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Locale;

import javax.inject.Inject;

public class PauseActivity extends ActionBarActivity {

    public static final String TAG = PauseActivity.class.getSimpleName();

    public static final int EMOJI_TAB = 0;
    public static final int PRIVACY_TAB = 1;
    public static final int UPGRADE_TAB = 2;
    public static final int SETTINGS_TAB = 3;

    public PauseActivityView pauseActivityView;
    public ActionBar actionBar;
    public TabBarView tabBarView;
    public int pageIndex;

    public SummaryViewController summaryViewController;
    public ASCIIDirectoryViewController ASCIIDirectoryViewController;
    public CustomPauseViewController customPauseViewController;
    public PrivacyViewController privacyViewController;
    public SearchPrivacyViewController searchPrivacyViewController;
    public UpgradeViewController upgradeViewController;
    public SettingsViewController settingsViewController;

    @Inject
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        PauseApplication.pauseActivity = this;

        pauseActivityView = (PauseActivityView) inflater.inflate(R.layout.pause_activity_view,null);
        pauseActivityView.viewPager.setAdapter(new SectionsPagerAdapter(getFragmentManager()));

        setContentView(pauseActivityView);

        summaryViewController = new SummaryViewController();
        ASCIIDirectoryViewController = new ASCIIDirectoryViewController();
        customPauseViewController = new CustomPauseViewController();
        privacyViewController = new PrivacyViewController();
        upgradeViewController = new UpgradeViewController();
        settingsViewController = new SettingsViewController();

        tabBarView = new TabBarView(this);
        tabBarView.setViewPager(pauseActivityView.viewPager);
        tabBarView.addView(privacyViewController.privacyBtns);
        privacyViewController.privacyBtns.setVisibility(View.INVISIBLE);
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
                    privacyViewController.privacyBtns.setVisibility(View.INVISIBLE);
                }
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

        // Pencil icon added via Font-Awesome to custom view EditText.. Might change to feather later

        Drawable pencilIcon = new IconDrawable(getApplicationContext(), Iconify.IconValue.fa_pencil).colorRes(R.color.text).actionBarSize();
        ASCIIDirectoryViewController.asciiDirectoryView.customText.setCompoundDrawables(pencilIcon, null, null, null);
        ASCIIDirectoryViewController.asciiDirectoryView.customText.setCompoundDrawablePadding(20);

        pauseActivityView.setDragView(pauseActivityView.startPauseButton);
        ((ButtonFloat) pauseActivityView.startPauseButton).setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_pause_off));
        pauseActivityView.startPauseButton.setBackgroundColor(getResources().getColor(R.color.on));
        pauseActivityView.setPanelHeight(0);
        pauseActivityView.setAnchorPoint(0.8894308943f);
        pauseActivityView.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float ratio) {
                actionBar.getCustomView().setY(-((ratio + (ratio * 0.121875f)) * actionBar.getHeight()));

                ButtonFloat button = (ButtonFloat) pauseActivityView.startPauseButton;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)button.getLayoutParams();
                float y = summaryViewController.summaryView.getY();
                pauseActivityView.startPauseButton.setY(y - (pauseActivityView.startPauseButton.getHeight() + lp.bottomMargin));

                Log.i(null,"Sliding");

            }

            @Override
            public void onPanelAnchored(View view) {
                PauseApplication.startPauseService(Constants.Session.Creator.VOLUME);

                ButtonFloat button = (ButtonFloat) pauseActivityView.startPauseButton;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)button.getLayoutParams();
                float y = summaryViewController.summaryView.getY();
                pauseActivityView.startPauseButton.setY(y - (pauseActivityView.startPauseButton.getHeight() + lp.bottomMargin));

                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.on));

                ((ButtonFloat) pauseActivityView.startPauseButton).setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_pause_on));
                pauseActivityView.startPauseButton.setBackgroundColor(getResources().getColor(R.color.off));

                Log.i(null,"Anchored");

            }

            @Override
            public void onPanelCollapsed(View view) {
                PauseApplication.stopPauseService(PauseApplication.getCurrentSession().getCreator());

                ButtonFloat button = (ButtonFloat) pauseActivityView.startPauseButton;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)button.getLayoutParams();
                float y = summaryViewController.summaryView.getY();
                pauseActivityView.startPauseButton.setY(y - (pauseActivityView.startPauseButton.getHeight() + (lp.bottomMargin * (14/3))));

                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.off));

                ((ButtonFloat) pauseActivityView.startPauseButton).setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_pause_off));
                pauseActivityView.startPauseButton.setBackgroundColor(getResources().getColor(R.color.on));

                Log.i(null,"Collapsed");

            }

            @Override
            public void onPanelExpanded(View view) {

                Log.i(null,"Expanded");

            }


            @Override
            public void onPanelHidden(View view) {

                Log.i(null,"Hidden");

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG,"OnStart");

        boolean from_not = getIntent().getBooleanExtra("FROM_NOT", false);
        if (from_not) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);
        }
        updateView();
    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void updateView() {
        summaryViewController.updateUI();

        if(PauseApplication.isActiveSession() && pauseActivityView.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
            pauseActivityView.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        } else if (!PauseApplication.isActiveSession() && pauseActivityView.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            pauseActivityView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
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
                R.drawable.ic_action_ab_grid_on,
                R.drawable.ic_action_ab_privacy_on,
                R.drawable.ic_action_lightbulb,
                R.drawable.ic_action_ab_settings_on
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
                case EMOJI_TAB:
                    return getString(R.string.saves_activity_title).toUpperCase(l);
                case SETTINGS_TAB:
                    return getString(R.string.settings_section_title).toUpperCase(l);
                case PRIVACY_TAB:
                    return "Privacy".toUpperCase();
                case UPGRADE_TAB:
                    return "Upgrade".toUpperCase();

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
                case EMOJI_TAB:
                    rootView = PauseApplication.pauseActivity.ASCIIDirectoryViewController.asciiDirectoryView;

                    break;

                case PRIVACY_TAB:
                    rootView = PauseApplication.pauseActivity.privacyViewController.privacyView;

                    break;

                case UPGRADE_TAB:
                    rootView = PauseApplication.pauseActivity.upgradeViewController.upgradeView;

                    break;

                case SETTINGS_TAB:
                    rootView = PauseApplication.pauseActivity.settingsViewController.settingsView;

                    break;

            }

            return rootView;
        }
    }



}