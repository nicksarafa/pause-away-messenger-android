package com.pauselabs.pause.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.controller.CustomPauseViewController;
import com.pauselabs.pause.controller.EmojiDirectoryViewController;
import com.pauselabs.pause.controller.SettingsViewController;
import com.pauselabs.pause.controller.SummaryViewController;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.view.TabBarView;

import java.util.Locale;

import javax.inject.Inject;

import static android.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private TabBarView tabBarView;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;
    public int pageIndex;

    public static CustomPauseViewController customPauseViewController;
    public static EmojiDirectoryViewController emojiDirectoryViewController;
    public static SettingsViewController settingsViewController;
    public static SummaryViewController summaryViewController;

    @Inject
    LayoutInflater inflator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        PauseApplication.mainActivity = this;

        Injector.inject(this);

        customPauseViewController = new CustomPauseViewController();
        emojiDirectoryViewController = new EmojiDirectoryViewController();
        settingsViewController = new SettingsViewController();
        summaryViewController = new SummaryViewController();

        tabBarView = (TabBarView) inflator.inflate(R.layout.custom_ab, null);

        getActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(tabBarView);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new SectionsPagerAdapter(getFragmentManager()));

        tabBarView.setViewPager(mViewPager);
        tabBarView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        int intentIndex = getIntent().getIntExtra("SET_EDIT_ITEM", -1);
        if (intentIndex != -1) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);

            mViewPager.setCurrentItem(intentIndex);
        } else
        if (PauseApplication.isActiveSession())
            mViewPager.setCurrentItem(3);
        else
            mViewPager.setCurrentItem(pageIndex);
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
            mViewPager.setCurrentItem(3,true);
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
                R.drawable.ic_sms_icon,
                R.drawable.ic_action_wake,
                R.drawable.ic_action_settings_gear,
                R.drawable.ic_action_search
        };


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            return PlaceholderFragment.newInstance(position + 1);
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
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
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
                case 1:
                    rootView = customPauseViewController.customPauseView;

                    break;
                case 2:
                    rootView = emojiDirectoryViewController.emojiDirectoryView;

                    break;
                case 3:
                    rootView = settingsViewController.settingsView;

                    break;
                case 4:
                    rootView = summaryViewController.summaryView;

                    break;
            }

            return rootView;
        }
    }
}





