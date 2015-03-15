package com.pauselabs.pause.controllers;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.SavesItem;
import com.pauselabs.pause.view.SavesView;
import com.pauselabs.pause.view.tabs.SavesDirectoryView;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Passa on 1/26/15.
 */
public class SavesDirectoryViewController implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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
        savesDirectoryView.savesGrid.setOnItemLongClickListener(this);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SavesItem item = (SavesItem)view.getTag();
        dbHelper.moveSave(item.getId());

        savesDirectoryArrayAdapter.resetList();

        PauseApplication.startPauseService(Constants.Session.Creator.CUSTOM);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final SavesItem item = (SavesItem)view.getTag();

        SweetAlertDialog  alert = new SweetAlertDialog(PauseApplication.pauseActivity);

        alert.setTitleText("Edit Save #" + (position + 1));
        alert.setContentText(item.getText());

        alert.setConfirmText("Default");
        alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dbHelper.setDefaultSave(item.getId());

                savesDirectoryArrayAdapter.resetList();

                sweetAlertDialog.dismissWithAnimation();
            }
        });

        if (item.getId() > 1) {
            alert.setCancelText("Delete");
            alert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dbHelper.deleteSave(item.getId());

                    savesDirectoryArrayAdapter.resetList();

                    sweetAlertDialog.dismissWithAnimation();
                }
            });
        }

        alert.show();

        return true;
    }

    private class SavesAdapter extends ArrayAdapter<SavesView> {

        public SavesAdapter(Context context, int resource) {
            super(context, resource);

            resetList();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SavesView savesView = getItem(position);

            SavesItem item = (SavesItem)savesView.getTag();
            if (dbHelper.isDefaultSave(item.getId())) {
                savesView.savesTextView.setBackgroundColor(PauseApplication.pauseActivity.getResources().getColor(R.color.gold));
            }

            return savesView;
        }

        public void resetList() {
            clear();

            Cursor saves = dbHelper.getAllSaves();

            if (saves.getCount() > 0) {
                do {
                    int id = saves.getInt(SavesDatabaseHelper.KEY_ID);
                    String text = saves.getString(SavesDatabaseHelper.KEY_TEXT);
                    SavesItem item = new SavesItem(id, text);

                    SavesView savesView = (SavesView) inflater.inflate(R.layout.saves_list_item_view, null);
                    savesView.savesTextView.setText(item.getText());
                    savesView.setTag(item);

                    add(savesView);
                } while (saves.moveToNext());
            }

            Log.i("SDVC",DatabaseUtils.dumpCursorToString(dbHelper.getAllSaves()));
        }

    }

    /**
     * Created by Passa on 3/12/15.
     */
    public static class SavesDatabaseHelper extends SQLiteOpenHelper {

        private static final int DEFAULT_FALSE = 0;
        private static final int DEFAULT_TRUE = 1;

        public static final int KEY_ID = 0;
        public static final int KEY_LIST = 1;
        public static final int KEY_TEXT = 2;
        public static final int KEY_DEFAULT = 3;
        public static final String[] KEYS = {"rowid","list","text","def"};

        private static final String DB_NAME = "SavesDB";
        private static final String DB_TABLE = "saves";
        private static final int DB_VERSION = 1;

        private final String SQL_CREATE =
                "CREATE table IF NOT EXISTS " + DB_TABLE +
                        " (" + KEYS[KEY_LIST] + " int DEFAULT 0, " + KEYS[KEY_TEXT] + " VARCHAR(255) NOT NULL, " + KEYS[KEY_DEFAULT] + " int DEFAULT 0)";
        private final String SQL_DEFAULT_ROW =
                " INSERT INTO " + DB_TABLE + " (" + KEYS[KEY_LIST] + ", " + KEYS[KEY_TEXT] + ", " + KEYS[KEY_DEFAULT] + ")" +
                        " SELECT 0, " + "'I''m way from my phone, please leave a message.'" + ", " + DEFAULT_TRUE +
                " WHERE NOT EXISTS (SELECT * FROM " + DB_TABLE +
                        " WHERE " + KEYS[KEY_ID] + " = 1)";
        private final String SQL_INCREASE_LIST = "UPDATE " + DB_TABLE + " SET " + KEYS[KEY_LIST] + " = " + KEYS[KEY_LIST] + " + 1";

        public SavesDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
            db.execSQL(SQL_DEFAULT_ROW);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public Cursor getSaveById(int id) {
            Cursor save = getReadableDatabase().query(DB_TABLE, KEYS, KEYS[KEY_ID] + " = " + id,null,null,null,null, "1");
            save.moveToFirst();

            return save;
        }
        public Cursor getSaveByList(int list) {
            Cursor save = getReadableDatabase().query(DB_TABLE, KEYS, KEYS[KEY_LIST] + " = " + list,null,null,null,null, "1");
            save.moveToFirst();

            return save;
        }
        public Cursor getDefaultSave() {
            Cursor save = getReadableDatabase().query(DB_TABLE, KEYS, KEYS[KEY_DEFAULT] + " = " + DEFAULT_TRUE,null,null,null,null,"1");
            save.moveToFirst();

            return save;
        }
        public Cursor getAllSaves() {
            Cursor saves = getReadableDatabase().query(DB_TABLE, KEYS,null,null,null,null, KEYS[KEY_LIST] + " ASC",null);
            saves.moveToFirst();

            return saves;
        }

        public void moveSave(int id) {
            int list = getSaveById(id).getInt(KEY_LIST);

            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE " + DB_TABLE + " SET " + KEYS[KEY_LIST] + " = " + KEYS[KEY_LIST] + " + 1 WHERE " + KEYS[KEY_LIST] + " < " + list);
            db.execSQL("UPDATE " + DB_TABLE + " SET " + KEYS[KEY_LIST] + " = 0 WHERE " + KEYS[KEY_ID] + " = " + id);
        }

        public void insertSave(String text) {
            ContentValues newVal = new ContentValues();
            newVal.put(KEYS[KEY_TEXT], text);

            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(SQL_INCREASE_LIST);
            db.insert(DB_TABLE, null, newVal);
        }

        public void setDefaultSave(int id) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE " + DB_TABLE + " SET " + KEYS[KEY_DEFAULT] + " = " + DEFAULT_FALSE);
            db.execSQL("UPDATE " + DB_TABLE + " SET " + KEYS[KEY_DEFAULT] + " = " + DEFAULT_TRUE + " WHERE " + KEYS[KEY_ID] + " = " + id);
        }
        public boolean isDefaultSave(int id) {
            return getSaveById(id).getInt(KEY_DEFAULT) == DEFAULT_TRUE;
        }

        public void deleteSave(int id) {
            int list = getSaveById(id).getInt(KEY_LIST);
            boolean isDefault = isDefaultSave(id);

            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE " + DB_TABLE + " SET " + KEYS[KEY_LIST] + " = " + KEYS[KEY_LIST] + " - 1 WHERE " + KEYS[KEY_LIST] + " > " + list);
            db.delete(DB_TABLE, KEYS[KEY_ID] + " = " + id, null);

            if (isDefault)
                setDefaultSave(getSaveByList(0).getInt(KEY_ID));
        }

    }
}