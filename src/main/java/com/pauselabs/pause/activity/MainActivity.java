package com.pauselabs.pause.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controllers.IceViewController;
import com.pauselabs.pause.controllers.messages.EmojiDirectoryViewController;
import com.pauselabs.pause.controllers.SettingsViewController;
import com.pauselabs.pause.controllers.SummaryViewController;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.view.MainActivityView;
import com.pauselabs.pause.view.tabs.actionbar.TabBarView;

import java.util.Locale;

import javax.inject.Inject;

import static android.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int EMOJI_TAB = 0;
    public static final int SETTINGS_TAB = 1;
    public static final int ICE_TAB = 2;
    public static final int SUMMARY_TAB = 3;

    private MainActivityView mainActivityView;
    private TabBarView tabBarView;
    public int pageIndex;

    public static EmojiDirectoryViewController emojiDirectoryViewController;
    public static SettingsViewController settingsViewController;
    public static SummaryViewController summaryViewController;
    public static IceViewController iceViewController;

    @Inject
    LayoutInflater inflator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        PauseApplication.mainActivity = this;

        mainActivityView = (MainActivityView) inflator.inflate(R.layout.main_activity_view,null);
        mainActivityView.viewPager.setAdapter(new SectionsPagerAdapter(getFragmentManager()));
        mainActivityView.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageIndex = position;
            }

            @Override
            public void onPageSelected(int position) {
                pageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabBarView = new TabBarView(this);
        tabBarView.setViewPager(mainActivityView.viewPager);

        getActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(tabBarView);

        emojiDirectoryViewController = new EmojiDirectoryViewController();
        settingsViewController = new SettingsViewController();
        summaryViewController = new SummaryViewController();
        iceViewController = new IceViewController();

        setContentView(mainActivityView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int intentIndex = getIntent().getIntExtra("SET_EDIT_ITEM", -1);
        if (intentIndex != -1) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);

            mainActivityView.viewPager.setCurrentItem(intentIndex);
        } else
        if (PauseApplication.isActiveSession())
            mainActivityView.viewPager.setCurrentItem(EMOJI_TAB);
        else
            mainActivityView.viewPager.setCurrentItem(pageIndex);
    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateView() {
        summaryViewController.updateUI();

        if(PauseApplication.isActiveSession()) {
            mainActivityView.viewPager.setCurrentItem(3, true);
        }
    }


    /******************************************************/
    /**                   Fragment BS                     */
    /******************************************************/

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements TabBarView.IconTabProvider {

        private int[] tab_icons = {
                R.drawable.ic_action_wake,
                R.drawable.ic_action_settings_gear,
                R.drawable.ic_action_sleep,
                R.drawable.ic_sms_icon
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
                case ICE_TAB:
                    return getString(R.string.ice_section_title).toUpperCase(l);
                case SUMMARY_TAB:
                    return getString(R.string.summary_section_title).toUpperCase(l);
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
                    rootView = emojiDirectoryViewController.emojiDirectoryView;

                    break;
                case SETTINGS_TAB:
                    rootView = settingsViewController.settingsView;

                    break;
                case ICE_TAB:
                    rootView = iceViewController.iceView;

                    break;

                case SUMMARY_TAB:
                    rootView = summaryViewController.summaryView;

                    break;
            }

            return rootView;
        }
    }
}