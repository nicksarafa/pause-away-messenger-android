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
import com.pauselabs.pause.view.ASCIISquareView;
import com.pauselabs.pause.view.tabs.ASCIIDirectoryView;

import javax.inject.Inject;

/**
 * Created by Passa on 1/26/15.
 */
public class ASCIIDirectoryViewController implements AdapterView.OnItemClickListener {

    public ASCIIDirectoryView asciiDirectoryView;

    private ArrayAdapter<ASCIISquareView> asciiDirectoryArrayAdapter;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public ASCIIDirectoryViewController() {
        Injector.inject(this);

        asciiDirectoryView = (ASCIIDirectoryView) inflater.inflate(R.layout.ascii_directory, null);

        asciiDirectoryArrayAdapter = new ASCIIAdapter(asciiDirectoryView.getContext(),R.layout.ascii_square_view);
        asciiDirectoryView.asciiGrid.setAdapter(asciiDirectoryArrayAdapter);
        asciiDirectoryView.asciiGrid.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Grid", ((ASCIISquareView) view).asciiText.getText().toString());
    }

    private class ASCIIAdapter extends ArrayAdapter<ASCIISquareView> {

        public ASCIIAdapter(Context context, int resource) {
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
                ASCIISquareView asciiView = (ASCIISquareView) inflater.inflate(R.layout.ascii_square_view, null);
                asciiView.ascii.setText("(￣(エ)￣)");
                asciiView.asciiText.setText(color);

                add(asciiView);
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ASCIISquareView emojiView = getItem(position);

            return emojiView;
        }
    }
}
