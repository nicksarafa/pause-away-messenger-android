package com.pauselabs.pause.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.pauselabs.pause.models.PauseBounceBackMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Datasource handles all saving and retrieving of bounce back messages to/from Database
 */
public class SavedPauseDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID, // 0
            MySQLiteHelper.COLUMN_MESSAGE, // 1
            MySQLiteHelper.COLUMN_CREATED_ON, // 2
            MySQLiteHelper.COLUMN_PATH_TO_IMAGE, // 3
            MySQLiteHelper.COLUMN_PATH_TO_ORIGINAL, // 4
            MySQLiteHelper.COLUMN_LOCATION, // 5
            MySQLiteHelper.COLUMN_DURATION}; // 6

    private final int MAX_ENTRIES = 12; // Only allow 12 saved messages at a time


    public SavedPauseDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public PauseBounceBackMessage createSavedPause(PauseBounceBackMessage pauseMessage) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MESSAGE, pauseMessage.getMessage());
        values.put(MySQLiteHelper.COLUMN_CREATED_ON, pauseMessage.getCreatedOn());
        values.put(MySQLiteHelper.COLUMN_PATH_TO_IMAGE, pauseMessage.getPathToImage());
        values.put(MySQLiteHelper.COLUMN_PATH_TO_ORIGINAL, pauseMessage.getPathToOriginal());
        values.put(MySQLiteHelper.COLUMN_LOCATION, pauseMessage.getLocation());
        values.put(MySQLiteHelper.COLUMN_DURATION, pauseMessage.getEndTime());

        long insertId = database.insert(MySQLiteHelper.TABLE_SAVED_PAUSES, null,values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SAVED_PAUSES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        Cursor countCursor = database.rawQuery("select count(*) from pauses", null);
        countCursor.moveToFirst();
        int savedCount = countCursor.getInt(0);
        countCursor.close();

        if(savedCount >= MAX_ENTRIES) {
            // remove the oldest saved bounce back
            deleteOldestSavedPauseMessage();
        }

        cursor.moveToFirst();
        PauseBounceBackMessage savedPause = cursorToPause(cursor);
        cursor.close();
        return savedPause;
    }

    public PauseBounceBackMessage getSavedPauseById(long id) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SAVED_PAUSES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        PauseBounceBackMessage savedPause = cursorToPause(cursor);
        return savedPause;
    }

    public void deleteOldestSavedPauseMessage() {
        Cursor oldestSaveCursor = database.rawQuery("SELECT * from pauses ORDER BY " + MySQLiteHelper.COLUMN_CREATED_ON + " ASC LIMIT 1", null);
        oldestSaveCursor.moveToFirst();
        PauseBounceBackMessage oldestPause = cursorToPause(oldestSaveCursor);
        deleteSavedPauseMessage(oldestPause);
        oldestSaveCursor.close();
    }

    public void deleteSavedPauseMessage(PauseBounceBackMessage savedPause) {
        long id = savedPause.getId();
        System.out.println("Bounce back deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_SAVED_PAUSES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAllSavedPauseMessages() {
        System.out.println("deleting all saved Pauses");
        database.delete(MySQLiteHelper.TABLE_SAVED_PAUSES, null, null);

    }

    public List<PauseBounceBackMessage> getAllSavedPauses() {
        List<PauseBounceBackMessage> savesPauseMessages = new ArrayList<PauseBounceBackMessage>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SAVED_PAUSES,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_CREATED_ON + " DESC"); // Order by shows newest first

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PauseBounceBackMessage savedPause = cursorToPause(cursor);
            savesPauseMessages.add(savedPause);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return savesPauseMessages;
    }

    private PauseBounceBackMessage cursorToPause(Cursor cursor) {
        String message = cursor.getString(1);
        PauseBounceBackMessage savedPause = new PauseBounceBackMessage(message, message);
        savedPause.setId(cursor.getLong(0));
        savedPause.setCreatedOn(cursor.getLong(2));
        savedPause.setPathToImage(cursor.getString(3));
        savedPause.setPathToOriginal(cursor.getString(4));
        savedPause.setLocation(cursor.getString(5));
        savedPause.setEndTime(cursor.getLong(6));
        return savedPause;
    }

}
