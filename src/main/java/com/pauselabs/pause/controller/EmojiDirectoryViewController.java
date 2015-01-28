package com.pauselabs.pause.controller;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.TabViews.EmojiDirectoryView;

import javax.inject.Inject;

/**
 * Created by Passa on 1/26/15.
 */
public class EmojiDirectoryViewController implements View.OnClickListener {

    public EmojiDirectoryView emojiDirectoryView;

    public static CustomPauseViewController customPauseViewController;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public EmojiDirectoryViewController() {
        Injector.inject(this);

        customPauseViewController = new CustomPauseViewController();

        emojiDirectoryView = (EmojiDirectoryView) inflater.inflate(R.layout.emoji_directory, null);
        emojiDirectoryView.addView(customPauseViewController.customPauseView);
    }

    @Override
    public void onClick(View v) {

    }
}
