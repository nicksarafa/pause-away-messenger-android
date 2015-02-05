package com.pauselabs.pause.controllers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;

/**
 * Created by Passa on 2/5/15.
 */
public class MySlider {

    private View mainView;

    private boolean isExpanded;

    public MySlider() {
        this.isExpanded = false;
    }

    public void setMainView(View v) {
        this.mainView = v;
    }

    public void expand() {
        TranslateAnimation expand = new TranslateAnimation(0, 0, mainView.getY(), 0);
        expand.setDuration(900);
        expand.setFillAfter(true);
        expand.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainView.setY(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainView.startAnimation(expand);

        this.isExpanded = true;
    }

    public void collapse() {
        TranslateAnimation collapse = new TranslateAnimation(0, 0, mainView.getY(), (float)mainView.getHeight());
        collapse.setDuration(900);
        collapse.setFillAfter(true);
        collapse.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainView.setY((float)mainView.getHeight());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainView.startAnimation(collapse);

        this.isExpanded = false;
    }

    public boolean isExpanded() { return isExpanded; }
}