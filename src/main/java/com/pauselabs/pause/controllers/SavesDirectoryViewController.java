package com.pauselabs.pause.controllers;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.adapters.SavesAdapter;
import com.pauselabs.pause.core.SavesDatabaseHelper;
import com.pauselabs.pause.model.SavesItem;
import com.pauselabs.pause.view.tabs.SavesDirectoryView;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Passa on 1/26/15.
 */
public class SavesDirectoryViewController implements View.OnClickListener, AdapterView.OnItemClickListener {

    public SavesDirectoryView savesDirectoryView;

    public SavesDatabaseHelper dbHelper;
    public SavesAdapter savesDirectoryArrayAdapter;

    @Inject
    protected SharedPreferences prefs;
    @Inject
    LayoutInflater inflater;

    public SavesDirectoryViewController() {
        Injector.inject(this);

        savesDirectoryView = (SavesDirectoryView) inflater.inflate(R.layout.saves_directory, null);

        dbHelper = new SavesDatabaseHelper(PauseApplication.pauseActivity);
        savesDirectoryArrayAdapter = new SavesAdapter(savesDirectoryView.getContext(), R.layout.saves_list_item_view);

        savesDirectoryView.addCustomBtn.setOnClickListener(this);

        savesDirectoryView.savesGrid.setAdapter(savesDirectoryArrayAdapter);
        savesDirectoryView.savesGrid.setOnItemClickListener(this);
    }

    public void updateUI() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_pause_message_btn:
                dbHelper.insertSave(savesDirectoryView.customText.getText().toString());
                savesDirectoryView.customText.setText("");

                savesDirectoryArrayAdapter.resetList();

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final SavesItem item = (SavesItem)view.getTag();

        SweetAlertDialog alert = new SweetAlertDialog(PauseApplication.pauseActivity);

        alert.setCanceledOnTouchOutside(true);
        alert.setTitleText("Edit Save #" + (position + 1));
//        alert.setContentText(item.getText());
        alert.setConfirmText("Set as Default");
        alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dbHelper.setDefaultSave(item.getId());
                savesDirectoryArrayAdapter.resetList();

                sweetAlertDialog.setTitleText("Default Message Changed");
                sweetAlertDialog.setCustomImage(R.drawable.silencer_unselected_on_boarding);
                sweetAlertDialog.setContentText("Save #" + (position + 1) + " set as Default reply message!");
                sweetAlertDialog.showCancelButton(false);
                sweetAlertDialog.setConfirmText("Ok");

                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismissWithAnimation();

                    }
                });
            }
        });

        if (item.getId() > 1) {
            alert.setCancelText("Delete");
            alert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {

                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dbHelper.deleteSave(item.getId());
                    savesDirectoryArrayAdapter.resetList();
                    sweetAlertDialog.setTitleText("Complete!");
                    sweetAlertDialog.setContentText("Save #" + (position + 1) + " Deleted");
                    sweetAlertDialog.showCancelButton(false);
                    sweetAlertDialog.setConfirmText("Ok");

                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismissWithAnimation();

                        }
                    });
                }
            });
        }

        alert.show();



//        PauseApplication.startPauseService(Constants.Session.Creator.CUSTOM);
    }
}