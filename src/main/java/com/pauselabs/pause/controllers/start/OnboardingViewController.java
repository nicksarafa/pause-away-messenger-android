package com.pauselabs.pause.controllers.start;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.StartActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.start.onboaring.OnboardingContainerView;
import com.pauselabs.pause.view.start.onboaring.OnboardingContentView;
import com.pauselabs.pause.view.start.onboaring.content.OB0;
import com.pauselabs.pause.view.start.onboaring.content.OB1;
import com.pauselabs.pause.view.start.onboaring.content.OB2;
import com.pauselabs.pause.view.start.onboaring.content.OB3;

import javax.inject.Inject;

/**
 * Created by Passa on 3/12/15.
 */
public class OnboardingViewController implements View.OnClickListener {

    private final String TAG = OnboardingViewController.class.getSimpleName();

    private StartActivity startActivity;

    public OnboardingContainerView onboardingContainerView;

    public static final int NUM_VIEWS = 4;
    public final OnboardingContentView[] contentViews = new OnboardingContentView[NUM_VIEWS];

    public int pageIndex;

    @Inject
    LayoutInflater inflater;
    @Inject
    SharedPreferences prefs;

    public OnboardingViewController(StartActivity activity) {
        Injector.inject(this);

        startActivity = activity;

        onboardingContainerView = (OnboardingContainerView) inflater.inflate(R.layout.ob_container_view, null);

        for (int i = 0; i < NUM_VIEWS; i++) {
            OnboardingContentView viewToAdd = null;

            switch (i) {
                case 0:
                    OB0 OB0 = (OB0)inflater.inflate(R.layout.ob_0, null);

                    viewToAdd = OB0;

                    break;
                case 1:
                    OB1 OB1 = (OB1)inflater.inflate(R.layout.ob_1, null);
                    OB1.activateBtn.setOnClickListener(this);

                    viewToAdd = OB1;

                    break;
                case 2:
                    OB2 OB2 = (OB2)inflater.inflate(R.layout.ob_2, null);
                    OB2.deactivateBtn.setOnClickListener(this);

                    viewToAdd = OB2;

                    break;
                case 3:
                    OB3 OB3 = (OB3)inflater.inflate(R.layout.ob_3, null);
                    OB3.startAppBtn.setOnClickListener(this);

                    viewToAdd = OB3;

                    break;
            }

            contentViews[i] = viewToAdd;
        }

        onboardingContainerView.viewPager.setAdapter(new SectionsPagerAdapter(startActivity.getFragmentManager()));
        onboardingContainerView.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slide1_activate_btn:
                PauseApplication.startPauseService(Constants.Session.Creator.VOLUME);

                break;
            case R.id.slide2_deactivate_btn:
                PauseApplication.stopPauseService(Constants.Session.Destroyer.VOLUME);

                break;
            case R.id.ob_final_start_main_activity_btn:
                prefs.edit().putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, true).apply();

                startActivity.startApp();

                break;
        }
    }

    /******************************************************/
    /**                     Fragment                     **/
    /******************************************************/

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return OnboardingViewController.NUM_VIEWS;
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
            return PauseApplication.startActivity.onboardingViewController.contentViews[getArguments().getInt(ARG_SECTION_NUMBER)];
        }
    }

}
