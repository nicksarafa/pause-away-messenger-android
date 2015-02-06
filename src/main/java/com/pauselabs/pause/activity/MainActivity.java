package com.pauselabs.pause.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controllers.ASCIIDirectoryViewController;
import com.pauselabs.pause.controllers.CustomPauseViewController;
import com.pauselabs.pause.controllers.PrivacyViewController;
import com.pauselabs.pause.controllers.SettingsViewController;
import com.pauselabs.pause.controllers.SummaryViewController;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.view.MainActivityView;
import com.pauselabs.pause.view.tabs.actionbar.TabBarView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Locale;

import javax.inject.Inject;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int EMOJI_TAB = 0;
    public static final int HIDDEN_CUSTOM = 1;
    public static final int PRIVACY_TAB = 2;
    public static final int SETTINGS_TAB = 3;


    public MainActivityView mainActivityView;
    public ActionBar actionBar;
    public TabBarView tabBarView;
    public int pageIndex;

    public SummaryViewController summaryViewController;
    public ASCIIDirectoryViewController ASCIIDirectoryViewController;
    public SettingsViewController settingsViewController;
    public CustomPauseViewController customPauseViewController;
    public PrivacyViewController privacyViewController;

    @Inject
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        PauseApplication.mainActivity = this;

        mainActivityView = (MainActivityView) inflater.inflate(R.layout.main_activity_view,null);
        mainActivityView.viewPager.setAdapter(new SectionsPagerAdapter(getFragmentManager()));

        setContentView(mainActivityView);

        summaryViewController = new SummaryViewController();
        ASCIIDirectoryViewController = new ASCIIDirectoryViewController();
        settingsViewController = new SettingsViewController();
        customPauseViewController = new CustomPauseViewController();
        privacyViewController = new PrivacyViewController();

        tabBarView = new TabBarView(this);
        tabBarView.setViewPager(mainActivityView.viewPager);
        tabBarView.addView(privacyViewController.privacyBtns);
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
        
        setSupportActionBar(mainActivityView.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.pause_green));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(tabBarView);

        mainActivityView.addView(summaryViewController.summaryView);
        summaryViewController.summaryView.setClickable(true);

        mainActivityView.startPauseButton.bringToFront();
        mainActivityView.setDragView(mainActivityView.startPauseButton);
        mainActivityView.setPanelHeight(0);
        mainActivityView.setAnchorPoint(0.8894308943f);
        mainActivityView.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float ratio) {
                actionBar.getCustomView().setY(-((ratio + (ratio * 0.121875f)) * actionBar.getHeight()));

                ButtonFloat button = (ButtonFloat) mainActivityView.startPauseButton;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)button.getLayoutParams();
                float y = summaryViewController.summaryView.getY();
                mainActivityView.startPauseButton.setY(y - (mainActivityView.startPauseButton.getHeight() + lp.bottomMargin));
            }

            @Override
            public void onPanelCollapsed(View view) {
                PauseApplication.stopPauseService(PauseApplication.getCurrentSession().getCreator());


            }

            @Override
            public void onPanelExpanded(View view) {

            }

            @Override
            public void onPanelAnchored(View view) {
                PauseApplication.startPauseService(Constants.Session.Creator.SILENCE);
            }

            @Override
            public void onPanelHidden(View view) {

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

        if(PauseApplication.isActiveSession() && mainActivityView.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
            mainActivityView.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        } else if (!PauseApplication.isActiveSession() && mainActivityView.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            mainActivityView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
                R.drawable.ic_action_ab_custom_on,
                R.drawable.ic_action_ab_privacy_on,
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
                    return getString(R.string.emoji_section_title).toUpperCase(l);
                case SETTINGS_TAB:
                    return getString(R.string.settings_section_title).toUpperCase(l);
                case PRIVACY_TAB:
                    return "Ice".toUpperCase();
                case HIDDEN_CUSTOM:
                    return getString(R.string.hidden_custom_section_title).toUpperCase(l);

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
                    rootView = PauseApplication.mainActivity.ASCIIDirectoryViewController.asciiDirectoryView;

                    break;
                case SETTINGS_TAB:
                    rootView = PauseApplication.mainActivity.settingsViewController.settingsView;

                    break;

                case HIDDEN_CUSTOM:
                    rootView = PauseApplication.mainActivity.customPauseViewController.customPauseView;

                    break;
                case PRIVACY_TAB:
                    rootView = PauseApplication.mainActivity.privacyViewController.privacyView;

                    break;
            }

            return rootView;
        }
    }
}