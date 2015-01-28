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

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public EmojiDirectoryViewController() {
        Injector.inject(this);

        emojiDirectoryView = (EmojiDirectoryView) inflater.inflate(R.layout.emoji_directory, null);
    }

    @Override
    public void onClick(View v) {

    }
}
