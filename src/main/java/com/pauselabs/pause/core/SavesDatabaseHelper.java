package com.pauselabs.pause.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Created by Passa on 3/12/15. */
public class SavesDatabaseHelper extends SQLiteOpenHelper {

  private static final int DEFAULT_FALSE = 0;
  private static final int DEFAULT_TRUE = 1;

  public static final int KEY_ID = 0;
  public static final int KEY_LIST = 1;
  public static final int KEY_TEXT = 2;
  public static final int KEY_DEFAULT = 3;
  public static final String[] KEYS = {"rowid", "list", "text", "def"};

  private static final String DB_NAME = "SavesDB";
  private static final String DB_TABLE = "saves";
  private static final int DB_VERSION = 1;

  private final String SQL_CREATE =
      "CREATE table IF NOT EXISTS "
          + DB_TABLE
          + " ("
          + KEYS[KEY_LIST]
          + " int DEFAULT 0, "
          + KEYS[KEY_TEXT]
          + " VARCHAR(255) NOT NULL, "
          + KEYS[KEY_DEFAULT]
          + " int DEFAULT 0)";
  private final String SQL_DEFAULT_ROW =
      " INSERT INTO "
          + DB_TABLE
          + " ("
          + KEYS[KEY_LIST]
          + ", "
          + KEYS[KEY_TEXT]
          + ", "
          + KEYS[KEY_DEFAULT]
          + ")"
          + " SELECT 0, "
          + "'I am away from my phone, please leave a message.'"
          + ", "
          + DEFAULT_TRUE
          + " WHERE NOT EXISTS (SELECT * FROM "
          + DB_TABLE
          + " WHERE "
          + KEYS[KEY_ID]
          + " = 1)";
  private final String SQL_INCREASE_LIST =
      "UPDATE " + DB_TABLE + " SET " + KEYS[KEY_LIST] + " = " + KEYS[KEY_LIST] + " + 1";

  public SavesDatabaseHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE);
    db.execSQL(SQL_DEFAULT_ROW);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

  public Cursor getSaveById(int id) {
    Cursor save =
        getReadableDatabase()
            .query(DB_TABLE, KEYS, KEYS[KEY_ID] + " = " + id, null, null, null, null, "1");
    save.moveToFirst();

    return save;
  }

  public Cursor getSaveByList(int list) {
    Cursor save =
        getReadableDatabase()
            .query(DB_TABLE, KEYS, KEYS[KEY_LIST] + " = " + list, null, null, null, null, "1");
    save.moveToFirst();

    return save;
  }

  public Cursor getDefaultSave() {
    Cursor save =
        getReadableDatabase()
            .query(
                DB_TABLE,
                KEYS,
                KEYS[KEY_DEFAULT] + " = " + DEFAULT_TRUE,
                null,
                null,
                null,
                null,
                "1");
    save.moveToFirst();

    return save;
  }

  public Cursor getAllSaves() {
    Cursor saves =
        getReadableDatabase()
            .query(DB_TABLE, KEYS, null, null, null, null, KEYS[KEY_LIST] + " ASC", null);
    saves.moveToFirst();

    return saves;
  }

  public void moveSave(int id) {
    int list = getSaveById(id).getInt(KEY_LIST);

    SQLiteDatabase db = getWritableDatabase();
    db.execSQL(
        "UPDATE "
            + DB_TABLE
            + " SET "
            + KEYS[KEY_LIST]
            + " = "
            + KEYS[KEY_LIST]
            + " + 1 WHERE "
            + KEYS[KEY_LIST]
            + " < "
            + list);
    db.execSQL(
        "UPDATE "
            + DB_TABLE
            + " SET "
            + KEYS[KEY_LIST]
            + " = 0 WHERE "
            + KEYS[KEY_ID]
            + " = "
            + id);
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
    db.execSQL(
        "UPDATE "
            + DB_TABLE
            + " SET "
            + KEYS[KEY_DEFAULT]
            + " = "
            + DEFAULT_TRUE
            + " WHERE "
            + KEYS[KEY_ID]
            + " = "
            + id);
  }

  public boolean isDefaultSave(int id) {
    return getSaveById(id).getInt(KEY_DEFAULT) == DEFAULT_TRUE;
  }

  public void deleteSave(int id) {
    int list = getSaveById(id).getInt(KEY_LIST);
    boolean isDefault = isDefaultSave(id);

    SQLiteDatabase db = getWritableDatabase();
    db.execSQL(
        "UPDATE "
            + DB_TABLE
            + " SET "
            + KEYS[KEY_LIST]
            + " = "
            + KEYS[KEY_LIST]
            + " - 1 WHERE "
            + KEYS[KEY_LIST]
            + " > "
            + list);
    db.delete(DB_TABLE, KEYS[KEY_ID] + " = " + id, null);

    if (isDefault) setDefaultSave(getSaveByList(0).getInt(KEY_ID));
  }
}
