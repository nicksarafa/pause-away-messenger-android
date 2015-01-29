package com.pauselabs.pause.controllers.messages;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.view.EmojiSquareView;
import com.pauselabs.pause.view.tabs.EmojiDirectoryView;

import javax.inject.Inject;

/**
 * Created by Passa on 1/26/15.
 */
public class EmojiDirectoryViewController implements AdapterView.OnItemClickListener {

    public EmojiDirectoryView emojiDirectoryView;

    public static CustomPauseViewController customPauseViewController;

    private ArrayAdapter<EmojiSquareView> emojiDirectoryArrayAdapter;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public EmojiDirectoryViewController() {
        Injector.inject(this);

        customPauseViewController = new CustomPauseViewController();

        emojiDirectoryView = (EmojiDirectoryView) inflater.inflate(R.layout.emoji_directory, null);
        emojiDirectoryView.addView(customPauseViewController.customPauseView);

        emojiDirectoryArrayAdapter = new EmojiAdapter(emojiDirectoryView.getContext(),R.layout.emoji_square_view);
        emojiDirectoryView.emojiGrid.setAdapter(emojiDirectoryArrayAdapter);
        emojiDirectoryView.emojiGrid.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Grid", ((EmojiSquareView)view).emojiText.getText().toString());
    }

    private class EmojiAdapter extends ArrayAdapter<EmojiSquareView> {

        public EmojiAdapter(Context context, int resource) {
            super(context, resource);

            String[] colors = {
                    "Red",
                    "Magenta",
                    "Dark Grey",
                    "Grey",
                    "Green",
                    "Cyan",
                    "Indigo",
                    "Violet",
                    "Fusha",
                    "Sparkly Pink"
            };

            for (String color : colors) {
                EmojiSquareView emojiView = (EmojiSquareView) inflater.inflate(R.layout.emoji_square_view, null);
                emojiView.emojiText.setText(color);

                add(emojiView);
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EmojiSquareView emojiView = getItem(position);

            return emojiView;
        }
    }
}
