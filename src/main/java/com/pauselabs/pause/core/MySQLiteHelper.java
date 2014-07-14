package com.pauselabs.pause.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tyndallm on 6/30/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SAVED_PAUSES = "pauses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CREATED_ON = "created_on";
    public static final String COLUMN_PATH_TO_IMAGE = "path_to_image";

    private static final String DATABASE_NAME = "pauses.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SAVED_PAUSES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_MESSAGE
            + " text not null, " + COLUMN_CREATED_ON
            + " text not null, " + COLUMN_PATH_TO_IMAGE
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_PAUSES);
        onCreate(db);
    }
}
