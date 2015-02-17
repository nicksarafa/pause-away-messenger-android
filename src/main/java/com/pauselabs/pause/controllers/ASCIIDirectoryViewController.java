package com.pauselabs.pause.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.ASCIIItem;
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

        asciiDirectoryArrayAdapter = new ASCIIAdapter(asciiDirectoryView.getContext(), R.layout.ascii_square_view);
        asciiDirectoryView.asciiGrid.setAdapter(asciiDirectoryArrayAdapter);
        asciiDirectoryView.asciiGrid.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ASCIIItem item = (ASCIIItem)view.getTag();

        PauseApplication.mainActivity.customPauseViewController.setCustomPause(item.getAscii() + '\n' + item.getName());
    }

    private class ASCIIAdapter extends ArrayAdapter<ASCIISquareView> {

        public ASCIIAdapter(Context context, int resource) {
            super(context, resource);

            ASCIIItem[] asciis = {
                    new ASCIIItem("Out having a good time", "(　＾∇＾)"),
                    new ASCIIItem("Not sure where I am", "¯\\(°_o)/¯"),
                    new ASCIIItem("I just want to be left alone", "（￣ー￣）"),
                    new ASCIIItem("Happy Dance!!!","┏(･o･)┛♪┗ (･o･) ┓"),
                    new ASCIIItem(
                            "Dog","♫•*¨*•.¸ ¸\n" +
                            ".//^ ^ \\\\.\n" +
                            "(/(_•_) \\)•♪♫•*•.¸\n" +
                            "._/''*''\\_ღ♥* :o)))\n" +
                            "..(\")_(\") (¸.•* (¸.•*¨*•♪♫")
            };

            for (ASCIIItem item : asciis) {
                ASCIISquareView asciiView = (ASCIISquareView) inflater.inflate(R.layout.ascii_square_view, null);
//                asciiView.asciiText.setText(item.getName());
                asciiView.ascii.setText(item.getAscii());
                asciiView.setTag(item);

                add(asciiView);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ASCIISquareView asciiView = getItem(position);

            return asciiView;
        }

    }
}