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
 * Created by tyndallm on 6/30/14.
 */
public class SavedPauseDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_MESSAGE, MySQLiteHelper.COLUMN_CREATED_ON, MySQLiteHelper.COLUMN_PATH_TO_IMAGE };

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
//        byte[] byteArray;
//        Bitmap pauseBitmap = pauseMessage.getImage();
//        if(pauseBitmap != null){
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            pauseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byteArray = stream.toByteArray();
//        }
//        else{
//            byteArray = new byte[0];
//        }

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MESSAGE, pauseMessage.getMessage());
        values.put(MySQLiteHelper.COLUMN_CREATED_ON, pauseMessage.getCreatedOn());
        values.put(MySQLiteHelper.COLUMN_PATH_TO_IMAGE, pauseMessage.getPathToImage());

        long insertId = database.insert(MySQLiteHelper.TABLE_SAVED_PAUSES, null,values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SAVED_PAUSES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
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

    public void deleteComment(PauseBounceBackMessage savedPause) {
        long id = savedPause.getId();
        System.out.println("Comment deleted with id: " + id);
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
                allColumns, null, null, null, null, null);

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
        // TODO Update this jankiness
        PauseBounceBackMessage savedPause = new PauseBounceBackMessage(message, message);
        savedPause.setId(cursor.getLong(0));
//        byte[] image = cursor.getBlob(2);
//
//        Bitmap bmp;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inMutable = true;
//        bmp = BitmapFactory.decodeByteArray(image, 0, image.length, options);
//
//        savedPause.setImage(bmp);
        savedPause.setCreatedOn(cursor.getLong(2));
        savedPause.setPathToImage(cursor.getString(3));
        return savedPause;
    }

}
